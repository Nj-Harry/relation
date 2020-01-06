package com.tzauto.javafxSupport;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The Class AbstractJavaFxApplicationSupport.
 *
 * @author Felix Roske
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractJavaFxApplicationSupport extends Application {


    private static Logger LOGGER = LoggerFactory.getLogger(AbstractJavaFxApplicationSupport.class);

    private static String[] savedArgs = new String[0];

    static Class<? extends AbstractFxmlView> savedInitialView;
    static SplashScreen splashScreen;
    private static ConfigurableApplicationContext applicationContext;


    private static List<Image> icons = new ArrayList<>();
    private final List<Image> defaultIcons = new ArrayList<>();

    private final CompletableFuture<Runnable> splashIsShowing;

    protected AbstractJavaFxApplicationSupport() {
        splashIsShowing = new CompletableFuture<>();
    }

    public static Stage getStage() {
        return GUIState.getStage();
    }

    public static Scene getScene() {
        return GUIState.getScene();
    }

    public static HostServices getAppHostServices() {
        return GUIState.getHostServices();
    }

    public static SystemTray getSystemTray() {
        return GUIState.getSystemTray();
    }

    /**
     * @param window The FxmlView derived class that should be shown.
     * @param mode   See {@code javafx.stage.Modality}.
     */
//    public static void showView(final Class<? extends AbstractFxmlView> window, final Modality mode) {
//        final AbstractFxmlView view = applicationContext.getBean(window);
//        Stage newStage = new Stage();
//
//        Scene newScene;
//        if (view.getView().getScene() != null) {
//            // This view was already shown so
//            // we have a scene for it and use this one.
//            newScene = view.getView().getScene();
//        } else {
//            newScene = new Scene(view.getView());
//        }
//
//        newStage.setScene(newScene);
//        newStage.initModality(mode);
//        newStage.initOwner(getStage());
//        newStage.setTitle(view.getDefaultTitle());
//        newStage.initStyle(view.getDefaultStyle());
//
//        newStage.showAndWait();
//    }
    public static void showView(final Class<? extends AbstractFxmlView> window, final Modality mode) {
        final AbstractFxmlView view = applicationContext.getBean(window);
        Stage newStage = null;
        if (view.getStage() == null) {
            newStage = new Stage();
            Scene newScene;
            if (view.getView().getScene() != null) {
                // This view was already shown so
                // we have a scene for it and use this one.
                newScene = view.getView().getScene();
            } else {
                newScene = new Scene(view.getView());
            }
            newStage.setScene(newScene);
            newStage.initModality(mode);
            newStage.initOwner(getStage());
            newStage.setTitle(view.getDefaultTitle());
            newStage.initStyle(view.getDefaultStyle());
            view.setStage(newStage);
            newStage.showAndWait();

        } else {
            newStage = view.getStage();
            newStage.showAndWait();
        }

    }

    public static void showView(final Class<? extends AbstractFxmlView> window, Stage initOwner, String title, StageStyle style, final Modality mode) {
        final AbstractFxmlView view = applicationContext.getBean(window);
        Stage stage = null;
        if (view.getStage() == null) {
            stage = new Stage();
            view.setStage(stage);
            Scene newScene;
            if (view.getView().getScene() != null) {
                newScene = view.getView().getScene();
            } else {
                newScene = new Scene(view.getView());
            }
            stage.setScene(newScene);
            stage.initModality(mode);
            if (initOwner == null) {
                stage.initOwner(getStage());
            } else {
                stage.initOwner(initOwner);
            }
            if (title == null) {
                stage.setTitle(view.getDefaultTitle());
            } else {
                stage.setTitle(title);
            }
            if (style == null) {
                stage.initStyle(view.getDefaultStyle());
            } else {
                stage.initStyle(style);
            }
            Image image = null;
            try {
                image = new Image(AbstractJavaFxApplicationSupport.class.getResource("/logoTaiZhi.png").toExternalForm());
            } catch (Exception e) {
                LOGGER.error("stage icon 加载失败", e);
            }
            stage.getIcons().add(image);
            stage.setResizable(false);
        } else {
            stage = view.getStage();
        }
        if (!stage.isShowing()) {
            stage.show();
        }

    }

    private void loadIcons(ConfigurableApplicationContext ctx) {
        try {
            final List<String> fsImages = PropertyReaderHelper.get(ctx.getEnvironment(), Constant.KEY_APPICONS);

            if (!fsImages.isEmpty()) {
                fsImages.forEach((s) ->
                        {
                            Image img = new Image(getClass().getResource(s).toExternalForm());
                            icons.add(img);
                        }
                );
            } else { // add factory images
                icons.addAll(defaultIcons);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load icons: ", e);
        }


    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() throws Exception {
        // Load in JavaFx Thread and reused by Completable Future, but should no be a big deal.
        defaultIcons.addAll(loadDefaultIcons());
        CompletableFuture.supplyAsync(() ->
                SpringApplication.run(this.getClass(), savedArgs)
        ).whenComplete((ctx, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to load spring application context: ", throwable);
                Platform.runLater(() -> showErrorAlert(throwable));
            } else {
                Platform.runLater(() -> {
                    loadIcons(ctx);
                    launchApplicationView(ctx);
                });
            }
        }).thenAcceptBothAsync(splashIsShowing, (ctx, closeSplash) -> {
            Platform.runLater(closeSplash);
        });
    }


    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage stage) throws Exception {

        GUIState.setStage(stage);
        GUIState.setHostServices(this.getHostServices());
        final Stage splashStage = new Stage(StageStyle.TRANSPARENT);

        if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
            final Scene splashScene = new Scene(splashScreen.getParent(), Color.TRANSPARENT);
            splashStage.setScene(splashScene);
            splashStage.getIcons().addAll(defaultIcons);
            splashStage.initStyle(StageStyle.TRANSPARENT);
            beforeShowingSplash(splashStage);
            splashStage.show();
        }

        splashIsShowing.complete(() -> {
            showInitialView();
            if (AbstractJavaFxApplicationSupport.splashScreen.visible()) {
                splashStage.hide();
                splashStage.setScene(null);
            }
        });
    }


    /**
     * Show initial view.
     */
    private void showInitialView() {
        final String stageStyle = applicationContext.getEnvironment().getProperty(Constant.KEY_STAGE_STYLE);
        if (stageStyle != null) {
            GUIState.getStage().initStyle(StageStyle.valueOf(stageStyle.toUpperCase()));
        } else {
            GUIState.getStage().initStyle(StageStyle.DECORATED);
        }

        beforeInitialView(GUIState.getStage(), applicationContext);
        showView(savedInitialView);
    }


    /**
     * Launch application view.
     */
    private void launchApplicationView(final ConfigurableApplicationContext ctx) {
        AbstractJavaFxApplicationSupport.applicationContext = ctx;

    }

    private static javafx.scene.control.Button minButton;
    private static javafx.scene.control.Button amxButton;
    private static boolean flag = true;

    private static GridPane gridPane;
    private static VBox box;
    private static VBox vBox;
    private static KeyCharacterCombination keyCombination;
    private static Scene scene;

//    static {
//        minButton = new javafx.scene.control.Button("—");
//        amxButton = new javafx.scene.control.Button("口");
//        gridPane = new GridPane();
//        box = new VBox();
//        vBox = new VBox();
//        keyCombination = new KeyCharacterCombination("Z", KeyCombination.CONTROL_DOWN);
//        scene = new Scene(vBox);
//    }

    /**
     * Show view.
     *
     * @param newView the new view
     */
    public static void showView(final Class<? extends AbstractFxmlView> newView) {
        try {
            final AbstractFxmlView view = applicationContext.getBean(newView);

            if (GUIState.getScene() == null) {
                GUIState.setScene(new Scene(view.getView()));
            } else {
                GUIState.getScene().setRoot(view.getView());
            }
            GUIState.getStage().setScene(GUIState.getScene());

            applyEnvPropsToView();
            if(view.getStage() == null){
                view.setStage(GUIState.getStage());
            }
            GUIState.getStage().getIcons().addAll(icons);
            GUIState.getStage().show();

        } catch (Throwable t) {
            LOGGER.error("Failed to load application: ", t);
            showErrorAlert(t);
        }
    }
//    public static void showView(final Class<? extends AbstractFxmlView> newView) {
//        try {
//            final AbstractFxmlView view = applicationContext.getBean(newView);
//            view.setStage(GUIState.getStage());
//
//            if (GUIState.getScene() == null) {
//                GUIState.setScene(new Scene(view.getView()));
//            } else {
//                GUIState.getScene().setRoot(view.getView());
//            }
//            GUIState.getStage().setScene(GUIState.getScene());
//
//            applyEnvPropsToView();
//
//            GUIState.getStage().getIcons().addAll(icons);
//
//
//            gridPane.setStyle("-fx-background-color: white;");
//            gridPane.setPrefHeight(32);
//            gridPane.setAlignment(Pos.CENTER_LEFT);
//            Label label = new Label("  EAPClient");
//            label.setFont(Font.font(14));
//            label.setTextFill(Paint.valueOf("BLACK"));
//            ImageView imageView = new ImageView("logoTaiZhi.png");
//            imageView.setFitHeight(24);
//            imageView.setFitWidth(24);
//            label.setGraphic(imageView);
//
//
//            GridPane root = (GridPane) view.getView();
//
//            Stage stage = view.getStage();
//            minButton.setFont(Font.font(14));
//            amxButton.setFont(Font.font(14));
//
//            minButton.setStyle("-fx-base: rgb(243,243,243);"
//                    + "-fx-max-height: infinity;-fx-text-fill: #000000 ; -fx-border-image-insets: 0;-fx-background-color: white;");
//            amxButton.setStyle("-fx-base: rgb(243,243,243); "
//                    + "-fx-max-height: infinity;-fx-text-fill: #000000 ; -fx-border-image-insets: 0;-fx-background-color: white");
//
//            minButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    minButton.setStyle("-fx-background-color: red");
//                }
//            });
//            amxButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    amxButton.setStyle("-fx-background-color: red");
//                }
//            });
//            minButton.setOnMouseExited(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    minButton.setStyle("-fx-background-color: white");
//                }
//            });
//            amxButton.setOnMouseExited(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//                    amxButton.setStyle("-fx-background-color: white");
//                }
//            });
//            minButton.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    stage.setIconified(true);
//
//                }
//            });
//            amxButton.setOnAction(new EventHandler<ActionEvent>() {
//
//                @Override
//                public void handle(ActionEvent event) {
//                    if (flag) {
//                        stage.setMaximized(flag);
//                        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//                        stage.setX(primaryScreenBounds.getMinX());
//                        stage.setY(primaryScreenBounds.getMinY());
//                        double width = primaryScreenBounds.getWidth();
//                        stage.setWidth(width);
//                        double height = primaryScreenBounds.getHeight();
//                        stage.setHeight(height);
//                        root.setPrefHeight(vBox.getHeight() - gridPane.getHeight());
//                        flag = !flag;
//                    } else {
//                        stage.setMaximized(flag);
//                        root.setPrefHeight(vBox.getHeight() - gridPane.getHeight());
//                        flag = !flag;
//                    }
//
//                }
//            });
//            GridPane.setHgrow(label, Priority.ALWAYS);
//            gridPane.addColumn(0, label);
//            gridPane.addColumn(1, minButton);
//            gridPane.addColumn(2, amxButton);
//
//
//            vBox.getChildren().addAll(box, root);
//            // 拖动监听器
//            DragUtil.addDragListener(stage, gridPane);
//            // 添加窗体拉伸效果
//            DragUtil.addDrawFunc(stage, vBox, root);
//            box.getChildren().addAll(gridPane, root);
//            stage.setTitle("EAPClient");
//
//            stage.setScene(scene);
//            stage.initStyle(StageStyle.TRANSPARENT);
//
//            stage.setFullScreenExitHint("");
//            stage.setFullScreenExitKeyCombination(keyCombination);
//
//            stage.show();
//
//
//        } catch (Throwable t) {
//            LOGGER.error("Failed to load application: ", t);
//            showErrorAlert(t);
//        }
//    }

    /**
     * Show error alert that close app.
     *
     * @param throwable cause of error
     */
    private static void showErrorAlert(Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR, "Oops! An unrecoverable error occurred.\n" +
                "Please contact your software vendor.\n\n" +
                "The application will stop now.\n\n" +
                "Error: " + throwable.getMessage());
        alert.showAndWait().ifPresent(response -> Platform.exit());
    }

    /**
     * Apply env props to view.
     */
    private static void applyEnvPropsToView() {
        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_TITLE, String.class,
                GUIState.getStage()::setTitle);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_WIDTH, Double.class,
                GUIState.getStage()::setWidth);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_HEIGHT, Double.class,
                GUIState.getStage()::setHeight);

        PropertyReaderHelper.setIfPresent(applicationContext.getEnvironment(), Constant.KEY_STAGE_RESIZABLE, Boolean.class,
                GUIState.getStage()::setResizable);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        if (applicationContext != null) {
            applicationContext.close();
        } // else: someone did it already
    }

    /**
     * Sets the title. Allows to overwrite values applied during construction at
     * a later time.
     *
     * @param title the new title
     */
    protected static void setTitle(final String title) {
        GUIState.getStage().setTitle(title);
    }

    /**
     * Launch app.
     *
     * @param appClass the app class
     * @param view     the view
     * @param args     the args
     */
    public static void launch(final Class<? extends Application> appClass,
                              final Class<? extends AbstractFxmlView> view, final String[] args) {

        launch(appClass, view, new SplashScreen(), args);
    }

    /**
     * Launch app.
     *
     * @param appClass the app class
     * @param view     the view
     * @param args     the args
     * @deprecated To be more in line with javafx.application please use launch
     */
    @Deprecated
    public static void launchApp(final Class<? extends Application> appClass,
                                 final Class<? extends AbstractFxmlView> view, final String[] args) {

        launch(appClass, view, new SplashScreen(), args);
    }

    /**
     * Launch app.
     *
     * @param appClass     the app class
     * @param view         the view
     * @param splashScreen the splash screen
     * @param args         the args
     */
    public static void launch(final Class<? extends Application> appClass,
                              final Class<? extends AbstractFxmlView> view, final SplashScreen splashScreen, final String[] args) {
        savedInitialView = view;
        savedArgs = args;

        if (splashScreen != null) {
            AbstractJavaFxApplicationSupport.splashScreen = splashScreen;
        } else {
            AbstractJavaFxApplicationSupport.splashScreen = new SplashScreen();
        }

        if (SystemTray.isSupported()) {
            GUIState.setSystemTray(SystemTray.getSystemTray());
        }

        Application.launch(appClass, args);
    }

    /**
     * Launch app.
     *
     * @param appClass     the app class
     * @param view         the view
     * @param splashScreen the splash screen
     * @param args         the args
     * @deprecated To be more in line with javafx.application please use launch
     */
    @Deprecated
    public static void launchApp(final Class<? extends Application> appClass,
                                 final Class<? extends AbstractFxmlView> view, final SplashScreen splashScreen, final String[] args) {
        launch(appClass, view, splashScreen, args);
    }

    /**
     * Gets called after full initialization of Spring application context
     * and JavaFX platform right before the initial view is shown.
     * Override this method as a hook to add special code for your app. Especially meant to
     * add AWT code to add a system tray icon and behavior by calling
     * GUIState.getSystemTray() and modifying it accordingly.
     * <p>
     * By default noop.
     *
     * @param stage can be used to customize the stage before being displayed
     * @param ctx   represents spring ctx where you can loog for beans.
     */
    public void beforeInitialView(final Stage stage, final ConfigurableApplicationContext ctx) {
    }

    public void beforeShowingSplash(Stage splashStage) {

    }

    public Collection<Image> loadDefaultIcons() {
        return Arrays.asList(
                new Image(getClass().getResource("/logoTaiZhi.png").toExternalForm()));
    }
}

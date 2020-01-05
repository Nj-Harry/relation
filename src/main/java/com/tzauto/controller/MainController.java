package com.tzauto.controller;

import com.tzauto.*;
import com.tzauto.entity.RelationEntity;
import com.tzauto.server.MainServer;
import com.tzauto.javafxSupport.FXMLController;
import com.tzauto.view.AddDataView;
import com.tzauto.view.ParmView;
import com.tzauto.view.UploadView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@FXMLController
public class MainController implements Initializable {

    public ObservableList<RelationInfo> list = FXCollections.observableArrayList();

    public static RelationEntity relationEntity;

    @FXML
    private TableView<RelationInfo> dataTable;     //tableView

    @FXML
    private TableColumn<RelationInfo, String> materialNumber = new TableColumn<>();
    @FXML
    private TableColumn<RelationInfo, String> recipeName = new TableColumn<>();
    @FXML
    private TableColumn<RelationInfo, String> fixtureno = new TableColumn<>();

    @Autowired
    MainServer mainServer;
    @Autowired
    ParmController parmController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        dataTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RelationInfo>() {//单击事件
//            @Override
//            public void changed(ObservableValue<? extends RelationInfo> observable, RelationInfo oldValue, RelationInfo newValue) {
//                System.out.println(newValue.getLot());
//            }
//        });

        dataTable.setRowFactory(tv -> {
            TableRow<RelationInfo> row = new TableRow<RelationInfo>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RelationInfo relationInfo = row.getItem();
                    relationEntity = new RelationEntity(relationInfo.getId(), relationInfo.getMaterialNumber(), relationInfo.getRecipeName(), relationInfo.getFixtureno());
                    parmController.test();
                    RelationApplication.showView(ParmView.class, null, "输入文本", null, Modality.NONE);
                }
            });
            return row;
        });
        materialNumber.setCellValueFactory(celldata -> celldata.getValue().materialNumberProperty());
        recipeName.setCellValueFactory(celldata -> celldata.getValue().recipeNameProperty());
        fixtureno.setCellValueFactory(celldata -> celldata.getValue().fixturenoProperty());

        flushData();

    }

    public void flushData() {
        dataTable.getItems().clear();
        List<RelationEntity> all = mainServer.getAll();
        for (int i = 0; i < all.size(); i++) {
            RelationEntity dataTableProperty = all.get(i);

            RelationInfo property = new RelationInfo(dataTableProperty.getMaterialNumber(),
                    dataTableProperty.getRecipeName(), dataTableProperty.getId(), dataTableProperty.getFixtureno());
            list.add(property);
        }
        dataTable.setItems(list);
    }

    public void delete(ActionEvent actionEvent) {
        if (dataTable.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }
        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
        //设置对话框标题
        alert2.setTitle("删除");
        //设置内容
        alert2.setHeaderText("确认要删除吗？");
        //显示对话框
        Optional<ButtonType> result = alert2.showAndWait();
        //如果点击OK
        if (result.get() == ButtonType.OK) {
            dataTable.getSelectionModel().getSelectedItems().forEach(x -> {
                mainServer.delete(x.getId());

            });
        }
    }

    public void add(ActionEvent actionEvent) {
        relationEntity = new RelationEntity();
        parmController.test();
        RelationApplication.showView(ParmView.class, null, "输入文本", null, Modality.NONE);

    }

    public void update(ActionEvent actionEvent) {
        if (dataTable.getSelectionModel().getSelectedItems().size() == 0) {
            return;
        }
        dataTable.getSelectionModel().getSelectedItems().forEach(x -> {

            relationEntity = new RelationEntity(x.getId(), x.getMaterialNumber(), x.getRecipeName(), x.getFixtureno());
            parmController.test();
            RelationApplication.showView(ParmView.class, null, "输入文本", null, Modality.NONE);

        });
    }
    public void addData(ActionEvent actionEvent) {
        RelationApplication.showView(AddDataView.class, null, "添加数据", null, Modality.NONE);
    }

    public void upload(ActionEvent actionEvent) {

        RelationApplication.showView(UploadView.class, null, "上传数据", null, Modality.NONE);

    }
}

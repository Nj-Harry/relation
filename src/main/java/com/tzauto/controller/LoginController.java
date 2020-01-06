package com.tzauto.controller;

import com.tzauto.javafxSupport.FXMLController;
import com.tzauto.server.MainServer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class LoginController implements Initializable {
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;
    @FXML
    private Pane ent;

    public TextField getUserName() {
        return userName;
    }

    public void setUserName(TextField userName) {
        this.userName = userName;
    }

    public PasswordField getPassword() {
        return password;
    }

    public void setPassword(PasswordField password) {
        this.password = password;
    }

    @Autowired
    MainServer mainServer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 键盘响应事件
        ent.setOnKeyPressed((KeyEvent event) ->{
            if(event.getCode()==KeyCode.ENTER)
                mainServer.login();
        });
    }

    public void loginAction(MouseEvent mouseEvent) {
        mainServer.login();

    }
}

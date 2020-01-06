package com.tzauto.controller;

import com.tzauto.javafxSupport.FXMLController;
import com.tzauto.server.MainServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class AddDataController implements Initializable {


    @FXML
    private TextField lot;
    @FXML
    private TextField isMain;

    @FXML
    private TextField item2;
    @FXML
    private TextField item4;
    @FXML
    private TextField item5;
    @FXML
    private TextField item6;

    @Autowired
    MainServer mainServer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


//    public void action(ActionEvent actionEvent) {
//
//        boolean b = mainServer.addData(lot.getText().trim(), item2.getText().trim(), item4.getText().trim(), item5.getText().trim(), item6.getText().trim(), isMain.getText().trim());
//        if (b) {
//            item2.setText("");
//            item4.setText("");
//            item5.setText("");
//            item6.setText("");
//            isMain.setText("");
//        }
//    }


}

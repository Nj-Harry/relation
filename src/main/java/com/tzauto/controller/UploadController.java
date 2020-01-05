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
public class UploadController implements Initializable {


    @FXML
    private TextField lot;

    @FXML
    private TextField endTime;

    @Autowired
    MainServer mainServer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void action(ActionEvent actionEvent) {
        lot.getText().trim();
       endTime.getText().trim();
        mainServer.upload(lot.getText().trim(),endTime.getText().trim());
    }



}

package com.tzauto.controller;
import com.tzauto.entity.RelationEntity;
import com.tzauto.javafxSupport.FXMLController;
import com.tzauto.server.MainServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import javax.annotation.Resource;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
public class QueryController implements Initializable {
    @FXML
    private TextField materialNumber;
    @Resource
    private MainServer mainServer;
    @FXML
    void queryByMaterialNumber(ActionEvent event) {
        mainServer.queryByMaterialNumber(materialNumber.getText().trim());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public TextField getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(TextField materialNumber) {
        this.materialNumber = materialNumber;
    }
}

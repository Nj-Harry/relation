package com.tzauto.controller;

import com.tzauto.*;
import com.tzauto.entity.RelationEntity;
import com.tzauto.entity.RelationVO;
import com.tzauto.server.MainServer;
import com.tzauto.javafxSupport.FXMLController;
import com.tzauto.view.AddDataView;
import com.tzauto.view.ParmView;
import com.tzauto.view.QueryView;
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

    public ObservableList<RelationVO> list = FXCollections.observableArrayList();

    public static RelationEntity relationEntity;

    @FXML
    private TableView<RelationVO> dataTable;     //tableView

    @FXML
    private TableColumn<RelationVO, String> materialNumber = new TableColumn<>();
    @FXML
    private TableColumn<RelationVO, String> fixtureno = new TableColumn<>();
    @FXML
    private TableColumn<RelationVO, String> recipeName = new TableColumn<>();
    @FXML
    private TableColumn<RelationVO, String> lastModifyTime = new TableColumn<>();
    @Autowired
    MainServer mainServer;
    @Autowired
    ParmController parmController;
    @Autowired
    QueryController queryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        dataTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RelationVO>() {//单击事件
//            @Override
//            public void changed(ObservableValue<? extends RelationVO> observable, RelationVO oldValue, RelationVO newValue) {
//                System.out.println(newValue.getLot());
//            }
//        });

        dataTable.setRowFactory(tv -> {
            TableRow<RelationVO> row = new TableRow<RelationVO>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RelationVO relationInfo = row.getItem();
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
        lastModifyTime.setCellValueFactory(celldata -> celldata.getValue().lastModifyTimeProperty());
        flushData();

    }


    public void flushData() {
        List<RelationEntity>  resultList = mainServer.getAll();
        addToTable(resultList);
    }

    public void addToTable(List<RelationEntity> resultList){
        dataTable.getItems().clear();
        for (RelationEntity relationEntity : resultList) {
            RelationVO property = new RelationVO(relationEntity.getId(), relationEntity.getMaterialNumber(),
                    relationEntity.getFixtureno(), relationEntity.getRecipeName(),relationEntity.getLastModifyTime());
            list.add(property);
        }
        dataTable.setItems(list);
    }

    public void queryByMaterialNumber() {
        RelationApplication.showView(QueryView.class, null, "查询", null, Modality.NONE);
        TextField materialNumber = queryController.getMaterialNumber();
        materialNumber.setText("");
        materialNumber.requestFocus();
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
            RelationApplication.showView(ParmView.class, null, "输入文本", null, Modality.NONE);
            parmController.test();
        });
    }
    public void addData(ActionEvent actionEvent) {
        RelationApplication.showView(AddDataView.class, null, "添加数据", null, Modality.NONE);
    }

    public void upload(ActionEvent actionEvent) {
        RelationApplication.showView(UploadView.class, null, "上传数据", null, Modality.NONE);
    }
}

package com.tzauto.server;

import com.tzauto.*;
import com.tzauto.controller.LoginController;
import com.tzauto.controller.MainController;
import com.tzauto.dao.MainMapping;
import com.tzauto.entity.LotInfo;
import com.tzauto.entity.RelationEntity;
import com.tzauto.utils.AvaryAxisUtil;
import com.tzauto.view.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/8/16.
 */
@Service
public class MainServer {

    private static final Logger logger = Logger.getLogger(MainServer.class);
    @Autowired
    MainController mainController;
    @Autowired
    MainMapping mainMapping;
    @Autowired
    ParmView parmView;
    @Autowired
    LoginView loginView;
    @Autowired
    UploadView uploadView;
    @Autowired
    AddDataView addDataView;
    @Autowired
    MainView mainView;
    @Autowired
    LoginController loginController;
    @Autowired
    QueryView queryView;

    public List<RelationEntity> getAll() {
        List<RelationEntity> all = mainMapping.getAll();
        all.stream().forEach(x -> {
            x.setRecipeName(x.getRecipeName().substring(0, x.getRecipeName().length() - 4));
        });
        return all;
    }

    public void delete(int id) {
        mainMapping.delete(id);
        mainController.flushData();
    }

    public void queryByMaterialNumber(String materialNumber){
        RelationEntity relationEntity = new RelationEntity();
        relationEntity.setMaterialNumber(materialNumber);
        List<RelationEntity> relationEntityList = mainMapping.query(relationEntity);
        mainController.addToTable(relationEntityList);
        queryView.getStage().close();
    }

    /**
     * 添加或修改提交逻辑
     * @param relationEntity
     */
    public void active(RelationEntity relationEntity) {

        if (relationEntity.getRecipeName().equals("")
                || relationEntity.getMaterialNumber().equals("") || relationEntity.getFixtureno().equals("")) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "请将内容填写完整！！！");
            return;
        }

        List<RelationEntity> relationEntityList = mainMapping.query(relationEntity);
        if (relationEntity.getId() == null) {
            if (relationEntityList.size() > 0) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "相同的料号，流程号,程序名的记录已存在，删除后可进行添加！！！");
                return;
            }
            relationEntity.setRecipeName(relationEntity.getRecipeName() + ".xml");
            mainMapping.add(relationEntity);
        } else {
            RelationEntity query = relationEntityList.get(0);
            if (query != null && (!query.getId().equals(relationEntity.getId()))) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "相同的料号，序号,流程名的记录已存在，删除后可进行修改！！！");
                return;
            }
            relationEntity.setRecipeName(relationEntity.getRecipeName() + ".xml");
            mainMapping.update(relationEntity);
        }

        parmView.getStage().close();

        mainController.flushData();
    }

    public void login() {
        String name = loginController.getUserName().getText();
        String passWord = loginController.getPassword().getText();
        if (name.equals("") || passWord.equals("")) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "请输入用户名或密码!");
            return;
        }
        name = mainMapping.queryUser(name, passWord);
        if (name == null) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "用户名或密码错误!");
            return;
        }
        loginView.getStage().close();
//        登录成功，呈现“关系维护”系欸按
        RelationApplication.showView(MainView.class, null, "关系维护", null, Modality.NONE);
    }

    public static Pattern pattern = Pattern.compile("((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|" +
            "((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|" +
            "((0[48]|[2468][048]|[3579][26])00))0229))" +
            "([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])$");

//    public static void main(String[] args) {
//        String starttime = "20191101115501";
//        String endTime = "20181101120400";
//        LocalDateTime start = LocalDateTime.of(Integer.parseInt(starttime.substring(0, 4)), Integer.parseInt(starttime.substring(4, 6)), Integer.parseInt(starttime.substring(6, 8)), Integer.parseInt(starttime.substring(8, 10)), Integer.parseInt(starttime.substring(10, 12)), Integer.parseInt(starttime.substring(12, 14)));
//        LocalDateTime end = LocalDateTime.of(Integer.parseInt(endTime.substring(0, 4)), Integer.parseInt(endTime.substring(4, 6)), Integer.parseInt(endTime.substring(6, 8)), Integer.parseInt(endTime.substring(8, 10)), Integer.parseInt(endTime.substring(10, 12)), Integer.parseInt(endTime.substring(12, 14)));
//        start = start.plusMinutes(10);
//        if (end.isBefore(start)) {
//            System.out.println("结束时间过早!");
//        }
//
//    }

}

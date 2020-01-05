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

    public void active(RelationEntity relationEntity) {

        if (relationEntity.getRecipeName().equals("")
                || relationEntity.getMaterialNumber().equals("") || relationEntity.getFixtureno().equals("")) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "请将内容填写完整！！！");
            return;
        }

        RelationEntity query = mainMapping.query(relationEntity);

        if (relationEntity.getId() == null) {
            if (query != null) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "相同的批号，料号，序号的记录已存在，删除后可进行添加！！！");
                return;
            }
            relationEntity.setRecipeName(relationEntity.getRecipeName() + ".xml");
            mainMapping.add(relationEntity);
        } else {
            if (query != null && (!query.getId().equals(relationEntity.getId()))) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "相同的批号，料号，序号的记录已存在，删除后可进行修改！！！");
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

    public void upload(String lot, String endTime) {

        if (!pattern.matcher(endTime).matches()) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "结束时间格式不正确!");
            return;
        }


        LotInfo lotInfo = mainMapping.queryLot(lot);
        if (lotInfo == null) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "没有该批次信息!");
            return;
        }

        String starttime = lotInfo.getStarttime();

        try {
            LocalDateTime start = LocalDateTime.of(Integer.parseInt(starttime.substring(0, 4)), Integer.parseInt(starttime.substring(4, 6)), Integer.parseInt(starttime.substring(6, 8)), Integer.parseInt(starttime.substring(8, 10)), Integer.parseInt(starttime.substring(10, 12)), Integer.parseInt(starttime.substring(12, 14)));
            LocalDateTime end = LocalDateTime.of(Integer.parseInt(endTime.substring(0, 4)), Integer.parseInt(endTime.substring(4, 6)), Integer.parseInt(endTime.substring(6, 8)), Integer.parseInt(endTime.substring(8, 10)), Integer.parseInt(endTime.substring(10, 12)), Integer.parseInt(endTime.substring(12, 14)));
            start = start.plusMinutes(10);
            LocalDateTime now = LocalDateTime.now();
            if (end.isBefore(start)) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "结束时间过早!  请重新输入结束时间");
                return;
            } else if (now.isBefore(end)) {
                CommonUiUtil.alert(Alert.AlertType.INFORMATION, "结束时间晚于现在时间!  请重新输入结束时间");
                return;
            }
        } catch (Exception e) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "时间解析错误!");
            return;
        }
        lotInfo.setEndTime(endTime);
        String result = null;
        try {
            result = AvaryAxisUtil.insertTable(lotInfo.getPaperNo(), "正常", lotInfo.getStarttime(), endTime, lot, lotInfo.getLayer()
                    , lotInfo.getMainSerial(), lotInfo.getPartNum(), lotInfo.getWorkNo()
                    , lotInfo.getLayer(), lotInfo.getLayerName(), lotInfo.getSerial(), lotInfo.getIsMain(), lotInfo.getOrderId()
                    , lotInfo.getRecipeName(), lotInfo.getTargetNum(), "0", lotInfo.getItem2(), lotInfo.getItem4(), lotInfo.getItem5(), lotInfo.getItem6(), lotInfo.getOpId());


        } catch (Throwable e) {
            logger.error("上传失败", e);
        }

        if ("OK".equals(result)) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "信息上传成功!");
            mainMapping.lotInfoBak(lot);
            mainMapping.deleteLot(lot);
            uploadView.getStage().close();
            StringBuilder sb = new StringBuilder();
            sb.append("用户：").append(loginController.getUserName().getText()).append(" 手动上传数据-->").append(lotInfo.toString());
            logger.info(sb.toString());
            return;
        }
        CommonUiUtil.alert(Alert.AlertType.WARNING, "信息上传失败!");
        return;


    }

    /**
     * 进行以下数据在lotinfo中的修改
     * Item2	燒焦(PNL)
     * Item4	皺褶(PNL)
     * Item5	異色(PNL)
     * Item6	其它(PNL)
     */
    public boolean addData(String lot, String item2, String item4, String item5, String item6, String isMain) {
        LotInfo lotInfo = mainMapping.queryLot(lot);
        if (lotInfo == null) {
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "没有该批次信息!");
            return false;
        }
        if (StringUtils.isEmpty(item2) && StringUtils.isEmpty(item4) && StringUtils.isEmpty(item5) && StringUtils.isEmpty(item6)) {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            //设置内容
            alert2.setHeaderText("确认要将批次:" + lot + "的数据清空吗？");
            //显示对话框
            Optional<ButtonType> result = alert2.showAndWait();
            //如果点击OK直接覆盖空值
            if (result.get() != ButtonType.OK) {
                return false;
            }
        }
        try {
            mainMapping.addData(lot, item2, item4, item5, item6, isMain);
            addDataView.getStage().close();
            return true;
        } catch (Exception e) {
            logger.error("修改数据失败", e);
            CommonUiUtil.alert(Alert.AlertType.INFORMATION, "系统出错!");

        }
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tzauto.temp;

import cn.tzauto.octopus.biz.device.domain.DeviceInfoExt;
import cn.tzauto.octopus.biz.device.domain.DeviceOplog;
import cn.tzauto.octopus.biz.device.service.DeviceService;
import cn.tzauto.octopus.common.dataAccess.base.mybatisutil.MybatisSqlSession;
import cn.tzauto.octopus.common.globalConfig.GlobalConstants;
import cn.tzauto.octopus.common.util.tool.JsonMapper;
import cn.tzauto.octopus.common.ws.AvaryAxisUtil;
import cn.tzauto.octopus.common.ws.AxisUtility;
import cn.tzauto.octopus.gui.guiUtil.UiLogUtil;
import cn.tzauto.octopus.isecsLayer.domain.EquipModel;
import cn.tzauto.octopus.isecsLayer.domain.ISecsHost;
import cn.tzauto.octopus.secsLayer.util.NormalConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author luosy
 */
public class EquipStatusHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(EquipStatusHandler.class);
    static String equipstatus = "--";
    static String preEquipstatus = "--";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String msgCtx = msg.toString();
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];

        buf.readBytes(req);

        String message = new String(req, "UTF-8");
        if (message.contains("done")) {

            String[] messages = message.replaceAll("alert", "").replaceAll("done", "").split(";");
            message = messages[0].trim();

            Map<String, String> map = new HashMap();
            for (Map.Entry<String, EquipModel> equipmodelEntry : GlobalConstants.stage.equipModels.entrySet()) {
                EquipModel equipModel = equipmodelEntry.getValue();
                ConcurrentLinkedQueue<ISecsHost> isecsHosts = equipModel.iSecsHostList;
                for (ISecsHost isecsHost : isecsHosts) {
                    map.put(isecsHost.ip, isecsHost.deviceCode);
                }
            }
            String eqpIp = ctx.channel().remoteAddress().toString().split(":")[0].replaceAll("/", "");
            String deviceCode = map.get(eqpIp);
            if (deviceCode.contains("-S")) {
                deviceCode = deviceCode.replace("-S", "");
            }
            String status = transferStatus(messages[0]);
            String prestatus = "";
            if (messages.length > 1) {
                prestatus = transferStatus(messages[1]);
            }
            MDC.put(NormalConstant.WHICH_EQUIPHOST_CONTEXT, deviceCode);
            logger.debug("接收到alert信息:" + message);
            logger.debug("设备:" + deviceCode + "设备进入" + status + "状态.");
            //UiLogUtil.getInstance().appendLog2EventTab(deviceCode, "设备进入" + status + "状态...");
            Map statusmap = new HashMap();
            statusmap.put("EquipStatus", status);
            EquipModel equipModel = GlobalConstants.stage.equipModels.get(deviceCode);
            if (equipModel != null) {
                if (equipModel.deviceType.contains("HITACHI-LASERDRILL")) {
//                    prestatus = equipModel.equipStatus;
//                    Thread.sleep(1000);
//                    status = equipModel.getEquipStatus();
                }
                equipModel.changeEquipPanel(statusmap);
                if (equipModel.deviceType.equals("PLASMA")) {
                    if ("s1".equals(message)) {
                        //开始读取数据
                        //开始读取数据，并记录
//                        equipModel.iSecsHost.executeCommand();
                        List<String> list =new ArrayList<>();
                        for(int i =0;i<1;i++){
                            String value = equipModel.iSecsHost.executeCommand("read "+i).get(0);
                            list.add(value);
                        }
                        equipModel.productNum = list.get(0);//初始化数量
                        LocalDateTime now = LocalDateTime.now();
                        equipModel.lotStartTime = now.format(AvaryAxisUtil.dtfyyyyMMddHHmmss);//批次开始时间
                    } else if ("f1".equals(message)) {
                        //一次料做完，进行数据上传,从START时读取到的数据，以及从webservice调用获取到的需要的数据进行上传
                        try {
                            if("".equals(equipModel.opId)&&"".equals(equipModel.productNum)&&"".equals(equipModel.materialNumber)){
                                logger.info("没有记录开始状态，有可能刚开机");


                            }
                            equipModel.uploadData(null);
                        } catch (Exception e) {
                            logger.error("上传数据失败",e);
                            logger.error("出错时数据为："+AvaryAxisUtil.createParm(equipModel.productNum,equipModel.lotStartTime,equipModel.lotId,
                                    equipModel.materialNumber,equipModel.opId,equipModel.recipeName));
                        } finally {
                            //一批结束之后进行数据重置
                            equipModel.productNum = "";
                            equipModel.lotStartTime = "";

                            equipModel.lotId = "";
                            equipModel.materialNumber = "";
                            equipModel.opId = "";
                            equipModel.recipeName = "";
                        }
                    }

                    return;
                }
                equipModel.preEquipStatus = prestatus.trim();
                equipModel.equipStatus = status.trim();
                preEquipstatus = prestatus.trim();
                equipstatus = status.trim();
                logger.debug("设备:" + deviceCode + " preEquipstatus " + preEquipstatus + " equipstatus" + equipstatus);
                if ("pause".equalsIgnoreCase(preEquipstatus) && "RUN".equalsIgnoreCase(equipstatus)) {
                    boolean businessmode = false;
                    if ("1".equals(GlobalConstants.getProperty("START_CHECK_BUSINESSMODE"))) {
                        businessmode = AxisUtility.checkBusinessMode(deviceCode);
                    }
                    if (!businessmode) {
                        if ("1".equals(GlobalConstants.getProperty("START_CHECK_LOCKFLAG"))) {
                            if (equipModel.checkLockFlagFromServerByWS(deviceCode)) {
                                String stopResult = equipModel.pauseEquip();
                                UiLogUtil.getInstance().appendLog2SeverTab(deviceCode, "检测到设备被Server要求锁机,设备将被锁!");
                            }
                        }
                    }

                }
                if ("run".equalsIgnoreCase(preEquipstatus) && "run".equalsIgnoreCase(equipstatus)) {
                    if (equipModel.deviceType.equals("SCREEN-LEDIA")) {
                        equipModel.getSpecificData(null);
                    }
                }
                if ("run".equalsIgnoreCase(preEquipstatus) && equipstatus.equalsIgnoreCase("idle")) {
//                    if (equipModel.deviceType.contains("HITACHI-LASERDRILL")) {
//                        equipModel.uploadData("生產");
//                    }
                }
                if ((preEquipstatus.contains("eady") || (preEquipstatus.contains("dle"))) && "RUN".equalsIgnoreCase(equipstatus)) {
//                    if (equipModel.deviceType.contains("HITACHI-LASERDRILL")) {
//                        if (needCare(equipModel)) {
//                            equipModel.uploadData("待料");
//                            LocalDateTime now = LocalDateTime.now();
//                            equipModel.idleStartTime = now.format(AvaryAxisUtil.dtfyyyyMMddHHmmss);
//                        }
//                    }
                    logger.info("设备:" + deviceCode + "开机作业.");
                    UiLogUtil.getInstance().appendLog2EventTab(deviceCode, "设备进入运行状态...");
                    boolean businessmode = false;
                    if ("1".equals(GlobalConstants.getProperty("START_CHECK_BUSINESSMODE"))) {
                        businessmode = AxisUtility.checkBusinessMode(deviceCode);
                    }
                    if (!businessmode) {
                        if ("1".equals(GlobalConstants.getProperty("START_CHECK"))) {
                            if (!GlobalConstants.stage.equipModels.get(deviceCode).startCheck()) {
                                String stopResult = GlobalConstants.stage.equipModels.get(deviceCode).stopEquip();
                                UiLogUtil.getInstance().appendLog2EventTab(deviceCode, "设备将被锁机...");
                                String holdDesc = "";
                                Map mqMap = new HashMap();
                                if ("0".equals(stopResult)) {
                                    holdDesc = "当前设备已经被锁机";
                                    Map mapTmp = new HashMap();
                                    mapTmp.put("EquipStatus", "Idle");
                                    equipModel.changeEquipPanel(mapTmp);
                                    UiLogUtil.getInstance().appendLog2EventTab(deviceCode, "锁机成功...");
                                    mqMap.put("holdResult", "锁机成功");
                                } else {
                                    mqMap.put("holdResult", "锁机失败");
                                    holdDesc = stopResult;
                                }
                                mqMap.put("holdDesc", holdDesc);
                                GlobalConstants.C2SEqptLogQueue.sendMessage(mqMap);
                            }
                        }
                    }

                }
                sendDeviceInfoExtAndOplog2Server(equipModel, preEquipstatus);
            }
        }
        //System.out.println("Netty-Server:Receive Message:" + message);
//        System.out.println("cn.tzinfo.htauto.octopus.isecsLayer.socket.TimeServerHandler.channelRead()" + msgCtx);
    }

    private static String transferStatus(String status) {
        if (status.contains("START") || status.contains("tart") || status.contains("WORK") || status.contains("ork") || status.contains("un")) {
            status = "Run";
        }
        if (status.contains("IDLE") || status.contains("dle")) {
            status = "Idle";
        }
        if (status.contains("READY") || status.contains("eady")) {
            status = "Ready";
        }
        if (status.contains("STOP") || status.contains("top")) {
            status = "Stop";
        }
        if (status.contains("ause")) {
            status = "Pause";
        }
        return status;
    }

    private static void sendDeviceInfoExtAndOplog2Server(EquipModel equipModel, String preEquipstatus) {
        if ("1".equals(GlobalConstants.getProperty("REPORT_EQUIPSTATUS"))) {
            SqlSession sqlSession = MybatisSqlSession.getSqlSession();
            DeviceService deviceService = new DeviceService(sqlSession);
            DeviceInfoExt deviceInfoExt = deviceService.getDeviceInfoExtByDeviceCode(equipModel.deviceCode);
            if (deviceInfoExt == null) {
                logger.error("未配置:" + equipModel.deviceCode + "的模型信息.");
                return;
            }
            deviceInfoExt.setDeviceStatus(equipModel.equipStatus);
            DeviceOplog deviceOplog = new DeviceOplog();
            List<DeviceOplog> deviceOplogList = deviceService.getDeviceOplog(equipModel.deviceCode);
            if (deviceOplogList == null || deviceOplogList.isEmpty()) {
                deviceOplog = equipModel.setDeviceOplog(preEquipstatus, deviceInfoExt.getLotId());
                //deviceService.saveDeviceOplog(deviceOplog);
            } else {
                String formerDeviceStatus = deviceOplogList.get(0).getCurrDeviceStatus();
                if (!formerDeviceStatus.equals(equipModel.equipStatus)) {
                    deviceOplog = equipModel.setDeviceOplog(preEquipstatus, deviceInfoExt.getLotId());
                    //deviceService.saveDeviceOplog(deviceOplog);
                }
            }
            sqlSession.close();
            if (!GlobalConstants.isLocalMode) {
                Map mqMap = new HashMap();
                mqMap.put("msgName", "eqpt.EqptStatusChange");
                mqMap.put("deviceCode", equipModel.deviceCode);
                mqMap.put("eventName", "eqpt.EqptStatusChange");
                mqMap.put("deviceInfoExt", JsonMapper.toJsonString(deviceInfoExt));
                mqMap.put("deviceCeid", "0");
                mqMap.put("eventDesc", deviceOplog.getOpDesc());
                mqMap.put("eventDate", GlobalConstants.dateFormat.format(new Date()));
                mqMap.put("deviceOplog", JsonMapper.toJsonString(deviceOplog));
                GlobalConstants.C2SEqptLogQueue.sendMessage(mqMap);
            }
        }

    }

    private boolean needCare(EquipModel equipModel) {
        if (equipModel.pmState.isPM()) {
            return false;
        }
        String startTime = equipModel.lotStartTime;

        LocalDateTime now = LocalDateTime.now();
        String nowTime = now.format(AvaryAxisUtil.dtfyyyyMMddHHmmss);
        String startTimeM = startTime.substring(10, 12);
        String nowTimeM = nowTime.substring(10, 12);

        if (Double.parseDouble(nowTimeM) - Double.parseDouble(startTimeM) < Double.parseDouble(GlobalConstants.getProperty("IDLE_LESS_TIME"))
                && Double.parseDouble(nowTimeM) + 60 - Double.parseDouble(startTimeM) < Double.parseDouble(GlobalConstants.getProperty("IDLE_LESS_TIME"))
        ) {
            return false;
        }
        return true;
    }
}

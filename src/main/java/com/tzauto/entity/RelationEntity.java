package com.tzauto.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * Created by Administrator on 2019/8/16.
 */
@Data
@AllArgsConstructor
public class RelationEntity {
    public RelationEntity() {
    }

    public RelationEntity(Integer id, String materialNumber,  String recipeName , String fixtureno) {
        this.id = id;
        this.materialNumber = materialNumber;
        this.recipeName = recipeName;
        this.fixtureno = fixtureno;
    }

    //iSecsHost.executeCommand("dialog \"Lot No\" write " + lotId);
    public RelationEntity(Integer id){

    }
    private Integer id;
    //料号
    private String materialNumber = "";
    //流程号
    private String fixtureno;
    //程序名
    private String recipeName = "";
    //最后修改时间
    private Date lastModifyTime;


}

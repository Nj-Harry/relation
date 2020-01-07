package com.tzauto.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2019/8/16.
 */
public class RelationVO {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //主键ID
    private SimpleIntegerProperty id;
    //序号
    private SimpleStringProperty fixtureno;

    //料号
    private SimpleStringProperty materialNumber;

    //程序名
    private SimpleStringProperty recipeName;

    private SimpleStringProperty lastModifyTime;


    public RelationVO() {
    }

    public String getFixtureno() {
        return fixtureno.get();
    }

    public SimpleStringProperty fixturenoProperty() {
        return fixtureno;
    }

    public void setFixtureno(String fixtureno) {
        this.fixtureno.set(fixtureno);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public RelationVO(String materialNumber, String recipeName) {

        this.recipeName = new SimpleStringProperty(recipeName);
        this.materialNumber = new SimpleStringProperty(materialNumber);
    }

    public RelationVO(Integer id, String materialNumber, String fixtureno, String recipeName) {
        this.id = new SimpleIntegerProperty(id);
        this.recipeName = new SimpleStringProperty(recipeName);
        this.materialNumber = new SimpleStringProperty(materialNumber);
        this.fixtureno = new SimpleStringProperty(fixtureno);
    }

    public RelationVO(Integer id, String materialNumber, String fixtureno, String recipeName,  Date lastModifyTime) {
        this.id = new SimpleIntegerProperty(id);
        this.recipeName = new SimpleStringProperty(recipeName);
        this.materialNumber = new SimpleStringProperty(materialNumber);
        this.fixtureno = new SimpleStringProperty(fixtureno);
        this.lastModifyTime = new SimpleStringProperty(dateFormat.format(lastModifyTime));
    }

    public String getMaterialNumber() {
        return materialNumber.get();
    }


    public SimpleStringProperty materialNumberProperty() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber.set(materialNumber);
    }

    public String getRecipeName() {
        return recipeName.get();
    }

    public SimpleStringProperty recipeNameProperty() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName.set(recipeName);
    }

    public String getLastModifyTime() {
        return lastModifyTime.get();
    }

    public SimpleStringProperty lastModifyTimeProperty() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime.set(lastModifyTime);
    }
}

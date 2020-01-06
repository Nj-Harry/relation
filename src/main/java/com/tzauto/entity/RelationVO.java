package com.tzauto.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Administrator on 2019/8/16.
 */
public class RelationVO {

    //主键ID
    private SimpleIntegerProperty id;
    //序号
    private SimpleStringProperty fixtureno;


    //料号
    private SimpleStringProperty materialNumber;


    //程序名
    private SimpleStringProperty recipeName;

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
    public RelationVO(String materialNumber, String recipeName, Integer id, String fixtureno) {
        this.id = new SimpleIntegerProperty(id);
        this.recipeName = new SimpleStringProperty(recipeName);
        this.materialNumber = new SimpleStringProperty(materialNumber);
        this.fixtureno = new SimpleStringProperty(fixtureno);

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
}

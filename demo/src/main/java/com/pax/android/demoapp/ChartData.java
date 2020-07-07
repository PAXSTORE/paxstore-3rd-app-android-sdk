package com.pax.android.demoapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartData implements Serializable {
    private ChartType type;
    private String title;
    private List<String> colums;
    private List<Object[]> datas;

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public ChartData(String title, List<String> colums, List<Object[]> datas) {
        this.title = title;
        this.colums = colums;
        this.datas = datas;
    }

    public ChartData() {
        title = "";
        colums = new ArrayList<>();
        datas = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getColumus() {
        return colums;
    }

    public void setColumus(List<String> legend) {
        this.colums = legend;
    }

    public List<Object[]> getDatas() {
        return datas;
    }

    public void setDatas(List<Object[]> datas) {
        this.datas = datas;
    }

    public boolean  isEmpty() {
        return (datas == null || datas.isEmpty());
    }

    public void clear() {
        title = "";
        colums.clear();
        datas.clear();
    }
}

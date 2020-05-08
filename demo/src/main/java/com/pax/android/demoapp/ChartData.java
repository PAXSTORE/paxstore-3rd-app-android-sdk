package com.pax.android.demoapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartData implements Serializable {
    private String title;
    private List<String> legend;
    private List<String> datas;

    public ChartData(String title, List<String> legend, List<String> datas) {
        this.title = title;
        this.legend = legend;
        this.datas = datas;
    }

    public ChartData() {
        title="";
        legend=new ArrayList<>();
        datas=new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLegend() {
        return legend;
    }

    public void setLegend(List<String> legend) {
        this.legend = legend;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    public boolean isEmpty(){
        return (datas==null||datas.isEmpty());
    }

    public void clear(){
        title="";
        legend.clear();
        datas.clear();
    }
}

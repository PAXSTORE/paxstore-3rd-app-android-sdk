package com.pax.android.demoapp.dto;

public class SalesRecord {
    private String eventTime;
    private String segment;
    private String city;
    private String state;
    private String region;
    private String category;
    private String product;
    private double sales;
    private int quantity;
    private double discount;
    private double profit;
    private int lineNumber; // 记录原始行号

    public SalesRecord() {
    }

    public SalesRecord(String eventTime, String segment, String city, String state,
                       String region, String category, String product, double sales,
                       int quantity, double discount, double profit, int lineNumber) {
        this.eventTime = eventTime;
        this.segment = segment;
        this.city = city;
        this.state = state;
        this.region = region;
        this.category = category;
        this.product = product;
        this.sales = sales;
        this.quantity = quantity;
        this.discount = discount;
        this.profit = profit;
        this.lineNumber = lineNumber;
    }

    // Getter 和 Setter 方法
    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "SalesRecord{" +
                "eventTime='" + eventTime + '\'' +
                ", segment='" + segment + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", region='" + region + '\'' +
                ", category='" + category + '\'' +
                ", product='" + product + '\'' +
                ", sales=" + sales +
                ", quantity=" + quantity +
                ", discount=" + discount +
                ", profit=" + profit +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
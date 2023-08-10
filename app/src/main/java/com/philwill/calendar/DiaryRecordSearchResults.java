package com.philwill.calendar;

/**
 * Created by USER on 15/04/2018.
 */

public class DiaryRecordSearchResults {

    private String id = "";
    private String year = "";
    private String dayName = "";
    private String dateofEvent = "";
    private String dateDifference = "";
    private String detail = "";
    private String category = "";
    private String subCategory = "";


    private String weather = "";
    private String stones = "";
    private String lbs = "";
    private String current_weight = "";
    private String Attachments = "0";
    private String headerRecord = "";


    public String getHeaderRecord() {
        return headerRecord;
    }

    public void setHeaderRecord(String headerRecord) {
        this.headerRecord = headerRecord;
    }

    public String getAttachments() {
        return Attachments;
    }

    public void setAttachments(String attachments) {
        Attachments = attachments;
    }

    public String getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(String current_weight) {
        this.current_weight = current_weight;
    }



    public String getAverage_weight() {
        return average_weight;
    }

    public void setAverage_weight(String average_weight) {
        this.average_weight = average_weight;
    }

    private String bmi = "";
    private String average_weight = "";

    public String getStones() {
        return stones;
    }

    public void setStones(String stones) {
        this.stones = stones;
    }

    public String getLbs() {
        return lbs;
    }

    public void setLbs(String lbs) {
        this.lbs = lbs;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getyear() {
        return year;
    }

    public void setyear(String year) {
        this.year = year;
    }

    public String getdetail() {
        return detail;
    }

    public void setdetail(String detail) {
        this.detail = detail;
    }

    public String getdateofEvent() {
        return dateofEvent;
    }

    public void setdateofEvent(String dateofEvent) {
        this.dateofEvent = dateofEvent;
    }

    public String getdateDifference() {
        return dateDifference;
    }

    public void setdateDifference(String dateDifference) {
        this.dateDifference = dateDifference;
    }

    public String getdayName() {
        return dayName;
    }

    public void setdayName(String dayName) {
        this.dayName = dayName;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public String getsubCategory() {
        return subCategory;
    }

    public void setsubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getweather() {
        return weather;
    }

    public void setweather(String weather) {
        this.weather = weather;
    }
}

package com.comp3617.finalproject.HelperClasses;

/**
 * Object class for Cost Transactions
 */
public class CostTransaction {

    private String TransactionId;
    private String Title;
    private String Category;
    private String Description;
    private String Cost;

    // Date related fields
    private int Year;
    private int Month;
    private int Day;

    // Transaction Id
    public String getTransactionId() {
        return TransactionId;
    }
    public void setTransactionId(String AdId) {
        this.TransactionId = AdId;
    }

    // Transaction Title
    public String getTitle() {
        return Title;
    }
    public void setTitle(String Title) {
        this.Title = Title;
    }

    // Transaction Category
    public String getCategory() {
        return Category;
    }
    public void setCategory(String Category) {
        this.Category = Category;
    }

    // Transaction Description
    public String getDescription() {
        return Description;
    }
    public void setDescription(String Description) {
        this.Description = Description;
    }

    // Transaction Cost
    public String getCost() {
        return Cost;
    }
    public void setCost(String cost) {
        this.Cost = cost;
    }

    // Transaction Date - Year
    public int getYear() {
        return Year;
    }
    public void setYear(int year) {
        this.Year = year;
    }

    // Transaction Date - Month
    public int getMonth() {
        return Month;
    }
    public void setMonth(int month) {
        this.Month = month;
    }

    // Transaction Date - Day
    public int getDay() {
        return Day;
    }
    public void setDay(int day) {
        this.Day = day;
    }
}
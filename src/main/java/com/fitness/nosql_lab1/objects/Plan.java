package com.fitness.nosql_lab1.objects;

public class Plan {
    private String planID;
    private String name;
    private String description;
    private int monthCost;
    private int yearCost;

    public Plan() {}

    public Plan(String planID, String name, String description, int monthCost, int yearCost) {
        this.planID = planID;
        this.name = name;
        this.description = description;
        this.monthCost = monthCost;
        this.yearCost = yearCost;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMonthCost() {
        return monthCost;
    }

    public void setMonthCost(int monthCost) {
        this.monthCost = monthCost;
    }

    public int getYearCost() {
        return yearCost;
    }

    public void setYearCost(int yearCost) {
        this.yearCost = yearCost;
    }
}

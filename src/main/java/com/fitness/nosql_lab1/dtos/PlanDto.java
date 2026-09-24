package com.fitness.nosql_lab1.dtos;

public class PlanDto {
    private String name;
    private String description;
    private int monthCost;
    private int yearCost;

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

    public int getYearCost() {
        return yearCost;
    }

    public void setYearCost(int yearCost) {
        this.yearCost = yearCost;
    }

    public int getMonthCost() {
        return monthCost;
    }

    public void setMonthCost(int monthCost) {
        this.monthCost = monthCost;
    }
}

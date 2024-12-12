package com.example.expense.Model;

import com.example.expense.Model.YearlyModel;

import java.util.List;

public class ParentYearlyModel {
    String month;
    List<YearlyModel> yearlyModels;

    public ParentYearlyModel(String month, List<YearlyModel> yearlyModels) {
        this.month = month;
        this.yearlyModels = yearlyModels;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<YearlyModel> getYearlyModels() {
        return yearlyModels;
    }

    public void setYearlyModels(List<YearlyModel> yearlyModels) {
        this.yearlyModels = yearlyModels;
    }
}

package org.insight.analytics.pojo;

import java.util.ArrayList;

public class CommitteeDetails {
    private double runningTotal;
    private ArrayList<Double> amounts;
    
    public CommitteeDetails(double runningTotal, ArrayList<Double> amounts) {
        super();
        this.runningTotal = runningTotal;
        this.amounts = amounts;
    }
    
    public double getRunningTotal() {
        return runningTotal;
    }
    
    public void setRunningTotal(double runningTotal) {
        this.runningTotal = runningTotal;
    }
    
    public ArrayList<Double> getAmounts() {
        return amounts;
    }
    
    public void setAmounts(ArrayList<Double> amounts) {
        this.amounts = amounts;
    }

    public int size() {
        return this.amounts.size();
    }
    
}

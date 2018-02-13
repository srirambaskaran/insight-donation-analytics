package org.insight.analytics.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class stores the all details for a given committee, zipcode and year.
 * It also calculates the requested percentile of the donations given.
 * 
 * @author srirambaskaran
 *
 */
public class CommitteeDetails {
    private double runningTotal;
    private int size;
    
    private HashSet<Donor> addedDonors;
    private HashMap<Donor, HashMap<Integer, ArrayList<Double>>> allDonations;
    
    private ArrayList<Double> values;
    
    public CommitteeDetails(HashMap<Donor, HashMap<Integer, ArrayList<Double>>> allDonations) {
        super();
        this.runningTotal = 0.0;
        this.addedDonors = new HashSet<>();
        this.size = 0;
        this.allDonations = allDonations; //stores only reference from StreamAnalyzer class.
        this.values = new ArrayList<>();
    }
    
    public double getRunningTotal() {
        return runningTotal;
    }
    
    /**
     * This method finds all donations in the given year for a recently identified
     * as repeated donor and adds to the transactions of (committee+zipcode+year).
     *  
     * @param amount
     * @param donor
     * @param year
     */
        
    public void addAmount(double amount, Donor donor, int year) {
        if (!addedDonors.contains(donor)) {
            ArrayList<Double> donations = allDonations.get(donor).get(year);
            for(Double donation: donations)
                addAmount(donation);
            addedDonors.add(donor);
        } else {
            addAmount(amount);
        }
    }
    
    
    public void addAmount(double amount) {
        this.size ++;
        this.runningTotal += amount;
        this.values.add(amount);
    }
    
    public double getPercentile(double xthPercentile) {
        int index = (int)(Math.ceil( xthPercentile / 100 * this.size));
        return this.values.get(index - 1);
    }
    
    public int size() {
        return this.size;
    }
    
}

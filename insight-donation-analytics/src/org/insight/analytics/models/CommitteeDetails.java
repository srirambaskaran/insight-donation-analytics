package org.insight.analytics.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class CommitteeDetails {
    private double runningTotal;
    private int size;
    
    private HashSet<Donor> addedDonors;
    private HashMap<Donor, HashMap<Integer, ArrayList<Double>>> allDonations;
    
    private PriorityQueue<Double> firstK; //holds the first k values in the (committee+year) combo.
    private PriorityQueue<Double> remaining;
    
    public CommitteeDetails(HashMap<Donor, HashMap<Integer, ArrayList<Double>>> allDonations) {
        super();
        this.runningTotal = 0.0;
        this.addedDonors = new HashSet<>();
        this.size = 0;
        this.firstK = new PriorityQueue<>(Comparator.reverseOrder());
        this.remaining = new PriorityQueue<>();
        this.allDonations = allDonations; //stores only reference from StremAnalyzer class.
    }
    
    public double getRunningTotal() {
        return runningTotal;
    }
    
    public void updateQueues(double amount, int k) {
        if(!firstK.isEmpty() && firstK.peek() > amount) {
            firstK.add(amount);
            while(firstK.size() > k)
                remaining.add(firstK.poll());
        }else {
            remaining.add(amount);
            while(firstK.size() < k)
                firstK.add(remaining.poll());
        }
    }
    
    public void addAmount(double amount, Donor donor, int year, double xthPercentile) {
        if (!addedDonors.contains(donor)) {
            ArrayList<Double> donations = allDonations.get(donor).get(year);
            for(Double donation: donations)
                addAmount(donation, xthPercentile);
            addedDonors.add(donor);
        } else {
            addAmount(amount, xthPercentile);
        }
    }
    
    public void addAmount(double amount, double xthPercentile) {
        this.size ++;
        int index = calculatePercentileIndex(xthPercentile);
        this.runningTotal += amount;
        updateQueues(amount, index);
    }
    
    public int calculatePercentileIndex(double xthPercentile) {
        return (int)Math.ceil( xthPercentile / 100 * this.size);
    }
    
    public double getPercentile() {
        return this.firstK.peek();
    }

    public int size() {
        return this.size;
    }
    
}

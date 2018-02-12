package org.insight.analytics.pojo;

public class OutputStreamMessage {
    
    private Committee committee;
    private double total;
    private double percentile;
    private int numberRepeatDonors;
    
    public OutputStreamMessage(Committee committee, double total, double percentile, int numberRepeatDonors) {
        super();
        this.committee = committee;
        this.total = total;
        this.percentile = percentile;
        this.numberRepeatDonors = numberRepeatDonors;
    }
    
    public Committee getCommittee() {
        return committee;
    }
    public double getTotal() {
        return total;
    }
    public double getPercentile() {
        return percentile;
    }
    public int getNumberRepeatDonors() {
        return numberRepeatDonors;
    }
    
    public String toString() {
        return committee.getCommittee()+"|"+committee.getZipCode()+"|"+committee.getYear()+"|"+Math.round(percentile)+"|"+Math.round(total)+"|"+numberRepeatDonors;
    }
}

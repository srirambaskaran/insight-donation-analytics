package org.insight.analytics.exceptions;

public class DonationAnalyticsException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String message;
    
    public DonationAnalyticsException(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}

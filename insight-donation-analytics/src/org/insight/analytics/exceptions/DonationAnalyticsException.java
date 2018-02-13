package org.insight.analytics.exceptions;

/**
 * Exception wrapper for better handling of exceptions.
 * 
 * @author srirambaskaran
 *
 */

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

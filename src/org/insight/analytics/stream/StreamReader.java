package org.insight.analytics.stream;

import org.insight.analytics.exceptions.DonationAnalyticsException;

public interface StreamReader {

    public String readMessage() throws DonationAnalyticsException;

    public void closeStream() throws DonationAnalyticsException;

}

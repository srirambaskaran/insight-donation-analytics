package org.insight.analytics.stream;

import org.insight.analytics.exceptions.DonationAnalyticsException;
import org.insight.analytics.models.OutputStreamMessage;

public interface StreamWriter {
    
    public void writeMessage(OutputStreamMessage message) throws DonationAnalyticsException;

    public void closeStream() throws DonationAnalyticsException;
}

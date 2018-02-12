package org.insight.analytics.stream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.insight.analytics.exceptions.DonationAnalyticsException;
import org.insight.analytics.pojo.OutputStreamMessage;

public class FileStreamWriter implements StreamWriter{
    
    private BufferedWriter writer;
    private int numberOfLinesWritten;
    
    public FileStreamWriter(String outputFile) throws DonationAnalyticsException {
        FileWriter writerObj  = null;
        try {
            writerObj = new FileWriter(outputFile);
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in opening output file");
        }
        
        writer = new BufferedWriter(writerObj);
        this.numberOfLinesWritten = 0;
    }

    @Override
    public void writeMessage(OutputStreamMessage message) throws DonationAnalyticsException {
        try {
            writer.write(message.toString()+"\n");
            this.numberOfLinesWritten ++;
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in writing message. Number of lines previously written: "+numberOfLinesWritten+".");
        }
    }

    @Override
    public void closeStream() throws DonationAnalyticsException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in closing stream");
        }
    }
    

}

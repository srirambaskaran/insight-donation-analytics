package org.insight.analytics.stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.insight.analytics.exceptions.DonationAnalyticsException;

/**
 * This is a file stream reader that reads the input transaction file. 
 * This is abstract and reads info line by line. This implements the 
 * {@link StreamReader} interface which provides basic read and close 
 * functions for the stream.
 *  
 * @author srirambaskaran
 *
 */
public class FileStreamReader implements StreamReader {

    private int numberOfLinesRead;
    private BufferedReader reader;
    
    public FileStreamReader(String inputFile) throws DonationAnalyticsException {
        FileReader readerObj = null;
        this.numberOfLinesRead = 0;
        try {
            readerObj = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            throw new DonationAnalyticsException("File not found");
        }
        reader = new BufferedReader(readerObj);
        this.numberOfLinesRead = 0;
    }
    
    /**
     * Reads a single message (line) from the file.
     */
    
    public String readMessage() throws DonationAnalyticsException {
        try {
            String message = this.reader.readLine();
            this.numberOfLinesRead++;
            return message;
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in reading message. Messages successfully read: "+numberOfLinesRead+".");
        }
    }
    
    /**
     * Closes the buffered reader.
     */
    public void closeStream() throws DonationAnalyticsException {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in closing the reader");
        }
    }
}

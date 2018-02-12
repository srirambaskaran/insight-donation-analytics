package org.insight.analytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.insight.analytics.exceptions.DonationAnalyticsException;

/**
 * This is the main class for the donation analytics. 
 * This calls methods to read input files and start analyzing them.
 * 
 * @author srirambaskaran
 *
 */
public class DonationAnalytics {
    
    public static void main(String[] args) {
        
        String percentileFile = args[0];
        String inputFile = args[1];
        String outputFile = args[2];
        
        double xthPercentile = 0.0;
        
        try {
            xthPercentile = getRequestedPercentile(percentileFile);
        } catch (DonationAnalyticsException e) {
            System.err.println(e.getMessage());
            System.exit(1); //exits when there is an exception.
        }
        
        StreamAnalyzer analyzer = null;
        try {
            analyzer = new StreamAnalyzer(inputFile, xthPercentile, outputFile);
            analyzer.processMessages();
        } catch (DonationAnalyticsException e) {
            System.err.println(e.getMessage());
            System.exit(1); //exits when there is an exception.
        }
        
    }

    /**
     * Reads percentileFile and returns the value of percentile to be calculated
     * @param percentileFile
     * @return percentile value
     * @throws IOException, when there are issues in reading the percentileFile
     */
    private static double getRequestedPercentile(String percentileFile) throws DonationAnalyticsException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(percentileFile));
            double xthPercentile = Double.parseDouble(reader.readLine());
            reader.close();
            return xthPercentile;
        }catch(IOException e) {
            throw new DonationAnalyticsException("Error in reading percentile file.");
        }
    }
}

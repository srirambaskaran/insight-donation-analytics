package org.insight.analytics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

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
        
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("app.properties"));
        } catch (IOException e1) {
            System.err.println("Unable to read properties file.");
        }
        
        String percentileFile = properties.getProperty("percentileFile", "input/percentile.txt");
        String inputFile = properties.getProperty("inputFile","input/itcont.txt");
        String outputFile = properties.getProperty("outputFile", "output/repeat_donors.txt");
        
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

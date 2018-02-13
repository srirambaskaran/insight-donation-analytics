package org.insight.analytics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import org.insight.analytics.exceptions.DonationAnalyticsException;

/**
 * This is the main class for the donation analytics. This calls methods to read
 * input files and start analyzing them.
 * 
 * @author srirambaskaran
 *
 */
public class DonationAnalytics {

    public static void main(String[] args) {
        
        long startTime = Calendar.getInstance().getTimeInMillis();
        Properties properties = new Properties();
        
        if(args.length > 0) {
            try {
                properties.load(new FileInputStream(args[0]));
            } catch (IOException e1) {
                System.err.println("Unable to read properties file. Using default values.");
            }
        } else {
            System.err.println("No arguments given. Using default location of input and output file.");
        }

        String percentileFile = properties.getProperty("percentileFile", "input/percentile.txt");
        String inputFile = properties.getProperty("inputFile", "input/itcont.txt");
        String outputFile = properties.getProperty("outputFile", "output/repeat_donors.txt");

        double xthPercentile = 0.0;

        try {
            xthPercentile = getRequestedPercentile(percentileFile);
        } catch (DonationAnalyticsException e) {
            System.err.println(e.getMessage());
            System.exit(1); // exits when there is an exception.
        }
        
        if(xthPercentile < 1 || xthPercentile > 100) {
            throw new IllegalArgumentException("Error in value of percentile. It should be between 1 and 100.");
        }

        StreamAnalyzer analyzer = null;
        try {
            analyzer = new StreamAnalyzer(inputFile, xthPercentile, outputFile);
            analyzer.processMessages();
        } catch (DonationAnalyticsException e) {
            System.err.println(e.getMessage());
            System.exit(1); // exits when there is an exception.
        }
        
        long endTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("Time taken : "+(endTime - startTime)+" ms.");

    }

    private static double getRequestedPercentile(String percentileFile) throws DonationAnalyticsException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(percentileFile));
            double xthPercentile = Double.parseDouble(reader.readLine());
            reader.close();
            return xthPercentile;
        } catch (IOException e) {
            throw new DonationAnalyticsException("Error in reading percentile file.");
        }
    }
}

package org.insight.analytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.insight.analytics.exceptions.DonationAnalyticsException;
import org.insight.analytics.models.Committee;
import org.insight.analytics.models.CommitteeDetails;
import org.insight.analytics.models.Donor;
import org.insight.analytics.models.InputStreamMessage;
import org.insight.analytics.models.OutputStreamMessage;
import org.insight.analytics.stream.FileStreamReader;
import org.insight.analytics.stream.FileStreamWriter;
import org.insight.analytics.stream.StreamReader;
import org.insight.analytics.stream.StreamWriter;

public class StreamAnalyzer {

    private StreamReader streamReader;
    private StreamWriter streamWriter;
    private double xthPercentile;

    private HashMap<Donor, Integer> minDonationDate;
    private HashMap<Committee, CommitteeDetails> committeRepeatDonerList;
    private HashMap<Donor, HashMap<Integer, ArrayList<Double>>> allDonations;

    public StreamAnalyzer(String inputFile, double xthPercentile, String outputFile) throws DonationAnalyticsException {
        this.minDonationDate = new HashMap<>();
        this.committeRepeatDonerList = new HashMap<>();
        this.allDonations = new HashMap<>();
        // Input Stream
        try {
            streamReader = new FileStreamReader(inputFile);
            this.xthPercentile = xthPercentile;
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException(
                    "Error when creating input stream. Cascade message: [" + e.getMessage() + "]");
        }

        // Output Stream
        try {
            streamWriter = new FileStreamWriter(outputFile);
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException(
                    "Error when creating output stream. Cascade message: [" + e.getMessage() + "]");
        }

    }

    /**
     * Method that fetches each message and processes individually in
     * processMessage().
     * 
     * @throws DonationAnalyticsException
     */

    public void processMessages() throws DonationAnalyticsException {
        String readMessage = "";
        while ((readMessage = streamReader.readMessage()) != null) {
            processMessage(readMessage);
        }

        // closing streams
        streamReader.closeStream();
        streamWriter.closeStream();
    }

    /**
     * Processes each message from stream, identifying repeated donors and creating
     * a output message for repeated donor. It manipulates two instance variables
     * namely, minDonationDate and committeeRepeatDonorList to track all repeated
     * donations and calculates percentile. We can safely find a repeated donor by
     * checking the earliest date of donation by the donor+zipcode combo.
     * 
     * @param readMessage
     * @throws DonationAnalyticsException
     */
    public void processMessage(String readMessage) throws DonationAnalyticsException {
        InputStreamMessage message = new InputStreamMessage(readMessage);
        if (message.isValidMessage()) {
            Donor donor = new Donor(message.getName(), message.getZipCode());
            addDonorTransaction(donor, message);
            int transactionYear = message.getParsedTransactionDate().get(Calendar.YEAR);
            if (minDonationDate.containsKey(donor)) {
                int earliestDonationYear = minDonationDate.get(donor);
                if (earliestDonationYear <= transactionYear) {
                    emitMessage(message, donor, transactionYear);
                } else {
                    minDonationDate.put(donor, transactionYear);
                }
            } else {
                minDonationDate.put(donor, transactionYear);
            }
        }
    }
    
    /**
     * Maintains an exhaustive collection of donor transactions based on year.
     * If the data is ordered chronologically, we can make this process simpler
     * saving more space.
     *  
     * @param donor
     * @param message
     */

    private void addDonorTransaction(Donor donor, InputStreamMessage message) {
        int year = message.getParsedTransactionDate().get(Calendar.YEAR);
        if (allDonations.containsKey(donor) && allDonations.get(donor).containsKey(year))
            allDonations.get(donor).get(year).add(message.getAmount());
        else if (allDonations.containsKey(donor)) {
            allDonations.get(donor).put(year, new ArrayList<>());
            allDonations.get(donor).get(year).add(message.getAmount());
        } else {
            allDonations.put(donor, new HashMap<>());
            allDonations.get(donor).put(year, new ArrayList<>());
            allDonations.get(donor).get(year).add(message.getAmount());
        }
    }

    /**
     * This finds the committee based on (committee, zipcode and year) combination
     * and find the percentile. All these are written into the output stream.
     * 
     * @param message
     * @throws DonationAnalyticsException
     */
    private void emitMessage(InputStreamMessage message, Donor donor, int year) throws DonationAnalyticsException {

        Committee committee = new Committee(message.getCommitteeId(), message.getZipCode(),
                message.getParsedTransactionDate().get(Calendar.YEAR));
        OutputStreamMessage outputStreamMessage = null;

        if (committeRepeatDonerList.containsKey(committee)) {
            // tracking all previous donations to this committee in a given year
            CommitteeDetails committeeDetails = committeRepeatDonerList.get(committee);
            committeeDetails.addAmount(message.getAmount(), donor, year);

            double percentile = committeeDetails.getPercentile(xthPercentile);
            
            outputStreamMessage = new OutputStreamMessage(committee, committeeDetails.getRunningTotal(), percentile,
                    committeeDetails.size());
        } else {

            // starting new tracking for a committee in a given year.
            double percentile = message.getAmount(); // Any percentile of a list of 1 value is the same.
            CommitteeDetails committeeDetails = new CommitteeDetails(allDonations);
            committeeDetails.addAmount(message.getAmount(), donor, year);
            committeRepeatDonerList.put(committee, committeeDetails);

            outputStreamMessage = new OutputStreamMessage(committee, committeeDetails.getRunningTotal(), percentile,
                    committeeDetails.size());
        }

        try {
            streamWriter.writeMessage(outputStreamMessage);
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException(
                    "Error when writing message into output stream. Cascade message: [" + e.getMessage() + "]");
        }

    }
}
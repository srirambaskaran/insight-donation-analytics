package org.insight.analytics;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.insight.analytics.exceptions.DonationAnalyticsException;
import org.insight.analytics.pojo.Committee;
import org.insight.analytics.pojo.CommitteeDetails;
import org.insight.analytics.pojo.Donor;
import org.insight.analytics.pojo.InputStreamMessage;
import org.insight.analytics.pojo.OutputStreamMessage;
import org.insight.analytics.stream.FileStreamReader;
import org.insight.analytics.stream.FileStreamWriter;
import org.insight.analytics.stream.StreamReader;
import org.insight.analytics.stream.StreamWriter;

public class StreamAnalyzer {
    
    private StreamReader streamReader;
    private StreamWriter streamWriter;
    private double xthPercentile;
    
    private HashMap<Donor, PriorityQueue<InputStreamMessage>> donationMessages;
    private HashMap<Committee, CommitteeDetails> committeRepeatDonerList;
    
    public StreamAnalyzer(String inputFile, double xthPercentile, String outputFile) throws DonationAnalyticsException {
        //Input Stream
        try {
            streamReader = new FileStreamReader(inputFile);
            this.xthPercentile = xthPercentile;
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when creating input stream. Cascade message: ["+e.getMessage()+"]");
        }
        
        //Output Stream
        try {
            streamWriter = new FileStreamWriter(outputFile);
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when creating output stream. Cascade message: ["+e.getMessage()+"]");
        }
        
    }
    
    /**
     * Method that fetches each message and processes individually in processMessage().
     * 
     * @throws DonationAnalyticsException
     */
    
    public void processMessages() throws DonationAnalyticsException {
        String readMessage = "";
        while((readMessage = streamReader.readMessage()) != null) {
            processMessage(readMessage);
        }
        
        //closing streams
        streamReader.closeStream();
        streamWriter.closeStream();
    }
    
    /**
     * Processes each message from stream, identifying repeated donors and 
     * creating a output message for repeated donor. It manipulates two instance 
     * variables namely, donationMessages and committeeRepeatDonorList to track all
     * repeated donations and calculates percentile.
     * 
     * @param readMessage
     * @throws DonationAnalyticsException
     */
    public void processMessage(String readMessage) throws DonationAnalyticsException {
        InputStreamMessage message = new InputStreamMessage(readMessage);
        if(message.isValidMessage()) {
            Donor donor = new Donor(message.getName(), message.getZipCode());
            boolean repeatedDonor = true;
            if(donationMessages.containsKey(donor)) {
                PriorityQueue<InputStreamMessage> donorMessages = donationMessages.get(donor);
                InputStreamMessage headOfQueue = donorMessages.peek();
                repeatedDonor = headOfQueue.getParsedTransactionDate().get(Calendar.YEAR) < message.getParsedTransactionDate().get(Calendar.YEAR);
                if(repeatedDonor) {
                    donorMessages.add(message);
                    addRepeatedDonor(message);
                }
                else {
                    donorMessages.add(message);
                }
            } else {
                PriorityQueue<InputStreamMessage> donorMessage = new PriorityQueue<>(new DonorYearComparator());
                donorMessage.add(message);
                donationMessages.put(donor, donorMessage);
            }
        }
    }
    
    /**
     * This finds the committee based on (committee, zipcode and year) combination and 
     * find the percentile. All these are written into the output stream.
     * 
     * @param message
     * @throws DonationAnalyticsException
     */
    private void addRepeatedDonor(InputStreamMessage message) throws DonationAnalyticsException {
        
        Committee committee = new Committee(message.getCommitteeId(), 
                message.getZipCode(), 
                message.getParsedTransactionDate().get(Calendar.YEAR));
        OutputStreamMessage outputStreamMessage = null;
        
        if(committeRepeatDonerList.containsKey(committee)) { 
            //tracking all previous donations to this committee in a given year
            
            CommitteeDetails committeeDetails = committeRepeatDonerList.get(committee);
            double percentile = DataUtils.calculatePercentile(committeeDetails.getAmounts(), this.xthPercentile);
            committeeDetails.getAmounts().add(message.getAmount());
            committeeDetails.setRunningTotal(committeeDetails.getRunningTotal()+message.getAmount());
            
            outputStreamMessage = new OutputStreamMessage(committee, 
                    committeeDetails.getRunningTotal(), 
                    percentile, 
                    committeeDetails.size());
        }else {
            
            //starting new tracking for a committee in a given year.
            double percentile = message.getAmount(); //Any percentile of a list of 1 value is the same.  
            CommitteeDetails committeeDetails = new CommitteeDetails(message.getAmount());
            committeeDetails.getAmounts().add(message.getAmount());
            committeRepeatDonerList.put(committee, committeeDetails);

            outputStreamMessage = new OutputStreamMessage(committee, 
                    committeeDetails.getRunningTotal(), 
                    percentile, 
                    committeeDetails.size());
        }
        
        try {
            streamWriter.writeMessage(outputStreamMessage);
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when writing message into output stream. Cascade message: ["+e.getMessage()+"]");
        }
        
    }
}

/**
 * Comparator for donor priority queue. 
 * This compares year in the input message and returns minimum.
 * @author srirambaskaran
 *
 */
class DonorYearComparator implements Comparator<InputStreamMessage> {
    @Override
    public int compare(InputStreamMessage o1, InputStreamMessage o2) {
        Integer year1 = o1.getParsedTransactionDate().get(Calendar.YEAR);
        Integer year2 = o2.getParsedTransactionDate().get(Calendar.YEAR);
        return year1.compareTo(year2);
    }

   
    
}

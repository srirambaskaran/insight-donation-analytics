package org.insight.analytics;

import java.util.ArrayList;
import java.util.Calendar;
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
        
        try {
            streamReader = new FileStreamReader(inputFile);
            this.xthPercentile = xthPercentile;
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when creating input stream. Cascade message: ["+e.getMessage()+"]");
        }
        
        try {
            streamWriter = new FileStreamWriter(outputFile);
            this.xthPercentile = xthPercentile;
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when creating output stream. Cascade message: ["+e.getMessage()+"]");
        }
        
    }
    
    public void processMessages() throws DonationAnalyticsException {
        String readMessage = "";
        while((readMessage = streamReader.readMessage()) != null) {
            processMessage(readMessage);
        }
    }
    
    public void processMessage(String readMessage) throws DonationAnalyticsException {
        InputStreamMessage message = new InputStreamMessage(readMessage);
        if(message.isValidMessage()) {
            Donor donor = new Donor(message.getName(), message.getZipCode());
            boolean repeatedDonor = true;
            if(donationMessages.containsKey(donor)) {
                //Check range
                if(repeatedDonor)
                    addRepeatedDonor(message);
                else {
                    //add to yearDonations
                    //continue;
                }
            }
        }
        else {
            //continue;
        }
    }

    private void addRepeatedDonor(InputStreamMessage message) throws DonationAnalyticsException {
        Committee committee = new Committee(message.getCommitteeId(), message.getZipCode(), message.getParsedTransactionDate().get(Calendar.YEAR));
        OutputStreamMessage outputStreamMessage = null;
        if(committeRepeatDonerList.containsKey(committee)) {
            double percentile = DataUtils.calculatePercentile(committeRepeatDonerList.get(committee).getAmounts(), this.xthPercentile);
            CommitteeDetails committeeDetails = committeRepeatDonerList.get(committee);
            committeeDetails.getAmounts().add(message.getAmount());
            committeeDetails.setRunningTotal(committeeDetails.getRunningTotal()+message.getAmount());
            
            outputStreamMessage = new OutputStreamMessage(committee, committeRepeatDonerList.get(committee).getRunningTotal(), percentile, committeRepeatDonerList.get(committee).size());
        }else {
            double percentile = message.getAmount(); //Any percentile of a list of 1 value is the same.  
            CommitteeDetails committeeDetails = new CommitteeDetails(message.getAmount(), new ArrayList<Double>());
            committeeDetails.setRunningTotal(committeeDetails.getRunningTotal()+message.getAmount());
            committeRepeatDonerList.put(committee, committeeDetails);

            outputStreamMessage = new OutputStreamMessage(committee, committeRepeatDonerList.get(committee).getRunningTotal(), percentile, committeRepeatDonerList.get(committee).size());
        }
        
        try {
            streamWriter.writeMessage(outputStreamMessage);
        } catch (DonationAnalyticsException e) {
            throw new DonationAnalyticsException("Error when writing message into output stream. Cascade message: ["+e.getMessage()+"]");
        }
        
    }
    
}

package org.insight.analytics.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Stream message class that parses the message in the stream and creates an
 * object.
 * 
 * Note: Check the validity of message before using the getters.
 * 
 * @author srirambaskaran
 *
 */
public class InputStreamMessage {
    private String committeeId;
    private String name;
    private String zipCode;
    private String transactionDate;
    private Calendar parsedTransactionDate;
    private String amountString;
    private double amount;
    private String otherId;

    // relevant indices in the message
    private static int COMMITTEE_ID_INDEX = 0;
    private static int NAME_INDEX = 7;
    private static int ZIPCODE_INDEX = 10;
    private static int TRANSACTION_DATE_INDEX = 13;
    private static int AMOUNT_INDEX = 14;
    private static int OTHER_ID_INDEX = 15;

    private boolean isValidMessage;

    public InputStreamMessage(String message) {
        parseMessage(message);
        this.isValidMessage = false;
        checkValidity();
    }

    /*
     * Getters
     */

    public String getCommitteeId() {
        return committeeId;
    }

    public String getName() {
        return name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public Calendar getParsedTransactionDate() {
        return parsedTransactionDate;
    }

    public String getAmountString() {
        return amountString;
    }

    public double getAmount() {
        return amount;
    }

    public String getOtherId() {
        return otherId;
    }

    public boolean isValidMessage() {
        return isValidMessage;
    }

    /*
     * Private Methods for parse and checking the validity of stream message
     */
    private void parseMessage(String message) {
        String[] tokens = message.split("\\|");
        this.committeeId = tokens[COMMITTEE_ID_INDEX];
        this.name = tokens[NAME_INDEX];
        this.zipCode = tokens[ZIPCODE_INDEX];
        this.transactionDate = tokens[TRANSACTION_DATE_INDEX];
        this.amountString = tokens[AMOUNT_INDEX];
        this.otherId = tokens[OTHER_ID_INDEX];
    }

    private void checkValidity() {

        isValidMessage = validCommitteeId() && validName() && validZipCode() && validTransactionDate() && validAmount()
                && validOtherId();
    }

    private boolean validOtherId() {
        return this.otherId == null || this.otherId.equals("");
    }

    private boolean validAmount() {
        if (this.amountString == null)
            return false;

        try {
            this.amount = Double.parseDouble(this.amountString);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean validTransactionDate() {
        if (this.transactionDate == null)
            return false;
        try {
            SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
            this.parsedTransactionDate = Calendar.getInstance();
            this.parsedTransactionDate.setTime(format.parse(this.transactionDate));
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean validZipCode() {
        if (this.zipCode == null || this.zipCode.equals("") || this.zipCode.length() < 5)
            return false;
        else {
            this.zipCode = this.zipCode.substring(0, 5);
            return true;
        }
    }

    private boolean validName() {
        if (this.name == null || this.name.equals(""))
            return false;
        else
            return true;
    }

    private boolean validCommitteeId() {
        if (this.committeeId == null || this.committeeId.equals(""))
            return false;
        else
            return true;
    }

}

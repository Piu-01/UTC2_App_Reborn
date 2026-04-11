package com.utc2.appreborn.ui.tuition;

public class Invoice {
    private String invoiceID;
    private String date;
    private String amount;

    public Invoice(String invoiceID, String date, String amount) {
        this.invoiceID = invoiceID;
        this.date = date;
        this.amount = amount;
    }

    public String getInvoiceID() { return invoiceID; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
}
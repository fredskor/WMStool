package com.company.Entries;

import java.util.Date;

public class Transactions {
    private String transactionRow;
    private Date startTime;
    private Date endTime;

    public String getTransactionRow() {
        return this.transactionRow;
    }

    /* Properties */

    public void setTransactionRow(String transactionRow) {
            this.transactionRow = transactionRow;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}

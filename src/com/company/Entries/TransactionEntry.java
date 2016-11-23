package com.company.Entries;

import java.util.List;

public class TransactionEntry {
    private String nameOfTheTransaction;
    private List<String> listOfTransactions;
    private List<String> startOfTheTransaction;
    private List<String> endOfTheTransaction;
    private List<String> firstPartOfOutput;

    /* Properties */

    public String getNameOfTheTransaction() {
        return this.nameOfTheTransaction;
    }

    public void setNameOfTheTransaction(String nameOfTheTransaction) throws Exception {
        if (nameOfTheTransaction == null && nameOfTheTransaction.isEmpty()) {
            throw new Exception("Name Of The Transaction cannot be empty string");
        }
        else {
            this.nameOfTheTransaction = nameOfTheTransaction;
        }
    }

    public List<String> getListOfTransactions() {
        return this.listOfTransactions;
    }

    public void setListOfTransactions(List<String> listOfTransactions) {
        this.listOfTransactions = listOfTransactions;
    }

    public List<String> getStartOfTheTransaction() {
        return this.startOfTheTransaction;
    }

    public void setStartOfTheTransaction(List<String> startOfTheTransaction) {
        this.startOfTheTransaction = startOfTheTransaction;
    }

    public List<String> getEndOfTheTransaction() {
        return this.endOfTheTransaction;
    }

    public void setEndOfTheTransaction(List<String> endOfTheTransaction) {
        this.endOfTheTransaction = endOfTheTransaction;
    }

    public List<String> getFirstPartOfOutput() {
        return this.firstPartOfOutput;
    }

    public void setFirstPartOfOutput(List<String> firstPartOfOutput) {
        this.firstPartOfOutput = firstPartOfOutput;
    }
}

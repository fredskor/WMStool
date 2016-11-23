package com.company.Readers;

import com.company.Entries.ErrorLogEntry;
import com.company.Entries.TransactionEntry;
import com.company.Entries.Transactions;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WMSReader extends AbstractReader {

    private String pathForWms;
    private String searchingString;

//    public WMSReader(String pathForWms, String searchingString) throws Exception {
//        this.setPathForWms(pathForWms);
//        this.setSearchingString(searchingString);
//    }

    public WMSReader(Calendar calendar, String searchingString) throws Exception {
        try {
            this.setPathForWms(getFullPath(calendar));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setSearchingString(searchingString);
    }

    //Properties
    public String getPathForWms() {
        return this.pathForWms;
    }

    public void setPathForWms(String pathForWms) throws Exception {
        if (pathForWms == null && pathForWms.isEmpty()) {
            throw new Exception("Path for WMS Log cannot be empty string");
        } else {
            this.pathForWms = pathForWms;
        }
    }

    public String getSearchingString() {
        return this.searchingString;
    }

    public void setSearchingString(String searchingString) throws Exception {
        if (searchingString == null && searchingString.isEmpty()) {
            throw new Exception("Searching string cannot be empty string");
        } else {
            this.searchingString = searchingString;
        }
    }

    //Methods

    public ErrorLogEntry FindAllStringsInWMSWriteToFileAndSetErrorEntry() throws Exception {
        TransactionEntry transactionEntry = new TransactionEntry();
        List<Date> listwithStartTime = new ArrayList<>();
        List<Date> listwithEndTime = new ArrayList<>();
        Date startTime;
        Date endTime;

        List<String> listOfTransactions = FindАllMatchedTransactions();

        for (int i = 0; i < listOfTransactions.size(); i++) {
            transactionEntry = FindTheTransactionNameAndFirstPartOfOutput(listOfTransactions.get(i));
            transactionEntry.setStartOfTheTransaction(FindTheStartOfTheTransaction(transactionEntry.getFirstPartOfOutput(), transactionEntry.getNameOfTheTransaction()));
            startTime = FindTheTimeForComparing(transactionEntry.getStartOfTheTransaction().get(0));
            listwithStartTime.add(startTime);

            WriteToFile(transactionEntry.getStartOfTheTransaction());

            transactionEntry.setEndOfTheTransaction(FindTheEndOfTheTransaction(transactionEntry.getStartOfTheTransaction(), transactionEntry.getNameOfTheTransaction()));
            endTime = FindTheTimeForComparing(transactionEntry.getEndOfTheTransaction().get(transactionEntry.getEndOfTheTransaction().size() - 1));
            listwithEndTime.add(endTime);

            WriteToFile(transactionEntry.getEndOfTheTransaction());
        }

        ErrorLogEntry errorLogEntry;
        errorLogEntry = AddTransactionRowsStartTimeAndEndTimeToErrorLogEntry(listOfTransactions,
                listwithStartTime,
                listwithEndTime);
        return errorLogEntry;
    }

    private List<String> FindАllMatchedTransactions() throws Exception {
        List<String> listOfTransactions = new ArrayList<>();
        String currentLine;
        FileInputStream inputStream = new FileInputStream(this.getPathForWms());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while ((currentLine = bufferedReader.readLine()) != null) {
            if (currentLine.contains(this.getSearchingString())) {
                listOfTransactions.add(currentLine);
            }
        }
        //delete duplicates and sort them
        listOfTransactions = DeleteAllTransactionDuplicatesAndSortThem(listOfTransactions);
        bufferedReader.close();
        return listOfTransactions;
    }

    private List<String> DeleteAllTransactionDuplicatesAndSortThem(List<String> listOfTransactions) {
        Set<String> hashSet = new HashSet<>();
        hashSet.addAll(listOfTransactions);
        listOfTransactions.clear();
        listOfTransactions.addAll(hashSet);

        for (int i = 0; i < listOfTransactions.size(); i++) {
            listOfTransactions.set(i, listOfTransactions.get(i).substring(24, listOfTransactions.get(i).length() - 1));
        }
        Collections.sort(listOfTransactions);

        try {
            for (int j = listOfTransactions.size() - 1; j >= 0; j--) {
                if (listOfTransactions.get(j).substring(0, 10).equals(listOfTransactions.get(j - 1).substring(0, 10))) {
                    listOfTransactions.remove(j);
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
        }
        return listOfTransactions;
    }

    private TransactionEntry FindTheTransactionNameAndFirstPartOfOutput(String transactionString) throws Exception {
        TransactionEntry transactionEntry = new TransactionEntry();
        String currentLine = "";
        Boolean isTheWordThere = false;
        List<String> firstPartOfOutput = new ArrayList<String>();
        String[] arrayOfFoundedLine = new String[currentLine.length()];
        FileInputStream inputStream = new FileInputStream(this.getPathForWms());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while (!isTheWordThere) {
            currentLine = bufferedReader.readLine();

            if (currentLine.contains(transactionString)) {
                arrayOfFoundedLine = currentLine.replaceAll("\\s+", "").split("\\[|\\]");
                isTheWordThere = true;
            }
            firstPartOfOutput.add(currentLine);
        }

        Collections.reverse(firstPartOfOutput);

        transactionEntry.setFirstPartOfOutput(firstPartOfOutput);
        transactionEntry.setNameOfTheTransaction(arrayOfFoundedLine[1]);

        return transactionEntry;
    }

    private List<String> FindTheStartOfTheTransaction(List<String> firstPartOfOutput, String transactionName) {
        String startOfTheProcess = "START";
        List<String> listWithStartWord = new ArrayList<String>();

        for (int i = 0; i < firstPartOfOutput.size(); i++) {
            if ((firstPartOfOutput.get(i).contains(startOfTheProcess))
                    && firstPartOfOutput.get(i).contains(transactionName)) {
                listWithStartWord.add(firstPartOfOutput.get(i));
                break;

            } else if ((!firstPartOfOutput.get(i).contains(startOfTheProcess))
                    && firstPartOfOutput.get(i).contains(transactionName)) {
                listWithStartWord.add(firstPartOfOutput.get(i));
            }
        }
        Collections.reverse(listWithStartWord);
        return listWithStartWord;
    }

    private List<String> FindTheEndOfTheTransaction(List<String> listWithStartWord, String transactionName) throws IOException {
        String startLineForReader = listWithStartWord.get(1);
        String currentLine;
        String endOfTheProcess = "END";
        List<String> listWithEndWord = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(this.getPathForWms());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        currentLine = bufferedReader.readLine();

        while (!currentLine.equals(startLineForReader)) {
            currentLine = bufferedReader.readLine();
        }

        while ((currentLine = bufferedReader.readLine()) != null) {
            //currentLine = bufferedReader.readLine();

            if (currentLine.contains(transactionName)) {
                listWithEndWord.add(currentLine);
            }

            if (((currentLine.contains(transactionName) && currentLine.contains(endOfTheProcess)))) {
                break;
            }
        }
        bufferedReader.close();
        return listWithEndWord;
    }

    private Date FindTheTimeForComparing(String partOfTransaction) throws ParseException {
        int index = 11;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getTimeFormat());
        Date timeForComparing = simpleDateFormat.parse(partOfTransaction.substring(index, 19));
        return timeForComparing;
    }

    private ErrorLogEntry AddTransactionRowsStartTimeAndEndTimeToErrorLogEntry(List<String> listOfTransactions, List<Date> listwithStartTime, List<Date> listWithEndTime) {
        ErrorLogEntry errorLogEntry = new ErrorLogEntry();

        for (int i = 0; i < listOfTransactions.size(); i++) {
            Transactions transactions = new Transactions();
            transactions.setTransactionRow(listOfTransactions.get(i));
            transactions.setStartTime(listwithStartTime.get(i));
            transactions.setEndTime(listWithEndTime.get(i));

            errorLogEntry.transactionList.add(transactions);
        }
        return errorLogEntry;
    }

    private void WriteToFile(List<String> listToWrite) throws IOException {
        String pathForWriter = "C:/wmsOutput.txt";
        FileWriter fileWriter = new FileWriter(pathForWriter, true);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        listToWrite.forEach(item -> {
            try {
                writer.write(item);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    @Override
    public String getPrefix() {
        return "wms";
    }
}

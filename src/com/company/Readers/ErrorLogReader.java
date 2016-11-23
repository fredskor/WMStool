package com.company.Readers;

import com.company.Entries.ErrorLogEntry;
import com.company.Entries.Transactions;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorLogReader extends AbstractReader {
    private String pathForErrorLog;
    private ErrorLogEntry errorEntry;


//    public ErrorLogReader(String pathForErrorLog, ErrorLogEntry errorEntry) throws Exception {
//        setPathForErrorLog(pathForErrorLog);
//        setErrorEntry(errorEntry);
//    }

    public ErrorLogReader(Calendar calendar, ErrorLogEntry errorEntry) throws Exception {
        setPathForErrorLog(getFullPath(calendar));
        setErrorEntry(errorEntry);
    }

    public ErrorLogReader(String pathForErrorLog) throws Exception {
        setPathForErrorLog(pathForErrorLog);
    }


    /* Properties */

    public String getPathForErrorLog() {
        return this.pathForErrorLog;
    }

    public void setPathForErrorLog(String pathForErrorLog) throws Exception {
        if (pathForErrorLog == null && pathForErrorLog.isEmpty()) {
            throw new Exception("Path for error Log cannot be empty string");
        } else {
            this.pathForErrorLog = pathForErrorLog;
        }
    }

    public ErrorLogEntry getErrorEntry() {
        return errorEntry;
    }

    public void setErrorEntry(ErrorLogEntry errorEntry) {
        this.errorEntry = errorEntry;
    }

    /* Methods */

    public void SearchForErrorsAndWriteToFile() throws IOException, ParseException {
        List<String> listWithAllResults;
        for (int i = 0; i < this.getErrorEntry().transactionList.size(); i++) {
            listWithAllResults = SearchByTransactionNameInErrorLog(this.errorEntry.transactionList.get(i));
            WriteToFile(listWithAllResults);
        }
    }

    private List<String> SearchByTransactionNameInErrorLog(Transactions transactions) throws IOException, ParseException {
        List<String> listWithResult = new ArrayList<>();
        String currentLine;
        String[] arrayOfFoundedLine = transactions.getTransactionRow().replaceAll("\\s+", "").split("\\[|\\]");
        String transactionName = arrayOfFoundedLine[1];

        FileInputStream inputStream = new FileInputStream(this.getPathForErrorLog());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while ((currentLine = bufferedReader.readLine()) != null) {
            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            Matcher matcher = pattern.matcher(currentLine);

            if (matcher.find()) {
                Date searchingTime = FindTheTimeForComparing(matcher.group());
                if (searchingTime.before(transactions.getStartTime()) || searchingTime.after(transactions.getEndTime()))
                    continue;
                if (currentLine.contains(transactionName)) do {
                    listWithResult.add(currentLine);
                    currentLine = bufferedReader.readLine();
                    matcher = pattern.matcher(currentLine);
                } while (!matcher.find());
            }
        }
        return listWithResult;
    }

    private Date FindTheTimeForComparing(String partOfLog) throws ParseException {
        int index = 11;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getTimeFormat());
        Date timeForComparing = simpleDateFormat.parse(partOfLog.substring(index, 19));

        return timeForComparing;
    }


    private void WriteToFile(List<String> listToWrite) throws IOException {
        String pathForWriter = "C:/errorOutput.txt";
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

    public void FindTheErrorUsingSearchWordAndTimeStampAndWriteToFile(String searchingWord, String startTime, String endTime) throws Exception {
        String currentLine;
        Boolean isTheWordThere = false;
        List<String> firstPartOfOutput = new ArrayList<String>();
        FileInputStream inputStream = new FileInputStream(this.getPathForErrorLog());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        List<String> startWordList = new ArrayList<>();
        boolean isAppropTime = false;
        try {
            while (!isAppropTime) {
                while (!isTheWordThere) {
                    currentLine = bufferedReader.readLine();

                    if (currentLine.contains(searchingWord)) {
                        isTheWordThere = true;
                    }

                    firstPartOfOutput.add(currentLine);
                }

                Collections.reverse(firstPartOfOutput);
                startWordList = FindStartOfTheError(firstPartOfOutput);
                isAppropTime = IsAppropriatedPeriodOfTime(startWordList.get(0), startTime, endTime);
                isTheWordThere = false;
            }

            bufferedReader.close();
            List<String> endWordList = FindEndOfTheError(startWordList);
            WriteToFile(startWordList);
            WriteToFile(endWordList);

        } catch (NullPointerException x) {}

    }

    private List<String> FindStartOfTheError(List<String> firstPartOfOutput) {
        String currentLine;
        List<String> listWithStartWord = new ArrayList<String>();

        for (int i = 0; i < firstPartOfOutput.size(); i++) {
            currentLine = firstPartOfOutput.get(i);
            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            Matcher matcher = pattern.matcher(currentLine);

            if (matcher.find()) {
                listWithStartWord.add(currentLine);
                break;
            }
            listWithStartWord.add(currentLine);
        }

        Collections.reverse(listWithStartWord);
        return listWithStartWord;
    }

    private List<String> FindEndOfTheError(List<String> listWithStartWord) throws IOException {
        String startLineForReader = listWithStartWord.get(listWithStartWord.size() - 1);
        String currentLine;
        List<String> listWithEndWord = new ArrayList<>();

        FileInputStream inputStream = new FileInputStream(this.getPathForErrorLog());
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        currentLine = bufferedReader.readLine();

        while (!currentLine.equals(startLineForReader)) {
            currentLine = bufferedReader.readLine();
        }

        while ((currentLine = bufferedReader.readLine()) != null) {
            //currentLine = bufferedReader.readLine();
            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            Matcher matcher = pattern.matcher(currentLine);

            if (matcher.find()) {
                break;
            }
            listWithStartWord.add(currentLine);
        }
        bufferedReader.close();
        return listWithEndWord;
    }

    private Boolean IsAppropriatedPeriodOfTime(String startOfError, String startTimeForCompare, String endTimeForComapre) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getTimeFormat());
        Date startTime = simpleDateFormat.parse(startTimeForCompare);
        Date endTime = simpleDateFormat.parse(endTimeForComapre);
        Date searchingTime = FindTheTimeForComparing(startOfError);

        return !(searchingTime.before(startTime) || searchingTime.after(endTime));

//        if (searchingTime.before(startTime) || searchingTime.after(endTime)) {
//            return false;
//        } else {
//            return true;
//        }

    }

    @Override
    public String getPrefix() {
        return "error";
    }
}

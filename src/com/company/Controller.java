package com.company;

import com.company.Entries.ErrorLogEntry;
import com.company.Readers.ErrorLogReader;
import com.company.Readers.WMSReader;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class Controller extends Application {

    public BorderPane borderPane;
    public TextField fromTimetxtF;
    public TextField toTimetxtF;
    public TextField searchingtxtF;
    public ComboBox<String> comboBox;
    public CheckBox checkBox;
    public Button searchButton;
    public String logType;
    public String startTime;
    public String endTime;
    public Button viewLogsButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("applicationView.fxml"));
        primaryStage.setTitle("Log Tool");
        primaryStage.setScene(new Scene(root, 410, 155));
        primaryStage.show();
    }

    public void search(ActionEvent actionEvent) throws Exception {

        List<Calendar> dateListForWMS = new ArrayList<>();
//        dateListForWMS.add(new GregorianCalendar(2016, 9, 6, 16, 0));
//        dateListForWMS.add(new GregorianCalendar(2016, 9, 6, 17, 0));
        dateListForWMS.add(new GregorianCalendar(2016, 9, 6, 18, 0));

        //searchingString = "Nutzer(3243210580";
        String searchingString = searchingtxtF.getText();
        System.out.println(searchingString);

        switch (logType) {
            case "both":
                for (int i = 0; i < dateListForWMS.size(); i++) {

                    WMSReader wmsReader = new WMSReader(dateListForWMS.get(i), searchingString);
                    ErrorLogEntry errorEntry = wmsReader.FindAllStringsInWMSWriteToFileAndSetErrorEntry();
                    ErrorLogReader errorReader = new ErrorLogReader(dateListForWMS.get(i), errorEntry);
                    errorReader.SearchForErrorsAndWriteToFile();
                }
                break;
            case "WMS":
                for (int i = 0; i < dateListForWMS.size(); i++) {

                    WMSReader wmsReader = new WMSReader(dateListForWMS.get(i), searchingString);
                    ErrorLogEntry errorEntry = wmsReader.FindAllStringsInWMSWriteToFileAndSetErrorEntry();
                }
                break;

            case "ERROR":
                String path = "C:/error.2016-09-06_16.log";
//                String searchWord = "DbHandlerService";
//                String startTime = "18:23:55";
//                String endTime = "18:23:57";
                ErrorLogReader erReder = new ErrorLogReader(path);
                startTime = fromTimetxtF.getText();
                endTime = toTimetxtF.getText();
                erReder.FindTheErrorUsingSearchWordAndTimeStampAndWriteToFile(searchingString, startTime, endTime);
                break;

        }

        AlertBox.display("End of the Process", "Done! You can find the logs in C:/\n WMS Log -> wmsOutput.txt\n Error Log -> errorOutput.txt");
    }

    public void chooseType(ActionEvent actionEvent) {
        System.out.println(comboBox.getValue());
        logType = comboBox.getValue();
    }

    public void chainSearch(ActionEvent actionEvent) {
        if (checkBox.isSelected()) {
            comboBox.setDisable(true);
            logType = "both";
        } else if (!checkBox.isSelected()) {
            comboBox.setDisable(false);
        }
    }

    public void initialize(ActionEvent actionEvent) {

    }

    public void viewLogs(ActionEvent actionEvent) throws IOException {
        LogsView.display("Logs View");
    }
}

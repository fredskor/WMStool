package com.company;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;

public class LogsView {
    public static void display(String title) throws IOException {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(1000);
        window.setMinHeight(1000);

        TabPane tabPane = new TabPane();
        Tab wmsTab = new Tab("WMS Log");
        Tab errorTab = new Tab("Error Log");

        TextArea textAreaWms = new TextArea();
        TextArea textAreaError = new TextArea();

        wmsTab.setContent(textAreaWms);
        errorTab.setContent(textAreaError);

        tabPane.getTabs().addAll(wmsTab,errorTab);

        String currentLine;
        FileInputStream inputStream = new FileInputStream("C:/wmsOutput.txt");
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(streamReader);

        while ((currentLine = bufferedReader.readLine()) != null) {
            textAreaWms.appendText(currentLine + "\n");
        }

        bufferedReader.close();

        inputStream = new FileInputStream("C:/errorOutput.txt");
        streamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(streamReader);

        while ((currentLine = bufferedReader.readLine()) != null) {
            textAreaError.appendText(currentLine + "\n");
        }

        bufferedReader.close();

        Scene scene = new Scene(tabPane);
        window.setScene(scene);
        window.getScene();
        window.showAndWait();

    }
}

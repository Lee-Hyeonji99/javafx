package com.itgroup.utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utility {

    public static final String FXML_PATH = "/com/itgroup/fxml/";
    public static final String IMAGE_PATH = "/com/itgroup/images/";

    public static final int MAX_RATING = 10;

    private static Set<String> nationSet = new LinkedHashSet<>();

    static {
        nationSet.add("한국");
        nationSet.add("미국");
        nationSet.add("영국");
        nationSet.add("일본");
        nationSet.add("프랑스");
    }

    public static Set<String> getNationList() {
        return Collections.unmodifiableSet(nationSet);
    }

    public static void showAlert(Alert.AlertType alertType, String title, String header, String context) {
        createAlert(alertType, title, header, context).show();
    }

    public static Alert createAlert(Alert.AlertType alertType, String title, String header, String context) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);

        return alert;
    }

    public static FXMLLoader getFXMLLoader(String fxmlFile) {
        String path = Utility.FXML_PATH + fxmlFile;
        FXMLLoader fxmlLoader = new FXMLLoader(Utility.class.getResource(path));
        return fxmlLoader;
    }

    public static Parent loadFXML(FXMLLoader fxmlLoader) {
        Parent parent = null;

        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parent;
    }

    public static Parent loadFXML(String fxmlFile) {
        FXMLLoader fxmlLoader = getFXMLLoader(fxmlFile);
        Parent parent = null;

        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parent;
    }

    public static Stage craeteStage(String fxmlFile, String title, boolean resizable) {
        Stage stage = new Stage();
        stage.setScene(new Scene(loadFXML(fxmlFile)));
        stage.setResizable(resizable);
        stage.setTitle(title);

        return stage;
    }

    public static Stage craeteStage(Parent parent, String title, boolean resizable) {
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.setResizable(resizable);
        stage.setTitle(title);

        return stage;
    }

    /**
     * @return yyyy-MM-dd 형식의 LocalDate
     */
    public static LocalDate getDatePickerValue(String inputdate) {
        if(inputdate == null || inputdate.isEmpty()) {
            return null;
        }

        String[] split = inputdate.split("-");
        return LocalDate.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    }

    /**
     * @return yyyy/MM/dd 형식의 String
     */
    public static String getDatePickerValueToString(LocalDate inputdate) {
        return inputdate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    public static void closeWindow(ActionEvent event) {
        Node source = (Node)event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static boolean isNotValidLength(String str, int ls, int gs) {
        return str.length() < ls || str.length() > gs;
    }
}

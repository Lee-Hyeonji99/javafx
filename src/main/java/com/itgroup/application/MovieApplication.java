package com.itgroup.application;

import com.itgroup.utility.Utility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MovieApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage = Utility.craeteStage("movie.fxml", "영화 관람평 관리 프로그램", false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

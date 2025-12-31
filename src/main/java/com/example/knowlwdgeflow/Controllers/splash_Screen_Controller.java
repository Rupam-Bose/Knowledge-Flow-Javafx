package com.example.knowlwdgeflow.Controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class splash_Screen_Controller {
    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {

        PauseTransition delay = new PauseTransition(Duration.seconds(2));

        delay.setOnFinished(event -> openNextScreen());

        delay.play();
    }

    private void openNextScreen() {
        try {
            Parent nextRoot = FXMLLoader.load(getClass().getResource("/fxml/welcome.fxml"));
            Scene nextScene = new Scene(nextRoot);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(nextScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

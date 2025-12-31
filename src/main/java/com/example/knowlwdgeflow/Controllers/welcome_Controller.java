package com.example.knowlwdgeflow.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class welcome_Controller {
    @FXML
    private StackPane rootPane;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> openScene("/fxml/login.fxml"));
        signupButton.setOnAction(e -> openScene("/fxml/signup.fxml"));
    }

    private void openScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene next = new Scene(root);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(next);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

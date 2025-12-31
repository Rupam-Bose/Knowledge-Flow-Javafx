package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class login_Controller {
    @FXML
    private StackPane rootPane;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Prevent autofocus on fields by clearing initial focus state.
        emailField.setFocusTraversable(false);
        passwordField.setFocusTraversable(false);
        loginButton.setOnAction(e -> handleLogin());
        signupButton.setOnAction(e -> openScene("/fxml/signup.fxml"));
    }

    private void handleLogin() {
        errorLabel.setText("");
        try {
            User user = authService.login(emailField.getText(), passwordField.getText());
            openMainProfile(user);
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }
    }

    private void openMainProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainProfile.fxml"));
            Parent root = loader.load();
            mainProfile_Controller controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Unable to load profile");
        }
    }

    private void openScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Navigation failed");
        }
    }
}

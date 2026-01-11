package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.AuthService;
import com.example.knowlwdgeflow.service.WindowService;
import com.example.knowlwdgeflow.service.SessionService;
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
    private final WindowService windowService = new WindowService();
    private final SessionService sessionService = new SessionService();

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
            Stage stage = (Stage) rootPane.getScene().getWindow();
            // persist session for auto-login
            sessionService.saveUserId(user.getId());
            // switch and get controller to pass user data
            mainProfile_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/mainProfile.fxml");
            if (controller != null) {
                controller.setUser(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Unable to load profile: " + ex.getMessage());
        }
    }

    private void openScene(String fxmlPath) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            windowService.switchScene(stage, fxmlPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Navigation failed");
        }
    }
}

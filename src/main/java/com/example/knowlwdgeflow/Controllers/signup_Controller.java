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
import javafx.stage.Stage;

public class signup_Controller {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signupButton;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        signupButton.setOnAction(e -> handleSignup());
    }

    private void handleSignup() {
        errorLabel.setText("");
        try {
            User user = authService.signup(nameField.getText(), emailField.getText(), passwordField.getText());
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
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Unable to load profile");
        }
    }
}

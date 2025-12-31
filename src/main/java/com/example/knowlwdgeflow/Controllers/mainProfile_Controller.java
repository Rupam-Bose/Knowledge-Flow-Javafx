package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class mainProfile_Controller {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;

    public void setUser(User user) {
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
        }
    }
}

package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class askQuestion_Controller {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private TextField questionField;
    @FXML
    private javafx.scene.image.ImageView questionImageView;

    private User currentUser;

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
        if (rootPane != null) {
            rootPane.requestFocus();
        }
        loadQuestionImage();
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser();
    }

    @FXML
    private void handleProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainProfile.fxml"));
            Parent root = loader.load();
            mainProfile_Controller controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuestionImage() {
        try {
            var is = getClass().getResourceAsStream("/Images/question.jpg");
            if (is != null) {
                questionImageView.setImage(new Image(is));
            }
        } catch (Exception ignored) {
        }
    }

    private void setProfileImageFromUser() {
        try {
            if (currentUser != null && currentUser.getProfileImage() != null && currentUser.getProfileImage().length > 0) {
                profileImageView.setImage(new Image(new ByteArrayInputStream(currentUser.getProfileImage())));
                return;
            }
            try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                if (is != null) {
                    profileImageView.setImage(new Image(is));
                }
            }
        } catch (Exception ignored) {
        }
    }
}

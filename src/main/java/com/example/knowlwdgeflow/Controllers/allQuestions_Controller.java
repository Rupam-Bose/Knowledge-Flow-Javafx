package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class allQuestions_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;

    private User currentUser;

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
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

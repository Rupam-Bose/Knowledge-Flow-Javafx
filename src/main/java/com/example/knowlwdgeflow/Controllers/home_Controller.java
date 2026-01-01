package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class home_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private ListView<?> blogListView;

    private final SessionService sessionService = new SessionService();
    private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {
        // bind clip to keep avatar circular
        if (profileImageView != null && profileClip != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
            profileImageView.setOnMouseClicked(e -> openProfile());
            profileImageView.setStyle("-fx-cursor: hand;");
        }
        loadProfileAvatar();
    }

    private void loadProfileAvatar() {
        try {
            Integer userId = sessionService.getSavedUserId();
            if (userId == null) {
                setPlaceholderAvatar();
                return;
            }
            User user = userDao.findById(userId);
            if (user != null && user.getProfileImage() != null && user.getProfileImage().length > 0) {
                profileImageView.setImage(new Image(new ByteArrayInputStream(user.getProfileImage())));
            } else {
                setPlaceholderAvatar();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setPlaceholderAvatar();
        }
    }

    private void setPlaceholderAvatar() {
        try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
            if (is != null) {
                profileImageView.setImage(new Image(is));
            }
        } catch (Exception ignored) {
            // ignore placeholder failure
        }
    }

    private void openProfile() {
        try {
            Integer userId = sessionService.getSavedUserId();
            User user = userId != null ? userDao.findById(userId) : null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainProfile.fxml"));
            Parent root = loader.load();
            var controller = loader.getController();
            if (controller instanceof mainProfile_Controller profileController && user != null) {
                profileController.setUser(user);
            }
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

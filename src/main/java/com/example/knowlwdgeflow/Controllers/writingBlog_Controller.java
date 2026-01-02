package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.prefs.Preferences;

public class writingBlog_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    @FXML
    private Button saveDraftButton;
    @FXML
    private Button publishButton;
    @FXML
    private AnchorPane rootPane;

    private User currentUser;
    private final Preferences draftPrefs = Preferences.userRoot().node("com.example.knowlwdgeflow.drafts");
    private final com.example.knowlwdgeflow.dao.BlogDao blogDao = new com.example.knowlwdgeflow.dao.BlogDao();

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
        if (profileImageView != null) {
            profileImageView.setOnMouseClicked(e -> navigateBackToProfile());
        }
        if (saveDraftButton != null) {
            saveDraftButton.setOnAction(e -> saveDraft());
        }
        if (publishButton != null) {
            publishButton.setOnAction(e -> publish());
        }
        // Remove initial focus from title by focusing root
        if (rootPane != null) {
            rootPane.requestFocus();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser();
        loadDraft();
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

    private void navigateBackToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainProfile.fxml"));
            Parent root = loader.load();
            mainProfile_Controller controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String draftKey(String field) {
        int id = currentUser != null ? currentUser.getId() : 0;
        return "user_" + id + "_" + field;
    }

    private void saveDraft() {
        if (titleField == null || contentArea == null) {
            return;
        }
        draftPrefs.put(draftKey("title"), titleField.getText() == null ? "" : titleField.getText());
        draftPrefs.put(draftKey("content"), contentArea.getText() == null ? "" : contentArea.getText());
    }

    private void loadDraft() {
        if (titleField == null || contentArea == null) {
            return;
        }
        String savedTitle = draftPrefs.get(draftKey("title"), "");
        String savedContent = draftPrefs.get(draftKey("content"), "");
        titleField.setText(savedTitle);
        contentArea.setText(savedContent);
    }

    private void publish() {
        if (currentUser == null || titleField == null || contentArea == null) {
            return;
        }
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String content = contentArea.getText() == null ? "" : contentArea.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            return;
        }
        try {
            blogDao.insert(currentUser.getId(), title, content);
            draftPrefs.remove(draftKey("title"));
            draftPrefs.remove(draftKey("content"));
            goHome();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) publishButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

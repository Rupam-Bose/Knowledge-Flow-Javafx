package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.QuestionDao;
import com.example.knowlwdgeflow.model.Question;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.WindowService;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    @FXML
    private Button askNowButton;

    private User currentUser;
    private final QuestionDao questionDao = new QuestionDao();
    private final WindowService windowService = new WindowService();
    private final SessionService sessionService = new SessionService();

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
        if (askNowButton != null) {
            askNowButton.setOnAction(e -> handleAskNow());
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser();
    }

    @FXML
    private void handleProfileClick() {
        openProfile();
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

    private void handleAskNow() {
        try {
            String text = questionField != null ? questionField.getText() : "";
            if (text == null || text.trim().isEmpty()) {
                showAlert("Please enter a question.");
                return;
            }
            if (currentUser == null) {
                showAlert("Please log in to ask a question.");
                return;
            }
            Question saved = questionDao.insert(currentUser.getId(), text.trim());
            // clear field and go to all questions
            if (questionField != null) {
                questionField.clear();
            }
            openAllQuestions();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Unable to post question: " + ex.getMessage());
        }
    }

    private void openProfile() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            mainProfile_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/mainProfile.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAllQuestions() {
        try {
            Stage stage = (Stage) askNowButton.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        // Alerts removed entirely; intentionally no-op
    }

    @FXML
    private void handleHome() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWritingBlog() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            writingBlog_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/writingBlog.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAskQuestion() {
        // Already here
    }

    @FXML
    private void handleAllQuestions() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookmarks() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            bookmarks_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/bookmarks.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            sessionService.clear();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/welcome.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

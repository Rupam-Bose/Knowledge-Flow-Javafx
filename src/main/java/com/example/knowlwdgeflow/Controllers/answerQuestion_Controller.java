package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.AnswerDao;
import com.example.knowlwdgeflow.model.Answer;
import com.example.knowlwdgeflow.model.Question;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.SessionService;
import com.example.knowlwdgeflow.service.WindowService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class answerQuestion_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private Label questionAuthor;
    @FXML
    private Label questionText;
    @FXML
    private Label questionMeta;
    @FXML
    private TextArea answerField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<Answer> answersListView;
    @FXML
    private ImageView questionAuthorImage;
    @FXML
    private Circle questionClip;

    private User currentUser;
    private Question currentQuestion;
    private final AnswerDao answerDao = new AnswerDao();
    private final WindowService windowService = new WindowService();
    private final SessionService sessionService = new SessionService();

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
        if (answersListView != null) {
            answersListView.setFocusTraversable(false);
            answersListView.setCellFactory(list -> new AnswerCell());
        }
        if (questionClip != null && questionAuthorImage != null) {
            questionClip.centerXProperty().bind(questionAuthorImage.fitWidthProperty().divide(2));
            questionClip.centerYProperty().bind(questionAuthorImage.fitHeightProperty().divide(2));
            questionAuthorImage.setClip(questionClip);
        }
        if (sendButton != null) {
            sendButton.setOnAction(evt -> handleSend());
        }
        setProfileImageFromUser();
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser();
    }

    public void setQuestion(Question question) {
        this.currentQuestion = question;
        renderQuestion();
        loadAnswers();
    }

    @FXML
    private void handleProfileClick() {
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

    @FXML
    private void handleHome() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/Home.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWritingBlog() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
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
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookmarks() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            bookmarks_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/bookmarks.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception e) {
            // TODO: replace with real logging
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            sessionService.clear();
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/welcome.fxml");
        } catch (Exception e) {
            // TODO: replace with real logging
            e.printStackTrace();
        }
    }

    private void handleSend() {
        try {
            if (currentQuestion == null || currentUser == null) {
                showAlert("Missing question or user.");
                return;
            }
            String content = answerField != null ? answerField.getText() : "";
            if (content == null || content.trim().isEmpty()) {
                showAlert("Please write an answer.");
                return;
            }
            answerDao.insert(currentQuestion.getId(), currentUser.getId(), content.trim());
            if (answerField != null) {
                answerField.clear();
            }
            loadAnswers();
        } catch (Exception ex) {
            // TODO: replace with real logging
            ex.printStackTrace();
            showAlert("Unable to send answer: " + ex.getMessage());
        }
    }

    private void renderQuestion() {
        if (currentQuestion == null) return;
        if (questionAuthor != null) questionAuthor.setText(currentQuestion.getAuthorName());
        if (questionText != null) questionText.setText(currentQuestion.getText());
        if (questionMeta != null) {
            String ts = currentQuestion.getCreatedAt() != null ? currentQuestion.getCreatedAt().toString().replace('T', ' ') : "";
            questionMeta.setText(ts + "  •  " + currentQuestion.getAuthorEmail());
        }
        if (questionAuthorImage != null) {
            try {
                if (currentQuestion.getAuthorImage() != null && currentQuestion.getAuthorImage().length > 0) {
                    questionAuthorImage.setImage(new Image(new ByteArrayInputStream(currentQuestion.getAuthorImage())));
                } else {
                    try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                        if (is != null) questionAuthorImage.setImage(new Image(is));
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void loadAnswers() {
        if (answersListView == null || currentQuestion == null) return;
        try {
            var answers = FXCollections.observableArrayList(answerDao.findByQuestion(currentQuestion.getId()));
            answersListView.setItems(answers);
        } catch (Exception ex) {
            // TODO: replace with real logging
            ex.printStackTrace();
        }
    }

    private void setProfileImageFromUser() {
        try {
            if (profileImageView == null) return;
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

    private void showAlert(String msg) {
        // Alerts removed entirely; intentionally no-op
    }

    private static class AnswerCell extends javafx.scene.control.ListCell<Answer> {
        @Override
        protected void updateItem(Answer a, boolean empty) {
            super.updateItem(a, empty);
            if (empty || a == null) {
                setGraphic(null);
                return;
            }
            var card = new VBox(6);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10 12; -fx-border-color: #e6e9ef; -fx-border-radius: 10;");

            var row = new HBox(8);
            var avatar = new ImageView();
            avatar.setFitHeight(28);
            avatar.setFitWidth(28);
            var clip = new Circle(14, 14, 14);
            avatar.setClip(clip);
            if (a.getAuthorImage() != null && a.getAuthorImage().length > 0) {
                avatar.setImage(new Image(new ByteArrayInputStream(a.getAuthorImage())));
            } else {
                try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                    if (is != null) avatar.setImage(new Image(is));
                } catch (Exception ignored) {}
            }
            var name = new Label(a.getAuthorName());
            name.setStyle("-fx-text-fill: #566072;");
            var ts = a.getCreatedAt() != null ? a.getCreatedAt().toString().replace('T', ' ') : "";
            var time = new Label(ts);
            time.setStyle("-fx-text-fill: #9aa2b1;");
            row.getChildren().addAll(avatar, name, new Label("•"), time);

            var body = new Label(a.getContent());
            body.setWrapText(true);
            body.setStyle("-fx-text-fill: #222b3a;");

            card.getChildren().setAll(row, body);
            setGraphic(card);
        }
    }
}

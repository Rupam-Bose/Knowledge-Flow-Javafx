package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.QuestionDao;
import com.example.knowlwdgeflow.model.Question;
import com.example.knowlwdgeflow.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class allQuestions_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private javafx.scene.control.ListView<Question> questionsListView;

    private User currentUser;
    private final QuestionDao questionDao = new QuestionDao();
    private final java.util.Map<Integer, Boolean> likedMap = new java.util.HashMap<>();
    private final java.util.Map<Integer, Integer> likeCounts = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
        if (questionsListView != null) {
            questionsListView.setFocusTraversable(false);
            questionsListView.setCellFactory(list -> new QuestionCell());
        }
        loadQuestions();
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser();
        loadQuestions();
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

    private void loadQuestions() {
        if (questionsListView == null) return;
        try {
            var items = FXCollections.observableArrayList(questionDao.findRecent(50));
            items.forEach(q -> likeCounts.putIfAbsent(q.getId(), 0));
            questionsListView.setItems(items);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class QuestionCell extends javafx.scene.control.ListCell<Question> {
        @Override
        protected void updateItem(Question q, boolean empty) {
            super.updateItem(q, empty);
            if (empty || q == null) {
                setGraphic(null);
                return;
            }
            var card = new javafx.scene.layout.VBox(8);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 14 16; -fx-border-color: #e6e9ef; -fx-border-radius: 12;");

            var authorRow = new javafx.scene.layout.HBox(10);
            var avatar = new javafx.scene.image.ImageView();
            avatar.setFitHeight(32);
            avatar.setFitWidth(32);
            var clip = new javafx.scene.shape.Circle(16, 16, 16);
            avatar.setClip(clip);
            if (q.getAuthorImage() != null && q.getAuthorImage().length > 0) {
                avatar.setImage(new javafx.scene.image.Image(new java.io.ByteArrayInputStream(q.getAuthorImage())));
            } else {
                try (java.io.InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                    if (is != null) avatar.setImage(new javafx.scene.image.Image(is));
                } catch (Exception ignored) {}
            }
            var author = new javafx.scene.control.Label(q.getAuthorName());
            author.setStyle("-fx-text-fill: #566072;");
            var dot = new javafx.scene.control.Label("•");
            dot.setStyle("-fx-text-fill: #9aa2b1;");
            var ts = q.getCreatedAt() != null ? q.getCreatedAt().toString().replace('T', ' ') : "";
            var time = new javafx.scene.control.Label(ts);
            time.setStyle("-fx-text-fill: #9aa2b1;");
            authorRow.getChildren().addAll(avatar, author, dot, time);

            var text = new javafx.scene.control.Label(q.getText());
            text.setWrapText(true);
            text.setStyle("-fx-text-fill: #444a59; -fx-font-size: 14px;");

            var actions = new javafx.scene.layout.HBox(12);
            actions.setAlignment(Pos.CENTER_LEFT);

            int qid = q.getId();
            var likeBtn = new javafx.scene.control.Button();
            likeBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #d2d7e4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 6 10;");
            var likeIcon = new ImageView();
            likeIcon.setFitHeight(16);
            likeIcon.setFitWidth(16);
            likeBtn.setGraphic(likeIcon);
            var likeCount = new javafx.scene.control.Label(String.valueOf(likeCounts.getOrDefault(qid, 0)));
            likeCount.setStyle("-fx-text-fill: #566072;");
            updateLikeVisual(qid, likeIcon, likeCount);
            likeBtn.setOnAction(e -> {
                boolean liked = likedMap.getOrDefault(qid, false);
                int cnt = likeCounts.getOrDefault(qid, 0);
                cnt = liked ? Math.max(0, cnt - 1) : cnt + 1;
                likedMap.put(qid, !liked);
                likeCounts.put(qid, cnt);
                updateLikeVisual(qid, likeIcon, likeCount);
            });
            var likeBox = new javafx.scene.layout.HBox(6, likeBtn, likeCount);
            likeBox.setAlignment(Pos.CENTER_LEFT);

            var answerBtn = new Button("Answer");
            answerBtn.setStyle("-fx-background-color: #1f7aff; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 12;");
            answerBtn.setOnAction(e -> openAnswerScreen(q));

            actions.getChildren().addAll(likeBox, answerBtn);

            card.getChildren().setAll(authorRow, text, actions);
            setGraphic(card);
        }
    }

    private void updateLikeVisual(int questionId, ImageView icon, Label countLabel) {
        boolean liked = likedMap.getOrDefault(questionId, false);
        String path = liked ? "/Images/heart_red.png" : "/Images/heart_black.png";
        try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) icon.setImage(new javafx.scene.image.Image(is));
        } catch (Exception ignored) {}
        countLabel.setText(String.valueOf(likeCounts.getOrDefault(questionId, 0)));
    }

    private void openAnswerScreen(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/answerQuestion.fxml"));
            Parent root = loader.load();
            var controller = loader.getController();
            if (controller instanceof answerQuestion_Controller answerController) {
                answerController.setUser(currentUser);
                answerController.setQuestion(question);
            }
            Stage stage = (Stage) questionsListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

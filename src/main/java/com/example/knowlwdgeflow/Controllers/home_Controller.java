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
    private ListView<com.example.knowlwdgeflow.model.Blog> blogListView;

    private final SessionService sessionService = new SessionService();
    private final UserDao userDao = new UserDao();
    private final com.example.knowlwdgeflow.dao.BlogDao blogDao = new com.example.knowlwdgeflow.dao.BlogDao();

    private final java.util.Map<Integer, Boolean> likedMap = new java.util.HashMap<>();
    private final java.util.Map<Integer, Integer> likeCounts = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        // bind clip to keep avatar circular
        if (profileImageView != null && profileClip != null) {
           profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
           profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
           profileImageView.setOnMouseClicked(e -> openProfile());
           profileImageView.setClip(profileClip);
        }
        if (blogListView != null) {
            blogListView.setFocusTraversable(false);
            blogListView.setCellFactory(list -> new BlogCell());
        }
        loadProfileAvatar();
        loadBlogs();
    }

    private void loadBlogs() {
        if (blogListView == null) return;
        try {
            var blogs = javafx.collections.FXCollections.observableArrayList(blogDao.findRecent(50));
            blogs.forEach(b -> likeCounts.putIfAbsent(b.getId(), 0));
            blogListView.setItems(blogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    class BlogCell extends javafx.scene.control.ListCell<com.example.knowlwdgeflow.model.Blog> {
        @Override
        protected void updateItem(com.example.knowlwdgeflow.model.Blog blog, boolean empty) {
            super.updateItem(blog, empty);
            if (empty || blog == null) {
                setGraphic(null);
                return;
            }
            var card = new javafx.scene.layout.VBox(8);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 14 16; -fx-border-color: #e6e9ef; -fx-border-radius: 12;");

            var title = new javafx.scene.control.Label(blog.getTitle());
            title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #222b3a;");

            var authorRow = new javafx.scene.layout.HBox(10);
            var avatar = new javafx.scene.image.ImageView();
            avatar.setFitHeight(32);
            avatar.setFitWidth(32);
            var clip = new javafx.scene.shape.Circle(16, 16, 16);
            avatar.setClip(clip);
            if (blog.getAuthorImage() != null && blog.getAuthorImage().length > 0) {
                avatar.setImage(new javafx.scene.image.Image(new java.io.ByteArrayInputStream(blog.getAuthorImage())));
            } else {
                try (java.io.InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                    if (is != null) avatar.setImage(new javafx.scene.image.Image(is));
                } catch (Exception ignored) {}
            }
            var author = new javafx.scene.control.Label(blog.getAuthorName());
            author.setStyle("-fx-text-fill: #566072;");
            var dot = new javafx.scene.control.Label("•");
            dot.setStyle("-fx-text-fill: #9aa2b1;");
            var ts = blog.getCreatedAt() != null ? blog.getCreatedAt().toString().replace('T', ' ') : "";
            var time = new javafx.scene.control.Label(ts);
            time.setStyle("-fx-text-fill: #9aa2b1;");
            authorRow.getChildren().addAll(avatar, author, dot, time);

            var snippet = new javafx.scene.control.Label(truncate(blog.getContent(), 240));
            snippet.setWrapText(true);
            snippet.setStyle("-fx-text-fill: #444a59;");

            var actions = new javafx.scene.layout.HBox(12);
            var readMore = new javafx.scene.control.Button("Read more");
            readMore.setStyle("-fx-background-color: #1f7aff; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 12;");

            int blogId = blog.getId();
            var likeBtn = new javafx.scene.control.Button();
            likeBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #d2d7e4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 6 10;");
            var likeIcon = new javafx.scene.image.ImageView();
            likeIcon.setFitHeight(16);
            likeIcon.setFitWidth(16);
            likeBtn.setGraphic(likeIcon);
            var likeCount = new javafx.scene.control.Label(String.valueOf(likeCounts.getOrDefault(blogId, 0)));
            likeCount.setStyle("-fx-text-fill: #566072;");

            updateLikeVisual(blogId, likeIcon, likeCount);
            likeBtn.setOnAction(e -> {
                boolean liked = likedMap.getOrDefault(blogId, false);
                int cnt = likeCounts.getOrDefault(blogId, 0);
                if (liked) {
                    cnt = Math.max(0, cnt - 1);
                } else {
                    cnt += 1;
                }
                likedMap.put(blogId, !liked);
                likeCounts.put(blogId, cnt);
                updateLikeVisual(blogId, likeIcon, likeCount);
            });

            var likeBox = new javafx.scene.layout.HBox(6, likeBtn, likeCount);
            likeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            var commentBtn = iconButton("/Images/comments.png");
            var commentCount = new javafx.scene.control.Label("0");
            commentCount.setStyle("-fx-text-fill: #566072;");
            var commentBox = new javafx.scene.layout.HBox(6, commentBtn, commentCount);
            commentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            var bookmarkBtn = iconButton("/Images/bookmark_black.png");
            var bookmarkBox = new javafx.scene.layout.HBox(6, bookmarkBtn);
            bookmarkBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            actions.getChildren().addAll(readMore, likeBox, commentBox, bookmarkBox);
            actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            card.getChildren().setAll(title, authorRow, snippet, actions);
            setGraphic(card);
        }

        private void updateLikeVisual(int blogId, javafx.scene.image.ImageView icon, javafx.scene.control.Label countLabel) {
            boolean liked = likedMap.getOrDefault(blogId, false);
            String path = liked ? "/Images/heart_red.png" : "/Images/heart_black.png";
            try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) icon.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
            countLabel.setText(String.valueOf(likeCounts.getOrDefault(blogId, 0)));
        }

        private javafx.scene.control.Button iconButton(String path) {
            var btn = new javafx.scene.control.Button();
            btn.setStyle("-fx-background-color: transparent; -fx-border-color: #d2d7e4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 6 10;");
            var iv = new javafx.scene.image.ImageView();
            iv.setFitHeight(16);
            iv.setFitWidth(16);
            try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) iv.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
            btn.setGraphic(iv);
            return btn;
        }

        private String truncate(String text, int max) {
            if (text == null) return "";
            if (text.length() <= max) return text;
            return text.substring(0, max) + "...";
        }
    }
}

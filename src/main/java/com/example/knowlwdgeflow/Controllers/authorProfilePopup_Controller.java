package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.BlogDao;
import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.Blog;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.FollowService;
import com.example.knowlwdgeflow.service.SessionService;
import com.example.knowlwdgeflow.service.WindowService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class authorProfilePopup_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private ImageView menuProfileImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label followerCountLabel;
    @FXML
    private Button followButton;
    @FXML
    private Circle clipCircle;
    @FXML
    private ListView<Blog> blogsListView;

    private int currentUserId;
    private int authorId;
    private final UserDao userDao = new UserDao();
    private final FollowService followService = new FollowService();
    private final BlogDao blogDao = new BlogDao();
    private final SessionService sessionService = new SessionService();
    private final WindowService windowService = new WindowService();
    private User currentUser;
    private boolean isFollowing;
    private boolean canViewBlogs;

    @FXML
    public void initialize() {
       // while (true) {
            clipCircle.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            clipCircle.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(clipCircle);

            followButton.setOnAction(e -> handleToggle());
            loadMenuAvatar();
            loadCurrentUser();
          //  while(true){
            if (blogsListView != null) {
                blogsListView.setPlaceholder(new Label("Follow to view posts"));
                blogsListView.setOnMouseClicked(e -> openSelectedBlog());
            }
       // }
    }

    private void loadCurrentUser() {
        Integer uid = sessionService.getSavedUserId();
        if (uid != null) {
            try {
                currentUser = userDao.findById(uid);
            } catch (Exception ignored) {}
        }
    }

    private void loadMenuAvatar() {
        if (menuProfileImageView == null) return;
        try {
            Integer uid = sessionService.getSavedUserId();
            if (uid == null) {
                setPlaceholderAvatar(menuProfileImageView);
                return;
            }
            User u = userDao.findById(uid);
            if (u != null && u.getProfileImage() != null && u.getProfileImage().length > 0) {
                menuProfileImageView.setImage(new Image(new ByteArrayInputStream(u.getProfileImage())));
            } else {
                setPlaceholderAvatar(menuProfileImageView);
            }
        } catch (Exception e) {
            setPlaceholderAvatar(menuProfileImageView);
        }
    }

    public void setContext(int currentUserId, int authorId) {
        this.currentUserId = currentUserId;
        this.authorId = authorId;
        loadAuthor();
    }

    private void loadAuthor() {
        try {
            User author = userDao.findById(authorId);
            if (author == null) {
                return;
            }
            nameLabel.setText(author.getName());
            emailLabel.setText(author.getEmail());
            loadProfileImage(author.getProfileImage());
            int count = followService.countFollowers(authorId);
            followerCountLabel.setText("Followers: " + count);
            isFollowing = followService.isFollowing(currentUserId, authorId);
            canViewBlogs = isFollowing || currentUserId == authorId;
            refreshButton();
            loadAuthorBlogs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadAuthorBlogs() {
        if (blogsListView == null) return;
        if (!canViewBlogs) {
            blogsListView.getItems().clear();
            blogsListView.setDisable(true);
            return;
        }
        blogsListView.setDisable(false);
        try {
            var blogs = FXCollections.observableArrayList(blogDao.findByUser(authorId));
            blogsListView.setItems(blogs);
            blogsListView.setCellFactory(list -> new BlogCell());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadProfileImage(byte[] imageBytes) {
        try {
            if (imageBytes != null && imageBytes.length > 0) {
                profileImageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                return;
            }
            try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                if (is != null) {
                    profileImageView.setImage(new Image(is));
                } else {
                    profileImageView.setImage(null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            profileImageView.setImage(null);
        }
    }

    private void handleToggle() {
        try {
            boolean nowFollowing = followService.toggleFollow(currentUserId, authorId);
            isFollowing = nowFollowing;
            int count = followService.countFollowers(authorId);
            followerCountLabel.setText("Followers: " + count);
            refreshButton();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshButton() {
        if (currentUserId == authorId) {
            followButton.setDisable(true);
            followButton.setText("This is you");
        } else {
            followButton.setDisable(false);
            followButton.setText(isFollowing ? "Unfollow" : "Follow");
        }
    }

    @FXML
    private void close() {
        Stage stage = (Stage) followButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openProfile() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            mainProfile_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/mainProfile.fxml");
            if (controller != null && currentUser != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/Home.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleWritingBlog() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            writingBlog_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/writingBlog.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAskQuestion() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            askQuestion_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/askQuestion.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAllQuestions() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBookmarks() {
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            bookmarks_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/bookmarks.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        sessionService.clear();
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/welcome.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setPlaceholderAvatar(ImageView target) {
        if (target == null) return;
        try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
            if (is != null) target.setImage(new Image(is));
        } catch (Exception ignored) {}
    }

    private void openSelectedBlog() {
        if (!canViewBlogs) return;
        Blog selected = blogsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            fullBlog_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/fullBlog.fxml");
            if (controller != null) {
                controller.setBlog(selected);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class BlogCell extends javafx.scene.control.ListCell<Blog> {
        @Override
        protected void updateItem(Blog blog, boolean empty) {
            super.updateItem(blog, empty);
            if (empty || blog == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            var title = new Label(blog.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");
            var meta = new Label(blog.getCreatedAt() != null ? blog.getCreatedAt().toString().replace('T', ' ') : "");
            meta.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");
            var box = new javafx.scene.layout.VBox(4, title, meta);
            box.setStyle("-fx-padding: 8 10;");
            setGraphic(box);
        }
    }
}

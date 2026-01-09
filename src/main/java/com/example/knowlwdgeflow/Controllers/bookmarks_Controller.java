package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.BlogDao;
import com.example.knowlwdgeflow.dao.BookmarkDao;
import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.Bookmark;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class bookmarks_Controller {
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle profileClip;
    @FXML
    private ListView<Bookmark> bookmarksListView;

    private final SessionService sessionService = new SessionService();
    private final UserDao userDao = new UserDao();
    private final BookmarkDao bookmarkDao = new BookmarkDao();
    private final BlogDao blogDao = new BlogDao();
    private User currentUser;

    @FXML
    public void initialize() {
        if (profileClip != null && profileImageView != null) {
            profileClip.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
            profileClip.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
            profileImageView.setClip(profileClip);
        }
        if (bookmarksListView != null) {
            bookmarksListView.setCellFactory(list -> new BookmarkCell());
        }
        loadProfileAvatar();
        loadBookmarks();
    }

    public void setUser(User user) {
        this.currentUser = user;
        setProfileImageFromUser(user);
        loadBookmarks();
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

    private void loadBookmarks() {
        if (bookmarksListView == null) return;
        try {
            Integer userId = sessionService.getSavedUserId();
            if (userId == null) return;
            var list = javafx.collections.FXCollections.observableArrayList(bookmarkDao.findByUser(userId));
            bookmarksListView.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProfileAvatar() {
        try {
            Integer userId = sessionService.getSavedUserId();
            User user = userId != null ? userDao.findById(userId) : null;
            this.currentUser = user;
            setProfileImageFromUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            setPlaceholderAvatar();
        }
    }

    private void setProfileImageFromUser(User user) {
        try {
            if (user != null && user.getProfileImage() != null && user.getProfileImage().length > 0) {
                profileImageView.setImage(new Image(new ByteArrayInputStream(user.getProfileImage())));
                return;
            }
            setPlaceholderAvatar();
        } catch (Exception ignored) {
            setPlaceholderAvatar();
        }
    }

    private void setPlaceholderAvatar() {
        try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
            if (is != null) {
                profileImageView.setImage(new Image(is));
            }
        } catch (Exception ignored) {
        }
    }

    private class BookmarkCell extends ListCell<Bookmark> {
        @Override
        protected void updateItem(Bookmark b, boolean empty) {
            super.updateItem(b, empty);
            if (empty || b == null) {
                setGraphic(null);
                return;
            }
            var card = new javafx.scene.layout.VBox(6);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 12 14; -fx-border-color: #e6e9ef; -fx-border-radius: 12;");

            var title = new javafx.scene.control.Label(b.getTitle());
            title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #222b3a;");

            var snippet = new Label(truncate(b.getContent(), 180));
            snippet.setWrapText(true);
            snippet.setStyle("-fx-text-fill: #444a59;");

            var meta = new HBox(8);
            var avatar = new ImageView();
            avatar.setFitHeight(28);
            avatar.setFitWidth(28);
            var clip = new Circle(14, 14, 14);
            avatar.setClip(clip);
            if (b.getAuthorImage() != null && b.getAuthorImage().length > 0) {
                avatar.setImage(new Image(new ByteArrayInputStream(b.getAuthorImage())));
            } else {
                try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                    if (is != null) avatar.setImage(new Image(is));
                } catch (Exception ignored) {}
            }
            var author = new javafx.scene.control.Label(b.getAuthorName());
            author.setStyle("-fx-text-fill: #566072;");
            var dot = new javafx.scene.control.Label("•");
            dot.setStyle("-fx-text-fill: #9aa2b1;");
            var ts = b.getCreatedAt() != null ? b.getCreatedAt().toString().replace('T', ' ') : "";
            var time = new javafx.scene.control.Label(ts);
            time.setStyle("-fx-text-fill: #9aa2b1;");
            meta.getChildren().addAll(avatar, author, dot, time);

            var open = new javafx.scene.control.Button("Read more");
            open.setStyle("-fx-background-color: #1f7aff; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 12;");
            open.setOnAction(e -> openFullBlog(b.getBlogId()));

            card.getChildren().setAll(title, meta, snippet, open);
            setGraphic(card);
        }

        private String truncate(String text, int max) {
            if (text == null) return "";
            if (text.length() <= max) return text;
            return text.substring(0, max) + "...";
        }
    }

    private void openFullBlog(int blogId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fullBlog.fxml"));
            Parent root = loader.load();
            var controller = loader.getController();
            if (controller instanceof fullBlog_Controller fullController) {
                try {
                    var blog = blogDao.findById(blogId);
                    if (blog != null) {
                        fullController.setBlog(blog);
                    }
                } catch (Exception ignored) {}
            }
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.LikeState;
import com.example.knowlwdgeflow.service.SessionService;
import com.example.knowlwdgeflow.service.BookmarkState;
import com.example.knowlwdgeflow.dao.BookmarkDao;
import com.example.knowlwdgeflow.dao.CommentDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private final BookmarkDao bookmarkDao = new BookmarkDao();
    private final CommentDao commentDao = new CommentDao();

    private final java.util.Map<Integer, Boolean> likedMap = new java.util.HashMap<>();

    private final LikeState likeState = LikeState.getInstance();
    private final Runnable likeListener = this::refreshVisibleLikes;
    private final BookmarkState bookmarkState = BookmarkState.getInstance();
    private final Runnable bookmarkListener = this::refreshVisibleLikes;

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
        likeState.addListener(likeListener);
        bookmarkState.addListener(bookmarkListener);
    }

    @FXML
    public void onClose() {
        likeState.removeListener(likeListener);
        bookmarkState.removeListener(bookmarkListener);
    }

    private void refreshVisibleLikes() {
        if (blogListView == null) return;
        blogListView.refresh();
    }

    private void loadBlogs() {
        if (blogListView == null) return;
        try {
            var blogs = javafx.collections.FXCollections.observableArrayList(blogDao.findRecent(50));
            blogs.forEach(b -> likeState.ensure(b.getId()));
            // preload bookmark state from DB for current user
            Integer userId = sessionService.getSavedUserId();
            if (userId != null) {
                for (var b : blogs) {
                    boolean marked = bookmarkDao.isBookmarked(userId, b.getId());
                    if (marked) bookmarkState.toggle(b.getId()); // sets to true if default false
                }
            }
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
            readMore.setOnAction(e -> openFullBlog(blog));

            int blogId = blog.getId();
            // Ensure bookmark state matches DB
            try {
                Integer userId = sessionService.getSavedUserId();
                if (userId != null && bookmarkDao.isBookmarked(userId, blogId)) {
                    if (!bookmarkState.isBookmarked(blogId)) {
                        bookmarkState.toggle(blogId);
                    }
                }
            } catch (Exception ignored) {}

            var likeBtn = new javafx.scene.control.Button();
            likeBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #d2d7e4; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 6 10;");
            var likeIcon = new javafx.scene.image.ImageView();
            likeIcon.setFitHeight(16);
            likeIcon.setFitWidth(16);
            likeBtn.setGraphic(likeIcon);
            var likeCount = new javafx.scene.control.Label(String.valueOf(likeState.getCount(blogId)));
            likeCount.setStyle("-fx-text-fill: #566072;");

            updateLikeVisual(blogId, likeIcon, likeCount);
            likeBtn.setOnAction(e -> {
                likeState.toggle(blogId);
                likedMap.put(blogId, likeState.isLiked(blogId));
                updateLikeVisual(blogId, likeIcon, likeCount);
            });
            var likeBox = new javafx.scene.layout.HBox(6, likeBtn, likeCount);
            likeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            var commentBtn = iconButton("/Images/comments.png");
            commentBtn.setOnAction(e -> openFullBlog(blog));
            var commentCount = new javafx.scene.control.Label();
            commentCount.setStyle("-fx-text-fill: #566072;");
            try {
                commentCount.setText(String.valueOf(commentDao.countByBlog(blogId)));
            } catch (Exception ignored) {
                commentCount.setText("0");
            }
            var commentBox = new javafx.scene.layout.HBox(6, commentBtn, commentCount);
            commentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            var bookmarkBtn = iconButton("/Images/bookmark_black.png");
            bookmarkBtn.setOnAction(e -> {
                Integer userId = sessionService.getSavedUserId();
                if (userId != null) {
                    try {
                        if (bookmarkState.isBookmarked(blogId)) {
                            bookmarkDao.removeBookmark(userId, blogId);
                        } else {
                            bookmarkDao.addBookmark(userId, blogId);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                bookmarkState.toggle(blogId);
                updateBookmarkVisual(blogId, (javafx.scene.image.ImageView) bookmarkBtn.getGraphic());
            });
            updateBookmarkVisual(blogId, (javafx.scene.image.ImageView) bookmarkBtn.getGraphic());
            var bookmarkBox = new javafx.scene.layout.HBox(6, bookmarkBtn);
            bookmarkBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            actions.getChildren().addAll(readMore, likeBox, commentBox, bookmarkBox);
            actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            card.getChildren().setAll(title, authorRow, snippet, actions);
            setGraphic(card);
        }

        private void updateLikeVisual(int blogId, javafx.scene.image.ImageView icon, javafx.scene.control.Label countLabel) {
            boolean liked = likeState.isLiked(blogId);
            String path = liked ? "/Images/heart_red.png" : "/Images/heart_black.png";
            try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) icon.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
            countLabel.setText(String.valueOf(likeState.getCount(blogId)));
        }

        private void updateBookmarkVisual(int blogId, javafx.scene.image.ImageView icon) {
            boolean marked = bookmarkState.isBookmarked(blogId);
            String path = marked ? "/Images/bookmark_blue.png" : "/Images/bookmark_black.png";
            try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) icon.setImage(new javafx.scene.image.Image(is));
            } catch (Exception ignored) {}
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

    private void openFullBlog(com.example.knowlwdgeflow.model.Blog blog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fullBlog.fxml"));
            Parent root = loader.load();
            var controller = loader.getController();
            if (controller instanceof fullBlog_Controller fullController) {
                fullController.setBlog(blog);
            }
            Stage stage = (Stage) blogListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

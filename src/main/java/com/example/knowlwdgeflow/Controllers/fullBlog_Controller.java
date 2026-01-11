package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.dao.CommentDao;
import com.example.knowlwdgeflow.model.Blog;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.model.Comment;
import com.example.knowlwdgeflow.service.LikeState;
import com.example.knowlwdgeflow.service.SessionService;
import com.example.knowlwdgeflow.service.BookmarkState;
import com.example.knowlwdgeflow.service.WindowService;
import com.example.knowlwdgeflow.service.FollowService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class fullBlog_Controller {

    @FXML
    private ImageView profileImageView;
    private final Circle clip = new Circle();

    @FXML
    private ImageView authorImageView;
    private final Circle authorClip = new Circle();

    @FXML
    private Label blogTitleLabel;
    @FXML
    private Label metaLabel;
    @FXML
    private Label contentLabel;

    @FXML
    private Button likeButton;
    @FXML
    private Label likeCountLabel;
    @FXML
    private ImageView likeIcon;

    @FXML
    private Button bookmarkButton;
    @FXML
    private ImageView bookmarkIcon;
    @FXML
    private ListView<Comment> commentsListView;
    @FXML
    private TextArea commentInput;
    @FXML
    private Label commentCountLabel;

    @FXML
    private Button commentButton;
    @FXML
    private ImageView commentIcon;

    @FXML
    private ScrollPane rootScroll;

    private Blog blog;
    private User currentUser; // track logged-in user for menu navigation
    private User author;

    private final SessionService sessionService = new SessionService();
    private final UserDao userDao = new UserDao();
    private final CommentDao commentDao = new CommentDao();
    private final LikeState likeState = LikeState.getInstance();
    private final BookmarkState bookmarkState = BookmarkState.getInstance();
    private final WindowService windowService = new WindowService();
    private final FollowService followService = new FollowService();
    private final Runnable likeListener = this::refreshLikeUi;
    private final Runnable bookmarkListener = this::refreshBookmarkUi;

    @FXML
    public void initialize() {
        if (profileImageView != null) {
            profileImageView.setCursor(Cursor.HAND);
            profileImageView.setClip(clip);
            profileImageView.fitWidthProperty().addListener((o1, o2, o3) -> updateClip());
            profileImageView.fitHeightProperty().addListener((o1, o2, o3) -> updateClip());
            updateClip();
            profileImageView.setOnMouseClicked(e -> openProfile());
            loadProfileAvatar();
        }
        if (authorImageView != null) {
            authorImageView.setClip(authorClip);
            authorImageView.fitWidthProperty().addListener((obs, o, n) -> updateAuthorClip());
            authorImageView.fitHeightProperty().addListener((obs, o, n) -> updateAuthorClip());
            authorImageView.setCursor(Cursor.HAND);
            authorImageView.setOnMouseClicked(this::openAuthorProfile);
            updateAuthorClip();
        }
        renderBlog();
        likeState.addListener(likeListener);
        bookmarkState.addListener(bookmarkListener);
    }

    @FXML
    public void onClose() {
        likeState.removeListener(likeListener);
        bookmarkState.removeListener(bookmarkListener);
    }

    private void updateClip() {
        if (profileImageView == null) return;
        clip.setCenterX(profileImageView.getFitWidth() / 2);
        clip.setCenterY(profileImageView.getFitHeight() / 2);
        clip.setRadius(Math.min(profileImageView.getFitWidth(), profileImageView.getFitHeight()) / 2);
    }

    private void updateAuthorClip() {
        if (authorImageView == null) return;
        authorClip.setCenterX(authorImageView.getFitWidth() / 2);
        authorClip.setCenterY(authorImageView.getFitHeight() / 2);
        authorClip.setRadius(Math.min(authorImageView.getFitWidth(), authorImageView.getFitHeight()) / 2);
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
        // keep current user from session for menu actions
        Integer uid = sessionService.getSavedUserId();
        if (uid != null) {
            try {
                this.currentUser = userDao.findById(uid);
            } catch (Exception ignored) {}
        }
        renderBlog();
        if (rootScroll != null) rootScroll.setVvalue(0);
    }

    private void renderBlog() {
        if (blog == null) return;
        if (blogTitleLabel != null) blogTitleLabel.setText(blog.getTitle());
        if (metaLabel != null) {
            String ts = blog.getCreatedAt() != null ? blog.getCreatedAt().toString().replace('T', ' ') : "";
            metaLabel.setText(blog.getAuthorName() + " • " + ts);
        }
        if (contentLabel != null) contentLabel.setText(blog.getContent());
        likeState.ensure(blog.getId());
        bookmarkState.ensure(blog.getId());
        refreshLikeUi();
        refreshBookmarkUi();
        loadAuthorAvatar();
        loadAuthorModel();
        loadComments();
    }

    private void loadProfileAvatar() {
        try {
            Integer userId = sessionService.getSavedUserId();
            if (userId == null) {
                setPlaceholderAvatar(profileImageView);
                return;
            }
            User user = userDao.findById(userId);
            if (user != null && user.getProfileImage() != null && user.getProfileImage().length > 0) {
                profileImageView.setImage(new Image(new ByteArrayInputStream(user.getProfileImage())));
            } else {
                setPlaceholderAvatar(profileImageView);
            }
        } catch (Exception e) {
            setPlaceholderAvatar(profileImageView);
        }
    }

    private void loadAuthorAvatar() {
        if (authorImageView == null || blog == null) return;
        byte[] bytes = blog.getAuthorImage();
        if (bytes != null && bytes.length > 0) {
            authorImageView.setImage(new Image(new ByteArrayInputStream(bytes)));
        } else {
            setPlaceholderAvatar(authorImageView);
        }
    }

    private void loadAuthorModel() {
        if (blog == null) return;
        try {
            author = userDao.findById(blog.getUserId());
        } catch (Exception ignored) {}
    }

    private void setPlaceholderAvatar(ImageView target) {
        if (target == null) return;
        try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
            if (is != null) {
                target.setImage(new Image(is));
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void openProfile() {
        try {
            Integer userId = sessionService.getSavedUserId();
            User user = userId != null ? userDao.findById(userId) : null;

            Stage stage = (Stage) profileImageView.getScene().getWindow();
            mainProfile_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/mainProfile.fxml");
            if (controller != null && user != null) {
                controller.setUser(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openAuthorProfile(MouseEvent event) {
        try {
            Integer currentUserId = sessionService.getSavedUserId();
            if (currentUserId == null || author == null) return;

            Stage stage = (Stage) rootScroll.getScene().getWindow();
            authorProfilePopup_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/authorProfilePopup.fxml");
            if (controller != null) {
                controller.setContext(currentUserId, author.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshLikeUi() {
        if (likeIcon == null || likeCountLabel == null || blog == null) return;
        boolean liked = likeState.isLiked(blog.getId());
        String path = liked ? "/Images/heart_red.png" : "/Images/heart_black.png";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) likeIcon.setImage(new Image(is));
        } catch (Exception ignored) {}
        likeCountLabel.setText(String.valueOf(likeState.getCount(blog.getId())));
    }

    private void refreshBookmarkUi() {
        if (bookmarkIcon == null || blog == null) return;
        boolean marked = bookmarkState.isBookmarked(blog.getId());
        String path = marked ? "/Images/bookmark_blue.png" : "/Images/bookmark_black.png";
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) bookmarkIcon.setImage(new Image(is));
        } catch (Exception ignored) {}
    }

    private void refreshCommentsUi(java.util.List<Comment> comments) {
        if (commentCountLabel != null) {
            commentCountLabel.setText(String.valueOf(comments == null ? 0 : comments.size()));
        }
    }

    private void loadComments() {
        if (commentsListView == null || blog == null) return;
        try {
            var comments = javafx.collections.FXCollections.observableArrayList(commentDao.findByBlog(blog.getId()));
            commentsListView.setCellFactory(list -> new CommentCell());
            commentsListView.setItems(comments);
            refreshCommentsUi(comments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLike(ActionEvent event) {
        if (blog == null) return;
        likeState.toggle(blog.getId());
        refreshLikeUi();
    }

    @FXML
    private void handleBookmark(ActionEvent event) {
        if (blog == null) return;
        bookmarkState.toggle(blog.getId());
        refreshBookmarkUi();
        // TODO: persist bookmarked blog to database/bookmarks list
    }

    @FXML
    private void handleSendComment(ActionEvent event) {
        if (blog == null || commentInput == null) return;
        String text = commentInput.getText() == null ? "" : commentInput.getText().trim();
        if (text.isEmpty()) return;
        try {
            Integer userId = sessionService.getSavedUserId();
            if (userId == null) return;
            commentDao.addComment(blog.getId(), userId, text);
            commentInput.clear();
            loadComments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick() {
        openProfile();
    }

    @FXML
    private void handleHome() {
        try {
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/Home.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleWritingBlog() {
        try {
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            writingBlog_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/writingBlog.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAskQuestion() {
        try {
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            askQuestion_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/askQuestion.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAllQuestions() {
        try {
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBookmarks() {
        try {
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            bookmarks_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/bookmarks.fxml");
            if (controller != null && currentUser != null) controller.setUser(currentUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            sessionService.clear();
            Stage stage = (Stage) rootScroll.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/welcome.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class CommentCell extends ListCell<Comment> {
        @Override
        protected void updateItem(Comment c, boolean empty) {
            super.updateItem(c, empty);
            if (empty || c == null) {
                setGraphic(null);
                return;
            }
            var box = new javafx.scene.layout.VBox(4);
            box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-padding: 8 10;");
            var header = new javafx.scene.layout.HBox(8);
            var avatar = new ImageView();
            avatar.setFitHeight(24);
            avatar.setFitWidth(24);
            var clip = new Circle(12, 12, 12);
            avatar.setClip(clip);
            if (c.getUserImage() != null && c.getUserImage().length > 0) {
                avatar.setImage(new Image(new ByteArrayInputStream(c.getUserImage())));
            } else {
                try (InputStream is = getClass().getResourceAsStream("/Images/knowledgeflowlogo.png")) {
                    if (is != null) avatar.setImage(new Image(is));
                } catch (Exception ignored) {}
            }
            var name = new Label(c.getUserName());
            name.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold;");
            var dot = new Label("•");
            dot.setStyle("-fx-text-fill: #9aa2b1;");
            var ts = c.getCreatedAt() != null ? c.getCreatedAt().toString().replace('T', ' ') : "";
            var time = new Label(ts);
            time.setStyle("-fx-text-fill: #9aa2b1;");
            header.getChildren().addAll(avatar, name, dot, time);

            var body = new Label(c.getContent());
            body.setWrapText(true);
            body.setStyle("-fx-text-fill: #374151;");

            box.getChildren().setAll(header, body);
            setGraphic(box);
        }
    }

    @FXML
    private void handleComment(ActionEvent event) {
        if (commentsListView != null) {
            commentsListView.requestFocus();
            commentsListView.scrollTo(0);
        }
    }
}

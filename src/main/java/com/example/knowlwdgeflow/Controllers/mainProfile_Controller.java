package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.AuthService;
import com.example.knowlwdgeflow.service.SessionService;
import com.example.knowlwdgeflow.service.WindowService;
import com.example.knowlwdgeflow.dao.UserDao;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class mainProfile_Controller {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Circle clipCircle;
    @FXML
    private Button uploadButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button writingBlogButton;
    @FXML
    private Button askQuestionButton;
    @FXML
    private Button questionsButton;
    @FXML
    private Button bookmarksButton;

    private User currentUser;
    private final AuthService authService = new AuthService();
    private final SessionService sessionService = new SessionService();
    private final WindowService windowService = new WindowService();
    private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {
        // Make image circular via clip
        clipCircle.centerXProperty().bind(profileImageView.fitWidthProperty().divide(2));
        clipCircle.centerYProperty().bind(profileImageView.fitHeightProperty().divide(2));
        profileImageView.setClip(clipCircle);

        uploadButton.setOnAction(e -> openImagePicker());

        if (logoutButton != null) {
            logoutButton.setOnAction(e -> handleLogout());
        }
        if (homeButton != null) {
            homeButton.setOnAction(e -> handleHome());
        }
        if (writingBlogButton != null) {
            writingBlogButton.setOnAction(e -> handleWritingBlog());
        }
        if (askQuestionButton != null) {
            askQuestionButton.setOnAction(e -> handleAskQuestion());
        }
        if (questionsButton != null) {
            questionsButton.setOnAction(e -> handleAllQuestions());
        }
        if (bookmarksButton != null) {
            bookmarksButton.setOnAction(e -> handleBookmarks());
        }

        Integer uid = sessionService.getSavedUserId();
        if (uid != null) {
            try {
                User u = userDao.findById(uid);
                if (u != null) {
                    setUser(u);
                }
            } catch (Exception ignored) {}
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            loadProfileImage(user.getProfileImage());
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

    private void openImagePicker() {
        if (currentUser == null) {
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select profile image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = chooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                profileImageView.setImage(image);
                byte[] bytes = imageToBytes(image);
                authService.updateProfileImage(currentUser.getId(), bytes);
                // refresh persisted user so other screens see new image
                currentUser = userDao.findById(currentUser.getId());
                setUser(currentUser);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private byte[] imageToBytes(Image image) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", os);
        return os.toByteArray();
    }

    private void handleLogout() {
        sessionService.clear();
        try {
            var stage = (javafx.stage.Stage) uploadButton.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/welcome.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleHome() {
        try {
            Stage stage = (Stage) homeButton.getScene().getWindow();
            windowService.switchScene(stage, "/fxml/Home.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleWritingBlog() {
        try {
            Stage stage = (Stage) writingBlogButton.getScene().getWindow();
            writingBlog_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/writingBlog.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleAskQuestion() {
        try {
            Stage stage = (Stage) askQuestionButton.getScene().getWindow();
            askQuestion_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/askQuestion.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Removed dialog popup per request
        }
    }

    private void handleAllQuestions() {
        try {
            Stage stage = (Stage) questionsButton.getScene().getWindow();
            allQuestions_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/allQuestions.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleBookmarks() {
        try {
            Stage stage = (Stage) bookmarksButton.getScene().getWindow();
            bookmarks_Controller controller = windowService.switchSceneAndGetController(stage, "/fxml/bookmarks.fxml");
            if (controller != null) {
                controller.setUser(currentUser);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

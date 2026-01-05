package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.AuthService;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    private User currentUser;
    private final AuthService authService = new AuthService();
    private final SessionService sessionService = new SessionService();

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
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/welcome.fxml"));
            javafx.scene.Parent root = loader.load();
            var stage = (javafx.stage.Stage) uploadButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleWritingBlog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/writingBlog.fxml"));
            Parent root = loader.load();
            writingBlog_Controller controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) writingBlogButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleAskQuestion() {
        try {
            var fxmlUrl = getClass().getResource("/fxml/askQuestion.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("askQuestion.fxml not found on classpath");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            askQuestion_Controller controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) askQuestionButton.getScene().getWindow();
            if (stage == null) {
                throw new IllegalStateException("Stage not available for askQuestionButton");
            }
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to open Ask Question screen: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void handleAllQuestions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allQuestions.fxml"));
            Parent root = loader.load();
            allQuestions_Controller controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) questionsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

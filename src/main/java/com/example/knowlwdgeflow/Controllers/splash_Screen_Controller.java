package com.example.knowlwdgeflow.Controllers;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class splash_Screen_Controller {
    @FXML
    private AnchorPane rootPane;

    private SessionService sessionService;
    private UserDao userDao;

    public void setSession(SessionService sessionService, UserDao userDao) {
        this.sessionService = sessionService;
        this.userDao = userDao;
    }

    @FXML
    public void initialize() {

        PauseTransition delay = new PauseTransition(Duration.seconds(2));

        delay.setOnFinished(event -> openNextScreen());

        delay.play();
    }

    private void openNextScreen() {
        try {
            Parent nextRoot;
            Integer savedId = sessionService != null ? sessionService.getSavedUserId() : null;
            if (savedId != null && userDao != null) {
                User user = userDao.findById(savedId);
                if (user != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainProfile.fxml"));
                    nextRoot = loader.load();
                    Object controller = loader.getController();
                    if (controller instanceof mainProfile_Controller mp) {
                        mp.setUser(user);
                    }
                } else {
                    sessionService.clear();
                    nextRoot = FXMLLoader.load(getClass().getResource("/fxml/welcome.fxml"));
                }
            } else {
                nextRoot = FXMLLoader.load(getClass().getResource("/fxml/welcome.fxml"));
            }

            Scene nextScene = new Scene(nextRoot);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(nextScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

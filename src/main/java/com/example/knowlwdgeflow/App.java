package com.example.knowlwdgeflow;

import com.example.knowlwdgeflow.dao.UserDao;
import com.example.knowlwdgeflow.model.User;
import com.example.knowlwdgeflow.service.SessionService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private final SessionService sessionService = new SessionService();
    private final UserDao userDao = new UserDao();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/splash_Screen.fxml"));
        Scene scene = new Scene(loader.load(), 800, 800);
        Object controller = loader.getController();
        if (controller instanceof com.example.knowlwdgeflow.Controllers.splash_Screen_Controller splash) {
            splash.setSession(sessionService, userDao);
        }
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.example.knowlwdgeflow.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Helper for switching scenes without losing window size or fullscreen/maximized state.
 */
public class WindowService {

    /**
     * Switch the given stage to a new scene defined by the provided FXML path.
     * Keeps size, position, maximized, and fullscreen state consistent.
     */
    public void switchScene(Stage stage, String fxmlPath) throws Exception {
        if (stage == null) return;
        boolean wasMaximized = stage.isMaximized();
        boolean wasFullScreen = stage.isFullScreen();
        double prevWidth = stage.getWidth();
        double prevHeight = stage.getHeight();
        double prevX = stage.getX();
        double prevY = stage.getY();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = stage.getScene();
        if (scene != null) {
            scene.setRoot(root); // reuse scene to avoid fullscreen flicker
        } else {
            scene = (prevWidth > 0 && prevHeight > 0)
                    ? new Scene(root, prevWidth, prevHeight)
                    : new Scene(root);
            stage.setScene(scene);
        }

        if (!Double.isNaN(prevX) && !Double.isNaN(prevY)) {
            stage.setX(prevX);
            stage.setY(prevY);
        }

        stage.setFullScreenExitHint("");
        if (wasFullScreen) {
            stage.setFullScreen(true);
        } else {
            stage.setMaximized(wasMaximized);
        }

        stage.show();
    }

    /** Load and return a controller after creating the scene, keeping window state. */
    public <T> T switchSceneAndGetController(Stage stage, String fxmlPath) throws Exception {
        if (stage == null) return null;
        boolean wasMaximized = stage.isMaximized();
        boolean wasFullScreen = stage.isFullScreen();
        double prevWidth = stage.getWidth();
        double prevHeight = stage.getHeight();
        double prevX = stage.getX();
        double prevY = stage.getY();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = stage.getScene();
        if (scene != null) {
            scene.setRoot(root);
        } else {
            scene = (prevWidth > 0 && prevHeight > 0)
                    ? new Scene(root, prevWidth, prevHeight)
                    : new Scene(root);
            stage.setScene(scene);
        }

        if (!Double.isNaN(prevX) && !Double.isNaN(prevY)) {
            stage.setX(prevX);
            stage.setY(prevY);
        }

        stage.setFullScreenExitHint("");
        if (wasFullScreen) {
            stage.setFullScreen(true);
        } else {
            stage.setMaximized(wasMaximized);
        }

        stage.show();
        return loader.getController();
    }
}

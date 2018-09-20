package net.zelinf.demos.opencv_demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class App extends Application {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("OpenCV Demo");
        primaryStage.setScene(new Scene(new MainWindow()));

        primaryStage.show();
    }
}

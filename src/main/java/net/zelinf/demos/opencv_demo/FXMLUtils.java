package net.zelinf.demos.opencv_demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public final class FXMLUtils {

    public static <T extends Parent> void loadFXML(T component) {
        FXMLLoader loader = new FXMLLoader();
        loader.setRoot(component);
        loader.setControllerFactory(theClass -> component);

        String fileName = component.getClass().getSimpleName() + ".fxml";
        try {
            loader.load(component.getClass().getResourceAsStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

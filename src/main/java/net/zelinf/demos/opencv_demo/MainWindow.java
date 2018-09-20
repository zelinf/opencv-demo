package net.zelinf.demos.opencv_demo;

import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWindow extends BorderPane {

    public MainWindow() {
        FXMLUtils.loadFXML(this);
    }

    @FXML
    private ImageView currentFrame;

    @FXML
    private Button startCamera;

    private VideoCapture capture = new VideoCapture();

    private ScheduledExecutorService timer;

    private BooleanProperty isCameraActive = new SimpleBooleanProperty(false);

    @FXML
    private void initialize() {
        startCamera.textProperty().bind(new ObjectBinding<String>() {
            {
                super.bind(isCameraActive);
            }

            @Override
            protected String computeValue() {
                return isCameraActive.get() ?
                        "Stop Camera" : "Start Camera";
            }
        });
    }

    @FXML
    private void onStartCamera(ActionEvent event) {
        if (!isCameraActive.get()) {
            capture.open(0);
            if (capture.isOpened()) {
                isCameraActive.set(true);

                Runnable frameGrabber = () -> {
                    Mat frame = grabFrame();
                    Image imageToShow = mat2Image(frame);
                    updateImageView(currentFrame, imageToShow);
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            }
        } else {
            isCameraActive.set(false);
            stopAcquisition();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            try {
                capture.read(frame);
                if (!frame.empty()) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return frame;
    }

    private void stopAcquisition() {
        if (timer != null && !timer.isShutdown()) {
            try {
                timer.shutdown();
                timer.awaitTermination(66, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (capture.isOpened()) {
            capture.release();
        }
    }

    private void updateImageView(ImageView view, Image image) {
        Platform.runLater(() -> view.setImage(image));
    }

    public static Image mat2Image(Mat frame) {
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    private static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }
}

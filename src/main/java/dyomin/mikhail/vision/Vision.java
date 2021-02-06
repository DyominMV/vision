package dyomin.mikhail.vision;

import com.github.sarxos.webcam.Webcam;
import dyomin.mikhail.vision.filters.gauss.PseudoGaussianBlur;
import dyomin.mikhail.vision.filters.gauss.SystemOrder;
import dyomin.mikhail.vision.filters.simple.Amplifier;
import dyomin.mikhail.vision.filters.simple.RadialDistortion;
import dyomin.mikhail.vision.filters.simple.TangentialDistortion;
import dyomin.mikhail.vision.vectors.RGB;
import dyomin.mikhail.vision.images.RgbImage;
import dyomin.mikhail.vision.images.EditableImage;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;

public class Vision extends Application {

    private static final Webcam webcam = Webcam.getWebcams().get(0);

    static {
        System.out.println(Webcam.getWebcams());
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();
    }

    public ImageView standardView;
    public ImageView modifiedView;

    private Image fromBufferedImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, new WritableImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight()
        ));
    }

    private final PseudoGaussianBlur<RGB> pgb =
            new PseudoGaussianBlur<>(10, SystemOrder.THREE);

    private final RadialDistortion<RGB> radialDistortion =
            new RadialDistortion<>(320,240,
                    1.0/10000,
                    1.0/100000
            );

    private final TangentialDistortion<RGB> tangentialDistortion =
            new TangentialDistortion<>(320,240,
                    0,
                    -1.0/10000,
                    1e-11, 1e-11
            );


    private EditableImage<RGB> handleImage(RgbImage rawImage) {
        EditableImage<RGB> result = rawImage.toMatrixImage()
                .applyFilters(Arrays.asList(
                        tangentialDistortion
                ));
        return result;
    }

    public void grabAndHandleImage() {
        BufferedImage rawImage = webcam.getImage();
        standardView.setImage(fromBufferedImage(rawImage));
        modifiedView.setImage(fromBufferedImage(handleImage(new RgbImage(rawImage)).visualize()));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello world Application");
        primaryStage.setWidth(1300);
        primaryStage.setHeight(520);
        primaryStage.setResizable(false);

        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/vision.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

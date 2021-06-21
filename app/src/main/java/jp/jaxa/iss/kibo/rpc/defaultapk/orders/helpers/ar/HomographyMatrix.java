package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import org.opencv.core.Mat;
import org.opencv.core.Size;

public class HomographyMatrix {

    // Change these out of mat form?
    private final Mat rotationVector;
    private final Mat translationVector;
    private final ARTagCollection arTagCollection;
    private final Size imageSize;


    HomographyMatrix(Mat rotationVector, Mat translationVector, ARTagCollection arTagCollection, Size imageSize) {
        this.rotationVector = rotationVector;
        this.translationVector = translationVector;
        this.arTagCollection = arTagCollection;
        this.imageSize = imageSize;
    }

    public Mat getRotationVector() {
        return this.rotationVector;
    }

    public Mat getTranslationVector() {
        return this.translationVector;
    }

    public ARTagCollection getArTagCollection() {
        return this.arTagCollection;
    }

    public double getImageWidth() {
        return this.imageSize.width;
    }

    public double getImageHeight() {
        return this.imageSize.height;
    }

    public Size getImageSize() {
        return this.imageSize;
    }
}

package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import org.opencv.core.Mat;

public class HomographyMatrix {

    // Change these out of mat form?
    private final Mat rotationVector;
    private final Mat translationVector;
    private final ARTagCollection arTagCollection;


    HomographyMatrix(Mat rotationVector, Mat translationVector, ARTagCollection arTagCollection) {
        this.rotationVector = rotationVector;
        this.translationVector = translationVector;
        this.arTagCollection = arTagCollection;
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
}

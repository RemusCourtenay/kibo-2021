package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

class RobotFireLaserOrder extends RobotOrder {


    private static final int markerDictionaryID = Aruco.DICT_5X5_250;

    private final ImageHelper imageHelper;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper) {
        super(api);
        this.imageHelper = imageHelper;
    }

    private void attemptToAlignLaser(double poseAngle, double distance) {

    }

    @Override
    protected Result attemptOrderImplementation() {
        Mat image =  api.getMatNavCam();
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat(4, 1, 0);
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);
        return null; // TODO...
    }

    @Override
    public String printOrderInfo() {
        return null;
    }

    private void takeTenSnapShots() {
        for (int i = 0; i< 10; i++) {
            api.takeSnapshot();
            try {
                wait(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

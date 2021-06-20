package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;

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
        //Aruco.detectMarkers();
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

package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Board;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class LaserGunner {

    private final KiboRpcApi api;

    public LaserGunner(KiboRpcApi api, Context context) {
        this.api = api;
    }

    /**
     * Uses the Homography function:
     * [x1,y2,w2] = [Intrinsics][Homography Matrix][x2,y2,z2,w2]
     * to find the difference in the value of the target point and the laser firing point with
     * regards to the robot's coordinate system.
     * @param homographyMatrix : The homography matrix that describes the rotation and translation
     *                         from the board's coordinate system to the cameras coordinate system
     * @param arTagCollection : Collection of AR tags from the board for use in finding the location
     *                        of the target
     */
    public RobotOrderResult attemptAquireTargetLock(HomographyMatrix homographyMatrix, ARTagCollection arTagCollection) {
        return null;
    }

    public RobotOrderResult fireLaser() {

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

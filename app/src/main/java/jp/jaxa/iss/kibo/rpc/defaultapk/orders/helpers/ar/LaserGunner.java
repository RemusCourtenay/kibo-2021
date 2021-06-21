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
     *                         from the board's coordinate system to the cameras coordinate system,
     *                         and the collection of tags from the board
     */
    public RobotOrderResult attemptAcquireTargetLock(HomographyMatrix homographyMatrix) {
        RobotOrderResult result;

        // The calculate distance function decided that we either weren't close enough or some other problem occurred
        if (!(result = calculateDistanceFromTarget(homographyMatrix)).hasSucceeded()) {
            return result;

        // We were on target but something has failed while firing the laser
        } else if (!(result = fireLaser()).hasSucceeded()) {
            return result;

        // Everything worked out
        } else {
            return null; // TODO... Replace with good result
        }
    }

    private RobotOrderResult calculateDistanceFromTarget(HomographyMatrix homographyMatrix) {

        return null;
    }

    private RobotOrderResult fireLaser() {

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

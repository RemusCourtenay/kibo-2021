package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Board;
import org.opencv.core.Point;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class LaserGunner {

    private final KiboRpcApi api;

    public LaserGunner(KiboRpcApi api, Context context) {
        this.api = api;
    }

    /**
     * Uses the Homography function:
     * [x1,y1,w1] = [Intrinsics][Homography Matrix][x2,y2,z2,w2]
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

    /**
     * Attempts to calculate the distance between the spot where the laser will hit the board and
     * the target itself on the board. This is calculated by using the homography function detailed
     * above to translate the coordinates of the target on the board to it's location within the
     * image (i.e. the x,y coordinates of it's pixels in the image).
     * If the two points are within a certain distance of each other we will return a positive
     * result, prompting the firing of the laser to start.
     * If they are not close enough then we return a negative result prompting the program to try
     * rotate the robot to a more accurate angle.
     *
     * @param homographyMatrix : The homography matrix detailing the transformation between the two
     *                         coordinate systems. AR tags are also contained in here for convenience.
     * @return : A result detailing how successful the action was.
     */
    private RobotOrderResult calculateDistanceFromTarget(HomographyMatrix homographyMatrix, int imageWidth, int imageHeight) {

        Point laserPoint = new Point(imageWidth/2.0, imageHeight/2.0); // Laser fires at the center of the camera (I assume)

        Point targetPoint = getTargetPointInBoardCoordSpace(homographyMatrix.getArTagCollection());

        // Somehow transform target point into picture coords using Homography matrix

        // return difference in location

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

    private Point getTargetPointInBoardCoordSpace(ARTagCollection arTagCollection) {
        return null;
    }

}

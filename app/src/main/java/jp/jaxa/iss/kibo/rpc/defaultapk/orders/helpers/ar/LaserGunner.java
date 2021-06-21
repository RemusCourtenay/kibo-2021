package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Board;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
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
    public RobotOrderResult attemptAcquireTargetLock(HomographyMatrix homographyMatrix) { // Not using the standard 1280x960 because the image has probably been cropped
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
    private RobotOrderResult calculateDistanceFromTarget(HomographyMatrix homographyMatrix) { // TODO...

        Point laserPoint = new Point(homographyMatrix.getImageWidth()/2.0, homographyMatrix.getImageHeight()/2.0); // Laser fires at the center of the camera (I assume)

        Mat targetPointMat = getTargetPointInBoardCoordSpaceAsMat(homographyMatrix.getArTagCollection());

        Mat instrinsicsMat = new MatOfDouble(api.getNavCamIntrinsics()[0]); // TODO... FIX THIS

        Mat rotationMatrix = homographyMatrix.getRotationVector();
        Mat translationMatrix = homographyMatrix.getTranslationVector();

        for (int i = 0; i < 4; i++) { // Assuming that translation matrix is horizontal
            rotationMatrix.put(3, i, translationMatrix.get(0,i));
        }

        rotationMatrix.dot(targetPointMat); // TODO... Find out if this works or if we have to do it row by row
        rotationMatrix.dot(instrinsicsMat);

        // Rotation matrix is now equal to target point in other co-ord system

        Point targetPoint = new Point((int)rotationMatrix.get(0,0)[0],(int)rotationMatrix.get(1, 0)[0]);

        double xDiff = targetPoint.x - laserPoint.x;
        double yDiff = targetPoint.y - laserPoint.y;

        if (xDiff < DISTANCE_AMOUNT)

        // Straight up vector is [x=0,y=1,z=0]
        // i.e. 0 + 1i
        // angle as complex number is xDiff + yDiff(i)
        // so we multiply them together multiply by i gives xDiff(i) - yDiff
        // so rotation vector is now [x=-yDiff, y=xDiff, z=0]
        // quaternion equation is [w=angle, x=xsin(angle/2), y=ysin(angle/2), z=zsin(angle/2)

        double amountToRotate = 10;

        Quaternion rotatedQuaternion = new Quaternion(
                (float)amountToRotate,
                (float)(-yDiff*Math.sin(amountToRotate/2)),
                (float)(xDiff*Math.sin(amountToRotate/2)),
                (float)0);

        // get current Quaternion
        Kinematics currentKinematics = api.getRobotKinematics();
        Quaternion currentQuaternion = currentKinematics.getOrientation();

        // get translated Quaternion

        Quaternion translatedQuaternion;


        /**
         *
         *                      y
         *                    / |
         *                  /   |
         *                /     |
         *              x -------
         *                 ^
         *                 |
         *                Angle
         *
         *
         */


        // return difference in location

        return new RobotLaserDistanceResult(false, 1, "Not close enough", translatedQuaternion);
    }

    /**
     * Attempts to use the API methods to fire the laser and take the snapshots required for the
     * expedition to succeed.
     * @return : A result detailing whether or not the use of the API methods worked
     */
    private RobotOrderResult fireLaser() { // TODO...

        Result result;
        RobotOrderResult orderResult;

        // If fail to turn on laser
        if (!(result = api.laserControl(true)).hasSucceeded()) {
            return new GenericRobotOrderResult(result);

        // If fail to take 10 snapshots
        } else if (!(orderResult = takeTenSnapShots()).hasSucceeded()) {
            return orderResult;

        // If fail to turn off laser
        } else if (!(result = api.laserControl(false)).hasSucceeded()) {
            return new GenericRobotOrderResult(result);

        // If everything works
        } else {
            return new GenericRobotOrderResult(true, 0, "");
        }
    }

    private RobotOrderResult takeTenSnapShots() {
        for (int i = 0; i< 10; i++) {
            api.takeSnapshot();
            try {
                wait(1000);
            } catch(InterruptedException e) {
                return new GenericRobotOrderResult(false, 0, e.getMessage()); // Maybe just not worry about this one?
            }
        }
        return new GenericRobotOrderResult(true, 0, "");
    }

    /**
     * Uses the corner location values of each mat and the defined knowledge about the board shape
     * and size to locate the x,y coordinates of the center of the target with regards to the board.
     * The resulting vertical vector should also contain a w value representing scale which should
     * always be set to 1
     *
     * Resulting vector:
     *      [x]     [x]
     *      [y]  =  [y]
     *      [w]     [1]
     *
     * @param arTagCollection : The ARTag objects representing the four AR tags situated on the board
     * @return : A vertical 1x3 vector representing the x,y,w values for the target point with
     *           respect to the board. This should be stored as a Mat object even though it's a pain
     */
    private Mat getTargetPointInBoardCoordSpaceAsMat(ARTagCollection arTagCollection) { // TODO...


        int[] coords = new int[]{0,0};
        double[] tagCoords;

        for (ARTag tag: arTagCollection.getARTags()) {
            tagCoords = tag.getCoordinates();
            coords[0] = (int)tagCoords[0];
            coords[1] = (int)tagCoords[1];
        }

        coords[0] = coords[0]/4; // int division but w/e
        coords[1] = coords[1]/4;

        return new MatOfInt(coords); // Not sure if this is vertical or not

    }

}

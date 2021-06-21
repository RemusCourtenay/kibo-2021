package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import org.opencv.aruco.Aruco;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderException;

public class ARTag {

    private static final int MOVE_AMOUNT = 10; // Chosen randomly
    private static final int TAG_SIDE_LENGTH = 5; // in cm

    private final int id;
    private final Mat corners;
    // private final double[][] camIntrinsics;

    ARTag(int id, Mat corners) {
        this.id = id;
        this.corners = corners;
        //this.camIntrinsics = camIntrinsics;
    }

    /**
     * Returns the orientation of the tag which is calculated by using the
     * estimatePoseSingleMarker() function and translating the resulting rotation matrix into a
     * rotation vector with the Rodrigues function.
     * See the Pose Estimation section here:
     * https://docs.opencv.org/4.5.2/d5/dae/tutorial_aruco_detection.html
     *
     */
    public void getOrientation() {  // PROBABLY NOT WORTH EFFORT - CAN JUST ASSUME ALWAYS UPRIGHT

//        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1); // Code copied from imageHelper
//        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);
//
//        cameraMatrix.put(0, 0, camIntrinsics[0]);
//        distCoeffs.put(0, 1, camIntrinsics[1]);
//
//        // Setting up variables for the function to fill
//        List<Mat> cornersList = new ArrayList<>();
//        Converters.Mat_to_vector_Mat(corners, cornersList);
//        Mat rotationMatrix = new Mat();
//        Mat translationVector = new Mat();
//        Aruco.estimatePoseSingleMarkers(cornersList, TAG_SIDE_LENGTH, cameraMatrix, distCoeffs, rotationMatrix, translationVector);
//
//        Mat rotationDiagonal = rotationMatrix.diag(0);

    } // TODO...


    /**
     * Utilises the id and orientation values of the tag to calculate what two dimensional rotation
     * the robot needs to do to in order to catch all four tags in the camera image.
     * This is necessary due to this statement from the rulebook:
     * "At least one AR tag is in the NavCam FOV from Point-A’."
     *
     * The standard layout of the tags is:
     *
     *      4 -------- 1
     *      |          |
     *      |          |
     *      3 -------- 2
     *
     * The coordinate system places 0,0 in the top left.
     * Note that the direction the camera needs to rotate is the opposite of the placement of the
     * tag as in this scenario the referenced tag is already in the image and we are attempting to
     * locate the others.
     *
     * @return : an int array [x,y] detailing the relative rotational movement required to find the
     * other tags.
     */
    public int[] getRelativeMovement() {
        int[] move;

        switch (this.id) {
            case 1: move = new int[]{-10, 10}; break;
            case 2: move = new int[]{-10, -10}; break;
            case 3: move = new int[]{10, -10}; break;
            case 4: move = new int[]{10, 10}; break;
            default: throw new RobotOrderException("");
        }

        return move;

    } // TODO...

    /**
     * Calculating the x,y coordinates of the tag with respect to the board's coordinate system.
     * This is done by averaging the x,y coordinates of each corner of the tag.
     *
     * @return : A two element double array representing the tag's x,y coordinates.
     */
    public double[] getCoordinates() {

        double[] coords = new double[]{0,0};
        double[] cornerCoords;

        for (int i = 0; i < 4; i++) { // 4 for four corners of square tag
            cornerCoords = this.corners.get(0, i);
            coords[0] = coords[0] + cornerCoords[0];
            coords[1] = coords[1] + cornerCoords[1];
        }

        // Getting average value
        coords[0] = coords[0]/4.0;
        coords[1] = coords[1]/4.0;

        return coords;
    }

    /* ------------ Standard Getter Methods ------------- */

    public int getId() {
        return this.id;
    }

    public Mat getCorners() {
        return this.corners;
    }


}
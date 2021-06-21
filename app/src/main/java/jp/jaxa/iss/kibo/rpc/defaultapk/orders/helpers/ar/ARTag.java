package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import org.opencv.core.Mat;

public class ARTag {

    private final int id;
    private final Mat corners;

    ARTag(int id, Mat corners) {
        this.id = id;
        this.corners = corners;
    }

    /**
     * Returns the orientation of the tag which is calculated by using the
     * estimatePoseSingleMarker() function and translating the resulting rotation matrix into a
     * rotation vector with the Rodrigues function.
     * See the Pose Estimation section here:
     * https://docs.opencv.org/4.5.2/d5/dae/tutorial_aruco_detection.html
     *
     */
    public void getOrientation() {} // TODO...


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
    public int[] getRelativeMovement() { // TODO...
        return null;
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
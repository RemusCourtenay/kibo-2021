package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

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
        Mat image =  imageHelper.undistort(api.getMatNavCam(),api.getNavCamIntrinsics());
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat();//(4, 1, 0);
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);
        Board board = Board.create(corners, Aruco.getPredefinedDictionary(markerDictionaryID), ids);
        /**
         * Attempt with estimate pose board
         */
        // Mat counter = new Mat(4, 1, 0);
        // Size imageSize = new Size();
        Mat distCoeffs = new Mat();//4, 1, 0);
        Mat rvecs = new Mat();
        Mat tvecs = new Mat();
        // double vecs = Aruco.calibrateCameraAruco(corners, ids, counter, board, imageSize, image, distCoeffs);
        int poses = Aruco.estimatePoseBoard(corners, ids, board, image, distCoeffs, rvecs, tvecs);
        //Mat camMat = cameraMatrix(fc, new Size(frameSize.width / 2, frameSize.height / 2));
        /**
         * Attempt with calib3d solvePnP
         */
        Mat camMat = image;
        MatOfDouble coeff = new MatOfDouble(); // dummy

        MatOfPoint2f centers = new MatOfPoint2f();
        //Size patternSize = new Size(4, 11);
        //MatOfPoint3f grid = asymmetricalCircleGrid(patternSize);
        MatOfPoint3f grid = new MatOfPoint3f(image);
        Mat rvec = new MatOfFloat();
        Mat tvec = new MatOfFloat();

        MatOfPoint2f reprojCenters = new MatOfPoint2f();
        Calib3d.solvePnP(grid, centers, camMat, coeff, rvec, tvec, false);
        //Calib3d.solvePnP();
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

    /**
     * getBoard makes and returns a board.
     * @return board
     */
    private Board getBoard() {
        Mat image =  imageHelper.undistort(api.getMatNavCam(),api.getNavCamIntrinsics());
        List<Mat> corners = new ArrayList<Mat>();
        Mat ids = new Mat();//(4, 1, 0);
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), corners, ids);
        Board board = Board.create(corners, Aruco.getPredefinedDictionary(markerDictionaryID), ids);
        return board;
    }


}

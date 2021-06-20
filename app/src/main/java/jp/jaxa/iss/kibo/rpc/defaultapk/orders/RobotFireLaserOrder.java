package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RobotFireLaserOrder extends RobotOrder {


    private static final int markerDictionaryID = Aruco.DICT_5X5_250;
    private static final int AMOUNT_TO_ADJUST = 10;
    private static final int MAX_BOARD_ATTEMPTS = 10;

    private final Map<Integer, int[]> idToDirectionMap;


    private final ImageHelper imageHelper;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper) {
        super(api);
        this.imageHelper = imageHelper;


        idToDirectionMap = new HashMap<>();
        // 0,0 is bottom left, 1,2,3,4 go clockwise from top right, array values are movements to get tags to enter frame assuming that camera is right way up
        idToDirectionMap.put(1, new int[]{-AMOUNT_TO_ADJUST,-AMOUNT_TO_ADJUST});
        idToDirectionMap.put(2, new int[]{-AMOUNT_TO_ADJUST,AMOUNT_TO_ADJUST});
        idToDirectionMap.put(3, new int[]{AMOUNT_TO_ADJUST,AMOUNT_TO_ADJUST});
        idToDirectionMap.put(4, new int[]{AMOUNT_TO_ADJUST,+AMOUNT_TO_ADJUST});

    }

    @Override
    protected Result attemptOrderImplementation() {
        List<Mat> cornersOfEachTag = new ArrayList<>(); // Supposedly I can pass these in and the functions will give them values
        Mat tagIdentifiers = new MatOfDouble();

        // Handles getting image onto 4 tags
        Mat image;
        Board airlockBoard;

        int numAttempts = 0;

        do { // Moved this out to here so I didn't have to pass image in
            if (numAttempts < MAX_BOARD_ATTEMPTS) {
                numAttempts++;
                image = imageHelper.undistort(api.getMatNavCam(), api.getNavCamIntrinsics());
            } else {
                throw new RobotOrderException("Failed to make board, unable to get all four tags?");
            }
        } while ((airlockBoard = getBoardData(image, cornersOfEachTag, tagIdentifiers)) != null);


        // Why we putting distortion in when we already undistort?? put in no distortion at all?
        Mat distortionCoefficients = new Mat(1, 5, CvType.CV_32FC1); // stolen from imageHelper, not my code, no idea what cv type is
        distortionCoefficients.put(0,1, api.getNavCamIntrinsics()[1]); // distortion coefficients are second in array

        Mat rotationVector = new Mat(); // Output vector corresponding to rotation of board
        Mat translationVector = new Mat(); // Output vector corresponding to translation of board

        if(Aruco.estimatePoseBoard(
                cornersOfEachTag,
                tagIdentifiers,
                airlockBoard,
                image,
                distortionCoefficients,
                rotationVector,
                translationVector
        ) == 0) {
            throw new RobotOrderException("Failed to estimate pose");
        }

        // Do something maths related with rotation and translation vector

        attemptToAlignLaser(rotationVector, translationVector);

        takeTenSnapShots();

        return null; // TODO...
    }

    @Override
    public String printOrderInfo() {
        return null;
    }

    private void attemptToAlignLaser(Mat rotationVector, Mat translationVector) {

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


    private Board getBoardData(Mat image, List<Mat> cornersOfEachTag, Mat tagIdentifiersVector) { // TODO... Comments
        cornersOfEachTag.clear();
        // not clearing tagIdentifiersVector, hopefully ok
        Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(markerDictionaryID), cornersOfEachTag, tagIdentifiersVector);
        if (tagIdentifiersVector.size().width < 4) {
            adjustImage(tagIdentifiersVector); // try move to get more tags into image
            return null; // signal that we need to run again
        } else {
            return Board.create(cornersOfEachTag, Aruco.getPredefinedDictionary(markerDictionaryID), tagIdentifiersVector);
        }
    }


    private void adjustImage(Mat idsVector) { // TODO... be smart and get rotation from order of tags

        // ordered x,y
        int[] adjustmentAmounts = new int[]{0,0};
        int[] specificAdjustment;
        double[] temp = new double[(int) idsVector.total() * idsVector.channels()];

        for (int i = 0; i < idsVector.size().width; i++) {
            idsVector.get(0, i, temp); // Assuming this is the right way around
            specificAdjustment = this.idToDirectionMap.get((int) temp[0]);
            adjustmentAmounts[0] = adjustmentAmounts[0] + specificAdjustment[0];
            adjustmentAmounts[1] = adjustmentAmounts[1] + specificAdjustment[1];
        }

        rotateRobot(adjustmentAmounts);
    }

    private void rotateRobot(int[] adjustmentAmounts) {
        // TODO... do maths
    }
}

package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ARTag;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ARTagReaderWrapper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ImageHelper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotARTagReadOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.List;

class RobotFireLaserOrder extends RobotOrder { // TODO... Javadoc comment


    private static final int markerDictionaryID = Aruco.DICT_5X5_250; // TODO... Move to xml file and get via context
    private static final int AMOUNT_TO_ADJUST = 10;
    private static final int MAX_BOARD_ATTEMPTS = 5;

    private final ImageHelper imageHelper;
    private final ARTagReaderWrapper arTagReaderWrapper;



    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper, ARTagReaderWrapper arTagReaderWrapper) {
        super(api);
        this.imageHelper = imageHelper;
        this.arTagReaderWrapper = arTagReaderWrapper;

    }

    @Override
    public RobotOrderResult attemptOrder() {

        // Attempting to get the tags and board
        RobotOrderResult result;
        Mat navCamMatImage;
        Mat cleanedMatImage;
        List<ARTag> arTags;
        Board board;

        // Not using infinite loop
        for(int i = 0; i < MAX_BOARD_ATTEMPTS; i++) {
            // Get image from camera
            navCamMatImage = api.getMatNavCam();

            // Clean image to make scanning faster
            cleanedMatImage = cleanupImage(navCamMatImage);

            // Attempt to get the tags / board
            result = this.arTagReaderWrapper.attemptReadImageForARTags(cleanedMatImage);

            // Failed completely, currently just passing result up but could try fix if have time
            if (!result.hasSucceeded()) {
                return result;

            // Didn't completely fail
            } else { // TODO... check that casting is safe here by using result.getType()

                // Getting bundled data out of result
                arTags = ((RobotARTagReadOrderResult) result).getARTags();
                board = ((RobotARTagReadOrderResult) result).getBoard();

                // Complete success, both board and tags were returned
                if (result.getReturnValue() == 0) {
                    break; // No need to keep trying

                    // Got tags but there weren't enough to build board (<4)
                } else if (result.getReturnValue() == 1) {
                    // Move robot and then we'll try again
                    adjustRobot(arTags);

                // Returned a value that hasn't been implemented yet...
                } else {
                    throw new RobotOrderException("Return value for AR Tag reading: " + result.getReturnValue() + " has not been implemented in RobotFireLaserOrder.attemptOrder()");
                }
            }
        }




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
        return "Fire laser order:";
    }


    /**
     * Uses the Homograph function:
     * [x1,y2,w2] = [Intrinsics][Rotation Vector][Translation Vector][x2,y2,z2,w2]
     * to find the difference in the value of the target point and the laser firing point with
     * regards to the robot's coordinate system.
     * @param rotationVector
     * @param translationVector
     */
    private void attemptToAlignLaser(Mat rotationVector, Mat translationVector) {

    }




    private void adjustRobot(List<ARTag> foundTags) {

        // ordered x,y
        int[] adjustmentAmounts = new int[]{0,0};
        int[] specificAdjustment;

        for (ARTag tag: foundTags) { // Possibly a cleaner way of doing this but w/e
            specificAdjustment = tag.getRelativeMovement();
            adjustmentAmounts[0] = adjustmentAmounts[0] + specificAdjustment[0];
            adjustmentAmounts[1] = adjustmentAmounts[1] + specificAdjustment[1];
        }

        rotateRobot(adjustmentAmounts);
    }

    private void rotateRobot(int[] adjustmentAmounts) {
        // TODO... do maths
    }


    /**
     * Handles the cropping and possible scaling of the image by using the methods
     * from imageHelper. Returns an image with high chance of AR tags being readable.
     * Does not remove distortion as this is needed to calculate the relative position of the camera
     * to the tags.
     *
     * @param matImage : initial image returned by Kibo robot camera
     * @return : image that is easy to read AR tags off of
     */
    private Mat cleanupImage(Mat matImage) {
        return null; // TODO...
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

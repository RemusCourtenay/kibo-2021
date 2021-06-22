package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.ARTag;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.ARTagCollection;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.ARTagReaderWrapper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ImageHelper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.HomographyMatrix;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.LaserGunner;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar.QuaternionHelper;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotARTagReadOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotLaserOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.core.Mat;

class RobotFireLaserOrder extends RobotOrder { // TODO... Javadoc comment


    private static final int markerDictionaryID = Aruco.DICT_5X5_250; // TODO... Move to xml file and get via context
    private static final int MAX_BOARD_ATTEMPTS = 5;
    private static final int MAX_FIRE_ATTEMPTS = 5;

    private final ARTagReaderWrapper arTagReaderWrapper;
    private final LaserGunner laserGunner;
    private final ImageHelper imageHelper;

    private HomographyMatrix homographyMatrix;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper, ARTagReaderWrapper arTagReaderWrapper, LaserGunner laserGunner) {
        super(api);
        this.arTagReaderWrapper = arTagReaderWrapper;
        this.laserGunner = laserGunner;
        this.imageHelper = imageHelper;
    }

    @Override
    public RobotOrderResult attemptOrder() {

        RobotOrderResult result;

        for (int i = 0; i < MAX_FIRE_ATTEMPTS; i++) {
            if (!(result = attemptGetBoardPose()).hasSucceeded()) {
                return result;
            } else if (!(result = laserGunner.attemptAcquireTargetLock(homographyMatrix)).hasSucceeded()) {
                rotateRobot(((RobotLaserOrderResult)result).getTranslatedQuaternion());
            } else {
                return new GenericRobotOrderResult(true, 0, "Fire Laser Order Succeeded");
            }

        }

        return new GenericRobotOrderResult(false, 1, "Fire Laser Order Failed");
    }

    @Override
    public String printOrderInfo() {
        return "Fire laser order:";
    }


    private RobotOrderResult attemptGetBoardPose() {
        // Attempting to get the tags and board
        RobotOrderResult result;
        Mat navCamMatImage;
        Mat cleanedMatImage;
        ARTagCollection arTags;
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
            } else {
                if (!(result.getType() == RobotARTagReadOrderResult.class)) {
                    throw new RobotOrderException("Incorrect type");
                }
                // Getting bundled data out of result
                arTags = ((RobotARTagReadOrderResult) result).getARTagCollection();
                board = ((RobotARTagReadOrderResult) result).getBoard();

                // Complete success, both board and tags were returned
                if (result.getReturnValue() == 0) {
                    this.homographyMatrix = calculateBoardPose(board, arTags, cleanedMatImage); // TODO... pass this through Result instead
                    return new GenericRobotOrderResult(true, 0, "Successfully attempted to get board pose");
                    // Got tags but there weren't enough to build board (<4) TODO... this isn't actually correct
                } else if (result.getReturnValue() == 1) {
                    // Move robot and then we'll try again
                    Quaternion adjustment = getAdjustmentNeededToFindTags(arTags);
                    rotateRobot(adjustment);
                } else {
                    throw new RobotOrderException("Return value for AR Tag reading: " + result.getReturnValue() + " has not been implemented in RobotFireLaserOrder.attemptOrder()");
                }
            }
        }
        return new GenericRobotOrderResult(false, 1, "Failed to get board pose");
    }

    /**
     * Uses the Aruco.estimatePoseBoard() function to find the rotation and translation vectors for
     * the given board. Wraps them in a Homography matrix to return.
     *
     * @param board : Board that exists on some separate coordinate system to the image
     * @param arTagCollection : AR tags found on the board
     * @return : The homography matrix that describes the translation from one coordinate system to
     *           the other        for (int i = 0; i < 4; i++) { // Assuming that translation matrix is horizontal
     *             rotationMatrix.put(3, i, translationMatrix.get(0,i));
     *         }
     */
    private HomographyMatrix calculateBoardPose(Board board, ARTagCollection arTagCollection, Mat cleanedMatImage) { // TODO...

        Mat[] cameraIntrinsics = this.imageHelper.getCameraIntrinsicsAsMats(api.getNavCamIntrinsics());

        Mat rvec = new Mat();
        Mat tvec = new Mat();

        Aruco.estimatePoseBoard(arTagCollection.getTagCornersMat(), arTagCollection.getTagIDsMat(), board, cameraIntrinsics[0], cameraIntrinsics[1], rvec, tvec);
        return new HomographyMatrix(rvec, tvec, arTagCollection, cleanedMatImage.size());
    }

    private Quaternion getAdjustmentNeededToFindTags(ARTagCollection foundTags) {

        // ordered x,y
        double[] adjustmentAmounts = new double[]{0,0};
        int[] specificAdjustment;

        for (ARTag tag: foundTags.getARTags()) { // Possibly a cleaner way of doing this but w/e
            specificAdjustment = tag.getRelativeMovement();
            adjustmentAmounts[0] = adjustmentAmounts[0] + specificAdjustment[0];
            adjustmentAmounts[1] = adjustmentAmounts[1] + specificAdjustment[1];
        }


        return QuaternionHelper.translateAdjustment(api, adjustmentAmounts);
    }

    private void rotateRobot(Quaternion quaternion) {
        api.relativeMoveTo(new Point(0.0,0.0, 0.0), quaternion, true);
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
        Mat croppedMat = new Mat(matImage, this.imageHelper.getCroppedImageRectangleArea(matImage.size()));
        Mat scaledMat = this.imageHelper.scaleMatDown(croppedMat);
        return scaledMat;
    }


}

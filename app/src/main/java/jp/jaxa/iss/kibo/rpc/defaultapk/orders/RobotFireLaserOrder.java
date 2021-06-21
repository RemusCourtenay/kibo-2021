package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.R;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

class RobotFireLaserOrder extends RobotOrder { // TODO... Javadoc comment


    private static final int markerDictionaryID = Aruco.DICT_5X5_250; // TODO... Move to xml file and get via context
    private static final int AMOUNT_TO_ADJUST = 10;
    private static final int MAX_BOARD_ATTEMPTS = 5;
    private static final int MAX_FIRE_ATTEMPTS = 5;

    private final ARTagReaderWrapper arTagReaderWrapper;
    private final LaserGunner laserGunner;
    private final Context context;
    private final int kiboCamImageHeight; // True for both nav and dock cam
    private final int kiboCamImageWidth;
    private final int percentThatCropRemoves;

    private HomographyMatrix homographyMatrix;
    private ARTagCollection arTagCollection;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ARTagReaderWrapper arTagReaderWrapper, LaserGunner laserGunner) {
        super(api);
        this.arTagReaderWrapper = arTagReaderWrapper;
        this.laserGunner = laserGunner;
        this.context = context;
        this.kiboCamImageHeight = context.getResources().getInteger(R.integer.kibo_cam_image_height);
        this.kiboCamImageWidth = context.getResources().getInteger(R.integer.kibo_cam_image_width);
        this.percentThatCropRemoves = context.getResources().getInteger(R.integer.percent_of_image_that_crop_removes);
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
                return new GenericRobotOrderResult(true, 0, ""); // TODO... Return good result
            }

        }

        return new GenericRobotOrderResult(false, 1, ""); // TODO... Return bad result
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
            } else { // TODO... check that casting is safe here by using result.getType()
                if (!(result.getType() == RobotARTagReadOrderResult.class)) {
                    throw new RobotOrderException("Incorrect type");
                }
                // Getting bundled data out of result
                arTags = ((RobotARTagReadOrderResult) result).getARTagCollection();
                board = ((RobotARTagReadOrderResult) result).getBoard();

                // Complete success, both board and tags were returned
                if (result.getReturnValue() == 0) {
                    this.homographyMatrix = calculateBoardPose(board, arTagCollection); // TODO... pass this through Result instead
                    return new GenericRobotOrderResult(true, 0, ""); // TODO... Pass good result not null
                    // Got tags but there weren't enough to build board (<4)
                } else if (result.getReturnValue() == 1) {
                    // Move robot and then we'll try again
                    Quaternion adjustment = getAdjustmentNeededToFindTags(arTags);
                    rotateRobot(adjustment);
                } else {
                    throw new RobotOrderException("Return value for AR Tag reading: " + result.getReturnValue() + " has not been implemented in RobotFireLaserOrder.attemptOrder()");
                }
            }
        }
        return new GenericRobotOrderResult(false, 1, ""); // TODO... pass bad result not null
    }

    /**
     * Uses the Aruco.estimatePoseBoard() function to find the rotation and translation vectors for
     * the given board. Wraps them in a Homography matrix to return.
     *
     * @param board : Board that exists on some separate coordinate system to the image
     * @param arTagCollection : AR tags found on the board
     * @return : The homography matrix that describes the translation from one coordinate system to
     *           the other
     */
    private HomographyMatrix calculateBoardPose(Board board, ARTagCollection arTagCollection) { // TODO...

        double[][] camIntrinsics = api.getNavCamIntrinsics();

        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        cameraMatrix.put(0, 0, camIntrinsics[0]);
        distCoeffs.put(0, 1, camIntrinsics[1]);

        Mat rvec = new Mat();
        Mat tvec = new Mat();

        Aruco.estimatePoseBoard(arTagCollection.getTagCornersMat(), arTagCollection.getTagIDsMat(), board, cameraMatrix, distCoeffs, rvec, tvec);
        HomographyMatrix homographyMatrix = new HomographyMatrix(rvec, tvec, arTagCollection, new Size(1280,960));
        return homographyMatrix;
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
        ImageHelper iH = new ImageHelper(this.context);
        Mat croppedMat = new Mat(matImage, iH.getCroppedImageRectangleArea(percentThatCropRemoves, kiboCamImageHeight, kiboCamImageWidth));
        return croppedMat; // TODO...
    }


}

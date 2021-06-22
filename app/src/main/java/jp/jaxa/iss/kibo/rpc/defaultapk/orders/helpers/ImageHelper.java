package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;

import static org.opencv.android.Utils.matToBitmap;

public class ImageHelper {

    // TODO... Move all to integers.xml
    private final int kiboCamImageHeight; // True for both nav and dock cam
    private final int kiboCamImageWidth;
    private final int percentWidthCropRemoves;
    private final int percentHeightTopCropRemoves;
    private final int percentHeightBottomCropRemoves;
    private final int scaleFactorAsInt;




    public ImageHelper(Context context) {
        this.kiboCamImageHeight = context.getResources().getInteger(R.integer.kibo_cam_image_height);
        this.kiboCamImageWidth = context.getResources().getInteger(R.integer.kibo_cam_image_width);
        this.percentWidthCropRemoves = context.getResources().getInteger(R.integer.percent_width_crop_removes);
        this.percentHeightTopCropRemoves = context.getResources().getInteger(R.integer.percent_height_top_crop_removes);
        this.percentHeightBottomCropRemoves = context.getResources().getInteger(R.integer.percent_height_bottom_crop_removes);
        this.scaleFactorAsInt = context.getResources().getInteger(R.integer.scale_factor_as_int);
    }


    /**
     * getBinaryBitmapFromMatImage takes a camera image, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public BinaryBitmap getBinaryBitmapFromMatImage(Mat matFromCam, double[][] camIntrinsics) { // TODO... Comment
        Log.d("Attempting to get Bitmap from Mat","");
        Mat undistortedMat = undistortFisheye(matFromCam, camIntrinsics);
        Log.d("Attempting to crop mat", "");
        Mat croppedMat = new Mat(undistortedMat, getCroppedImageRectangleArea(undistortedMat.size()));

        // Commented out as unsure if needed
        // Mat scaledMat = scaleMatDown(croppedMat, resizeImageWidth, resizeImageHeight);

        Bitmap bitmap = getBitmapFromMat(croppedMat); // Move this into separate method?

        Log.d("Attempting to convert bitmap to pixel array","");
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int[] pixelArray = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        // Lot of OpenCV stuff that I don't understand
        Log.d("Attempting to return as Binary Bitmap","");
        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmapWidth, bitmapHeight, pixelArray)));
    }

    public Mat scaleMatDown(Mat originalMat) { // TODO... Comment
        Log.d("Attempting to scale mat:", "Scale factor as percent int: " + scaleFactorAsInt);
        Mat newMat = new Mat();
        double scaleFactor = (double)(scaleFactorAsInt)/100.0;

        Imgproc.resize(originalMat, newMat, new Size(0,0), scaleFactor, scaleFactor, Imgproc.INTER_AREA); // Inter area best for decreasing size
        return newMat;
    }

    public Bitmap getBitmapFromMat(Mat mat) { // TODO... Figure out why we aren't using the built in function in the api for this
        Log.d("Attempting to convert mat to bitmap", "");
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        matToBitmap(mat, bitmap, false);
        return bitmap;
    }

    /**
     * undistorts image to allow for calculations
     */
    public Mat undistortFisheye(Mat distortedImageAsMat, double[][] cameraIntrinsics) {
        Log.d("Attempting to undistortFisheye mat","");

        Mat undistortedImageAsMat = new Mat(this.kiboCamImageHeight, this.kiboCamImageWidth, CvType.CV_8UC1); // Do we need to set the number of pixels here?

        Mat[] cameraIntrinsicsMats = getCameraIntrinsicsAsMats(cameraIntrinsics);

        Calib3d.fisheye_undistortImage(distortedImageAsMat, undistortedImageAsMat, cameraIntrinsicsMats[0], cameraIntrinsicsMats[1]);

        return undistortedImageAsMat;
    }

    public Mat[] getCameraIntrinsicsAsMats(double[][] cameraIntrinsics) {
        Mat cameraIntrinsicsMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distortionCoefficients = new Mat(1, 5, CvType.CV_32FC1);

        cameraIntrinsicsMatrix.put(0, 0, cameraIntrinsics[0]);
        distortionCoefficients.put(0, 1, cameraIntrinsics[1]);

        return new Mat[]{cameraIntrinsicsMatrix, distortionCoefficients};
    }

    // TODO... Javadoc comment
    public Rect getCroppedImageRectangleArea(Size pictureSize) { // Should probably be getting crop Region Of Interest (ROI) from a function not a random number
        Log.d("Calculating rectangle for crop: ",
                "\nRemoving percent of width: " + this.percentWidthCropRemoves +
                "\nRemoving percent of height from top: " + percentHeightTopCropRemoves +
                "\nRemoving percent of height from bottom: " + percentHeightBottomCropRemoves
        );

        // Origin of coordinate system is top left
        int numColumns = (int)pictureSize.width;
        int numRows = (int)pictureSize.height;

        // Converting 0-100 percent to 0-1 double
        double percentWidthRemoved = (double)(percentWidthCropRemoves)/100.0;
        double percentHeightRemovedFromTop = (double)(percentHeightTopCropRemoves)/100.0;
        double percentHeightRemovedFromBottom = (double)(percentHeightBottomCropRemoves)/100.0;

        // Percent of columns to offset the start of the cropped image by. Half of total removed as exists on both sides of inner cropped image
        double percentColumnOffset = percentWidthRemoved/2;

        // Number of columns/rows to offset the non-cropped area by
        int numColumnsOffset = (int)(numColumns * percentColumnOffset);
        int numRowsOffsetFromTop = (int)(numRows * percentHeightRemovedFromTop);
        int numRowsOffsetFromBottom = (int)(numRows * percentHeightRemovedFromBottom);

        // Number of rows/columns to keep in the cropped image
        int numRemainingColumns = numColumns - (numColumnsOffset + numColumnsOffset); // Offset from both sides by same amount
        int numRemainingRows = numRows - (numRowsOffsetFromTop + numRowsOffsetFromBottom);

        // Return the specified area to keep after cropping as a Rectangle object, top left coordinate system so y is calculated from top
        return new Rect(numColumnsOffset, numRowsOffsetFromTop, numRemainingColumns, numRemainingRows);
    }




}

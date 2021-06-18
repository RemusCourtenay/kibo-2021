package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.matToBitmap;

public class ImageHelper {

    // TODO... Move all to integers.xml
    private static final int KIBO_CAM_IMAGE_HEIGHT = 1280; // True for both nav and dock cam
    private static final int KIBO_CAM_IMAGE_WIDTH = 960;

    private static final int PERCENT_THAT_CROP_REMOVES = 40;
    private static final int RESIZE_IMAGE_WIDTH = KIBO_CAM_IMAGE_HEIGHT * PERCENT_THAT_CROP_REMOVES /2; // Picked at random
    private static final int RESIZE_IMAGE_HEIGHT = KIBO_CAM_IMAGE_WIDTH * PERCENT_THAT_CROP_REMOVES /2; // Picked at random

    /**
     * getBinaryBitmapFromMatImage gets the image from the camera, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public static BinaryBitmap getBinaryBitmapFromMatImage(Mat matFromCam, double[][] camIntrinsics) { // TODO... Comment
        Mat undistortedMat = undistort(matFromCam, camIntrinsics);
        Mat croppedMat = new Mat(undistortedMat, getCroppedImageRectangleArea(PERCENT_THAT_CROP_REMOVES, KIBO_CAM_IMAGE_HEIGHT, KIBO_CAM_IMAGE_WIDTH));
        Mat scaledMat = scaleMatDown(croppedMat, RESIZE_IMAGE_WIDTH, RESIZE_IMAGE_HEIGHT);

        Bitmap bitmap = getBitmapFromMat(scaledMat);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int[] pixelArray = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight); // Magic numbers

        // Lot of OpenCV stuff that I don't understand
        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmapWidth, bitmapHeight, pixelArray)));
    }

    public static Mat scaleMatDown(Mat originalMat, int newWidth, int newHeight) { // TODO... Comment
        Mat newMat = new Mat();

        double widthScale = (double)newWidth/(double)originalMat.width(); // getting width from here rather than static variable because of image cropping
        double heightScale = (double)newHeight/(double)originalMat.height(); // getting height from here rather than static variable because of image cropping

        Imgproc.resize(originalMat, newMat, new Size(0,0), widthScale, heightScale, Imgproc.INTER_AREA); // Inter area best for decreasing size
        return newMat;
    }

    public static Bitmap getBitmapFromMat(Mat mat) { // TODO... Comment
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        matToBitmap(mat, bitmap, false);
        return bitmap;
    }

    /**
     * undistorts image to reduce time taken for QR scanning
     */
    public static Mat undistort(Mat src, double[][] camIntrinsics) {
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        cameraMatrix.put(0, 0, camIntrinsics[0]);
        distCoeffs.put(0, 1, camIntrinsics[1]);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }

    // TODO... Javadoc comment
    public static Rect getCroppedImageRectangleArea(double percentRemoved, int numRows, int numColumns) {
        // Ratio of image width:height
        double ratio = (double)numColumns / (double)numRows;

        // Percent of rows to offset the start of the cropped image by. Half of total removed as exists on both sides of inner cropped image
        double percentRowOffset = percentRemoved/2;
        // Percent of columns to offset the start of the cropped image by. Calculated from ratio of width:height in order to keep the image aspect ratio
        double percentColumnOffset = percentRowOffset * ratio;

        // Number of rows/columns to offset the non-cropped area by
        int numRowsOffset = (int)(numRows * percentRowOffset); // Casting to int to ensure round number
        int numColumnsOffset = (int)(numColumns * percentColumnOffset);

        // Number of rows/columns to keep in the cropped image
        int numRemainingRows = numRows - (numRowsOffset * 2); // Multiply by two to deal with the divide by 2 done earlier
        int numRemainingColumns = numColumns - (numColumnsOffset * 2);

        // Return the specified area to keep after cropping as a Rectangle object
        return new Rect(numColumnsOffset, numRowsOffset, numRemainingColumns, numRemainingRows);
    }


}

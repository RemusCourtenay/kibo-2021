package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

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
    private final int percentThatCropRemoves;
    private final int resizeImageHeight;
    private final int resizeImageWidth;



    ImageHelper(Context context) {
        this.kiboCamImageHeight = context.getResources().getInteger(R.integer.kibo_cam_image_height);
        this.kiboCamImageWidth = context.getResources().getInteger(R.integer.kibo_cam_image_width);
        this.percentThatCropRemoves = context.getResources().getInteger(R.integer.percent_of_image_that_crop_removes);
        this.resizeImageHeight = context.getResources().getInteger(R.integer.resize_image_height);
        this.resizeImageWidth = context.getResources().getInteger(R.integer.resize_image_width);
    }


    /**
     * getBinaryBitmapFromMatImage takes a camera image, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public BinaryBitmap getBinaryBitmapFromMatImage(Mat matFromCam, double[][] camIntrinsics) { // TODO... Comment
        Mat undistortedMat = undistort(matFromCam, camIntrinsics);
        Mat croppedMat = new Mat(undistortedMat, getCroppedImageRectangleArea(percentThatCropRemoves, kiboCamImageHeight, kiboCamImageWidth));

        // Commented out as unsure if needed
        // Mat scaledMat = scaleMatDown(croppedMat, resizeImageWidth, resizeImageHeight);

        Bitmap bitmap = getBitmapFromMat(croppedMat); // Move this into separate method?

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int[] pixelArray = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        // Lot of OpenCV stuff that I don't understand
        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmapWidth, bitmapHeight, pixelArray)));
    }

    public Mat scaleMatDown(Mat originalMat, int newWidth, int newHeight) { // TODO... Comment
        Mat newMat = new Mat();

        double widthScale = (double)newWidth/(double)originalMat.width(); // getting width from here rather than static variable because of image cropping
        double heightScale = (double)newHeight/(double)originalMat.height(); // getting height from here rather than static variable because of image cropping

        Imgproc.resize(originalMat, newMat, new Size(0,0), widthScale, heightScale, Imgproc.INTER_AREA); // Inter area best for decreasing size
        return newMat;
    }

    public Bitmap getBitmapFromMat(Mat mat) { // TODO... Comment
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        matToBitmap(mat, bitmap, false);
        return bitmap;
    }

    /**
     * undistorts image to reduce time taken for QR scanning
     */
    public Mat undistort(Mat src, double[][] camIntrinsics) { // Not sure about any of this
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        cameraMatrix.put(0, 0, camIntrinsics[0]);
        distCoeffs.put(0, 1, camIntrinsics[1]);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }

    // TODO... Javadoc comment
    public Rect getCroppedImageRectangleArea(double percentRemoved, int numRows, int numColumns) {
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

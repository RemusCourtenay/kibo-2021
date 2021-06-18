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

    private static final int KIBO_CAM_IMAGE_HEIGHT = 1280; // True for both nav and dock cam
    private static final int KIBO_CAM_IMAGE_WIDTH = 960;

    private static final int NAV_MAX_COL = 1280; // TODO... Move all to Integers.xml
    private static final int NAV_MAX_ROW =  960;
    private static final int PERCENT_CROP = 40;
    private static final int RESIZE_IMAGE_WIDTH = KIBO_CAM_IMAGE_HEIGHT *PERCENT_CROP/2; // Picked at random
    private static final int RESIZE_IMAGE_HEIGHT = KIBO_CAM_IMAGE_WIDTH *PERCENT_CROP/2; // Picked at random

    /**
     * getImgBinBitmap gets the image from the camera, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public static BinaryBitmap getImgBinBitmap(Mat matCam, double[][] camIntrinsics) { // TODO... Comment
        Mat mat = new Mat(undistort(matCam, camIntrinsics), cropImage(PERCENT_CROP));
        mat = scaleMatDown(RESIZE_IMAGE_WIDTH, RESIZE_IMAGE_HEIGHT, mat);
        Bitmap bitmap = getBitmapFromMat(mat);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int[] pixelArray = new int[bitmapWidth * bitmapHeight];
        bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight); // Magic numbers

        LuminanceSource luminanceSource = new RGBLuminanceSource(bitmapWidth, bitmapHeight, pixelArray);
        return new BinaryBitmap(new HybridBinarizer(luminanceSource));
    }

    public static Mat scaleMatDown(int newWidth, int newHeight, Mat originalMat) { // TODO... Comment
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
     * undistort undistorts image to reduce time taken for QR scanning
     * @param src
     * @return Mat dst
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

    /**
     * cropImage crops image to reduce time taken for QR scanning
     * @param percent_crop
     * @return Cropped image
     */
    public static Rect cropImage(int percent_crop) {
        double ratio = NAV_MAX_COL / NAV_MAX_ROW;  // Is this supposed to be integer division?

        double percent_row = percent_crop/2; // Is this supposed to be integer division?
        double percent_col = percent_row * ratio;

        int offset_row = (int) percent_row * NAV_MAX_ROW / 100;
        int offset_col = (int) percent_col * NAV_MAX_COL / 100;
        double rows = NAV_MAX_ROW - (offset_row * 2);
        double cols = NAV_MAX_COL - (offset_col * 2);

        return new Rect(offset_col, offset_row, (int) cols, (int) rows);
    }


}

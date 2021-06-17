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

    private static final int NAV_MAX_COL = 1280; // TODO... Move to Integers.xml
    private static final int NAV_MAX_ROW =  960;

    /**
     * getImgBinBitmap gets the image from the camera, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public static BinaryBitmap getImgBinBitmap(Mat matNavCam, double[][] dockCamIntrinsics) {
        Mat src_mat = new Mat(undistort(matNavCam, dockCamIntrinsics), cropImage(40));
        Bitmap bMap = resizeImage(src_mat, 2000, 1500);

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return bitmap;
    }

    /**
     * resizeImage resizes image to reduce time taken for QR scanning
     * @param src
     * @param width
     * @param height
     * @return Bitmap of resized image
     */
    public static Bitmap resizeImage(Mat src, int width, int height) {
        Size size = new Size(width, height);
        Imgproc.resize(src, src, size);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(src, bitmap, false);
        return bitmap;
    }

    /**
     * undistort undistorts image to reduce time taken for QR scanning
     * @param src
     * @return Mat dst
     */
    public static Mat undistort(Mat src, double[][] dockCamIntrisics) {
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        cameraMatrix.put(0, 0, dockCamIntrisics[0]);
        distCoeffs.put(0, 1, dockCamIntrisics[1]);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }

    /**
     * cropImage crops image to reduce time taken for QR scanning
     * @param percent_crop
     * @return Cropped image
     */
    public static Rect cropImage(int percent_crop) {
        double ratio = NAV_MAX_COL / NAV_MAX_ROW;

        double percent_row = percent_crop/2; // Is this supposed to be integer division?
        double percent_col = percent_row * ratio;

        int offset_row = (int) percent_row * NAV_MAX_ROW / 100;
        int offset_col = (int) percent_col * NAV_MAX_COL / 100;
        double rows = NAV_MAX_ROW - (offset_row * 2);
        double cols = NAV_MAX_COL - (offset_col * 2);

        return new Rect(offset_col, offset_row, (int) cols, (int) rows);
    }


}

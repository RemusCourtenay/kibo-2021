package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.core.Rect;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import static org.opencv.android.Utils.matToBitmap;

import java.util.concurrent.TimeUnit;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    int NAV_MAX_COL = 1280;
    int NAV_MAX_ROW =  960;

    @Override
    protected void runPlan1(){
        // astrobee is undocked and the mission starts
        api.startMission();

        // move astrobee from dock station to point A
        moveToWrapper(11.21+0.0422, -9.80, 4.79+0.0826, 0, 0, -0.707, 0.707);

        // scan QR code
        final double[]A_dash = scanQR(3);

        // get position and KOZ pattern
        double koz = A_dash[0], A_dash_x = A_dash[1], A_dash_y = A_dash[2], A_dash_z = A_dash[3];

        //
        // MOVE TO POINT A' WHILE AVOIDING KOZ AND GET ROBOT IN CORRECT ORIENTATION FOR LASER
        //

        // turn laser on
        api.laserControl(true);

        // take 10 snapshots at 1-second intervals
        int num_snaps = 10;
        int i = 0;
        while (i < num_snaps) {
            api.takeSnapshot();
            wait(1000);
        }

        // turn laser off
        api.laserControl(false);
    }

    @Override
    protected void runPlan2(){
    }

    @Override
    protected void runPlan3(){
    }

    // add custom methods here
    private void moveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w){

        final int LOOP_MAX = 3;
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float)qua_x, (float)qua_y,
                (float)qua_z, (float)qua_w);

        Result result = api.moveTo(point, quaternion, true);

        int loopCounter = 0;
        while(!result.hasSucceeded() || loopCounter < LOOP_MAX){
            result = api.moveTo(point, quaternion, true);
            ++loopCounter;
        }
    }

    /**
     * scanQR scans the QR code.
     * @param loop_max
     * @return double array of x,y,z coordinates and kox-pattern
     */
    public double[] scanQR(int loop_max) {
        String contents = null;
        int count = 0;
        double koz_pattern = 0; // KOZ pattern between 1 and 8
        double x = 0, y = 0, z = 0; // don't need orientation of point A' since it's always (0, 0, -0.707, 0.707)

        api.flashlightControlFront(1f);
        while (contents == null && count < loop_max) {
            BinaryBitmap bitmap = getImgBinBitmap();
            try {
                com.google.zxing.Result result = new QRCodeReader().decode(bitmap);
                contents = result.getText();
                Log.d("QR[status]:", " Detected");
                String[] format_split = contents.split(",");
                String[] p_multi_contents = format_split[0].split(":");
                String[] x_multi_contents = format_split[1].split(":");
                String[] y_multi_contents = format_split[2].split(":");
                String[] z_multi_contents = format_split[3].split(":");
                koz_pattern = Double.parseDouble(p_multi_contents[1]);
                x = Double.parseDouble(x_multi_contents[1]);
                y = Double.parseDouble(y_multi_contents[1]);
                z = Double.parseDouble(z_multi_contents[1]);
            }
            catch (Exception e) {
                Log.d("QR[status]:", " Not detected");
            }
            count++;
        }
        api.flashlightControlFront(0f);

        api.sendDiscoveredQR(contents); // send the content of QR code for judge
        return new double[] {koz_pattern, x, y, z};
    }

    // undistort image to reduce time taken for QR scanning
    public Mat undistort(Mat src) {
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        double cameraIntrinsics[][] = api.getDockCamIntrinsics();
        cameraMatrix.put(0, 0, cameraIntrinsics[0]);
        distCoeffs.put(0, 1, cameraIntrinsics[1]);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }

    // crop image to reduce time taken for QR scanning
    public Rect cropImage(int percent_crop) {
        double ratio = NAV_MAX_COL / NAV_MAX_ROW;

        double percent_row = percent_crop/2;
        double percent_col = percent_row * ratio;

        int offset_row = (int) percent_row * NAV_MAX_ROW / 100;
        int offset_col = (int) percent_col * NAV_MAX_COL / 100;
        double rows = NAV_MAX_ROW - (offset_row * 2);
        double cols = NAV_MAX_COL - (offset_col * 2);

        return new Rect(offset_col, offset_row, (int) cols, (int) rows);
    }

    // resize image to reduce time taken for QR scanning
    public Bitmap resizeImage(Mat src, int width, int height) {
        Size size = new Size(width, height);
        Imgproc.resize(src, src, size);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(src, bitmap, false);
        return bitmap;
    }

    /**
     * getImgBinBitmap gets the image from the camera, and returns a binary bitmap for it.
     * @return BinaryBitmap for camera image
     */
    public BinaryBitmap getImgBinBitmap() {
        Mat src_mat = new Mat(undistort(api.getMatNavCam()), cropImage(40));
        Bitmap bMap = resizeImage(src_mat, 2000, 1500);

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return bitmap;
    }

    //
    public static void wait(int ms) {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}
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
        final double[]Adash_pos = scanQR(3);

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

    public double[] scanQR(int loop_max) {

        String contents = null;
        int count = 0;
        double koz_pattern = 0; // KOZ pattern between 1 and 8
        double x = 0, y = 0, z = 0; // not getting orientation of point A' since it's always (0, 0, -0.707, 0.707)

        while (contents == null && count < loop_max) {
            api.flashlightControlFront(1f);
            Mat src_mat = new Mat(undistord(api.getMatNavCam()), cropImage(40));
            Bitmap bMap = resizeImage(src_mat, 2000, 1500);

            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                com.google.zxing.Result result = new QRCodeReader().decode(bitmap);
                contents = result.getText();
                Log.d("QR[status]:", " Detected");

                String[] multi_contents = contents.split(", ");
                koz_pattern = Double.parseDouble(multi_contents[1]);
                x = Double.parseDouble(multi_contents[3]);
                y = Double.parseDouble(multi_contents[5]);
                z = Double.parseDouble(multi_contents[7]);
            }
            catch (Exception e) {
                Log.d("QR[status]:", " Not detected");
            }

            ++count;
        }

        api.flashlightControlFront(0f);
        api.sendDiscoveredQR(contents); // send the content of QR code for judge

        return new double[] {x, y, z, koz_pattern};
    }

    public Mat undistord(Mat src) {
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        double cameraIntrinsics[][] = api.getDockCamIntrinsics();
        cameraMatrix.put(0, 0, cameraIntrinsics[0]);
        distCoeffs.put(0, 1, cameraIntrinsics[1]);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }

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

    public Bitmap resizeImage(Mat src, int width, int height) {
        Size size = new Size(width, height);
        Imgproc.resize(src, src, size);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(src, bitmap, false);
        return bitmap;
    }
}
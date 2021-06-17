package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

import static jp.jaxa.iss.kibo.rpc.defaultapk.ImageHelper.getImgBinBitmap;
import static org.opencv.android.Utils.matToBitmap;

class RobotScanARCodeOrder extends RobotOrder {

    private final int loopMax;

    private double[] scanResult;

    RobotScanARCodeOrder(KiboRpcApi api, int loopMax) {
        super(api);
        this.loopMax = loopMax;
    }

    @Override
    protected Result attemptOrderImplementation() {

        scanResult = scanQR(this.loopMax); // Get result from other teams code here

        return null; // TODO... should return a Result
    }

    @Override
    public String printOrderInfo() {
        return null;
    } // TODO...

    /**
     * Overriding the standard attemptOrder() method to allow for the addition of the scan result
     */
    @Override
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation(), this.scanResult);
    }


    /**
     * scanQR scans the QR code.
     * @param loop_max
     * @return double array of x,y,z coordinates and kox-pattern
     */
    private double[] scanQR(int loop_max) {
        double koz_pattern = 0; // KOZ pattern between 1 and 8
        double x = 0, y = 0, z = 0; // don't need orientation of point A' since it's always (0, 0, -0.707, 0.707)
        String contents = readQR(loop_max);
        if (contents != null) {
            String[] format_split = contents.split(",");
            String[] p_multi_contents = format_split[0].split(":");
            String[] x_multi_contents = format_split[1].split(":");
            String[] y_multi_contents = format_split[2].split(":");
            String[] z_multi_contents = format_split[3].split(":");
            koz_pattern = Double.parseDouble(p_multi_contents[1]);
            x = Double.parseDouble(x_multi_contents[1]);
            y = Double.parseDouble(y_multi_contents[1]);
            z = Double.parseDouble(z_multi_contents[1]);
            api.sendDiscoveredQR(contents); // send the content of QR code for judge
        }
        return new double[] {koz_pattern, x, y, z};
    }



    /**
     * readQR is used to read a QR code
     * @param loop_max
     * @return String content of the QR code
     */
    private String readQR(int loop_max) {
        int count = 0;
        String contents = null;
        api.flashlightControlFront(0f);
        while (contents == null && count < loop_max) {
            if (count < 40) {
                api.flashlightControlFront((count+1)*0.025f);
            }
            BinaryBitmap bitmap = getImgBinBitmap(api.getMatNavCam(), api.getDockCamIntrinsics());
            try {
                com.google.zxing.Result result = new QRCodeReader().decode(bitmap);
                contents = result.getText();
                Log.d("QR[status]:", " Detected");

            }
            catch (Exception e) {
                Log.d("QR[status]:", " Not detected");
            }
            count++;
        }
        api.flashlightControlFront(0f);
        return contents;
    }




}

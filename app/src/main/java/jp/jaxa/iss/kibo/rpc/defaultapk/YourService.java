package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import android.graphics.Bitmap;

// import com.google.zxing.qrcode.QRCodeReader;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        // astrobee is undocked and the mission starts
        api.startMission();

        // move astrobee from dock station to point A
        moveToWrapper(11.21, -9.80, 4.79, 0, 0, -0.707, 0.707);

        // scan QR code
        //final double[]A_dash_pos = scanQR(3);

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

    private void scanQR(int loop_max) {

        String contents = null;
        int count = 0;
        int koz_pattern = 0; // KOZ pattern between 1 and 8
        double x = 0, y = 0, z = 0; // not getting orientation of point A' since it's always (0, 0, -0.707, 0.707)

        while (contents == null && count < loop_max) {
            api.flashlightControlFront(0.5f);
            //Bitmap image = api.getBitmapNavCam();
            ++count;
        }

        //com.google.zxing.Result result = new QRCodeReader().decode(image); - might need to add zxing as a dependency in the build.gradle file
        //contents = result.getText();
        //String[] split_contents = contents.split(",");
        //x = Double.parseDouble(split_contents[1]);
        //y = Double.parseDouble(split_contents[3]);
        //z = Double.parseDouble(split_contents[5]);
        //koz_pattern = String.parseString(split_contents[7]);

        api.flashlightControlFront(0f);
        //api.sendDiscoveredQR(split_contents);  - send the content of QR code for judge

        //return final double[] {x, y, z, koz_pattern};
    }
}
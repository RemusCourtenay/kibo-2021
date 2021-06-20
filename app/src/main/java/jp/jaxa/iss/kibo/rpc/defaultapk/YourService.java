package jp.jaxa.iss.kibo.rpc.defaultapk;


import android.content.Context;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderResult;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.types.Point;

import android.os.SystemClock;
import android.util.Log;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    @Override
    protected void runPlan1(){

        // Setting up wrapper
        Context context = getBaseContext();
        RobotOrderBuilder orderBuilder = new RobotOrderBuilder(context, this.api);
        ExpeditionWrapper wrapper = new ExpeditionWrapper(orderBuilder);

        // Attempting first stage (move to QR Code and scan)
        RobotOrderResult result = wrapper.attemptExpeditionStage(context.getString(R.string.move_to_scan_order_string));
        double[] scanResults = result.getReturnValue();

        // Ignoring result rn while we wait for the scan team to finish their section
        //int nextPathNum = (int)scanResults[0];
        // Getting which path to follow from the testing variable located in /app/src/main/res/values/integers.xml instead
        int nextPathNum = context.getResources().getInteger(R.integer.chosen_path_to_test);

        // Ignoring result rn while we wait for the scan team to finish their section
        //float pointADashXValue = (float)scanResults[1];
        //float pointADashYValue = (float)scanResults[2];
        //float pointADashZValue = (float)scanResults[3];
        // Getting which point A' is in from the testing variable located in /app/src/main/res/values/integers.xml instead
        float pointADashXValue = (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_x_value);
        float pointADashYValue = (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_y_value);
        float pointADashZValue = (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_z_value);

        orderBuilder.setPointADash(
                pointADashXValue,
                pointADashYValue,
                pointADashZValue
        );

        String nextStageOrderString;
        switch (nextPathNum) {
            case 1: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_1); break;
            case 2: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_2); break;
            case 3: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_3); break;
            case 4: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_4); break;
            case 5: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_5); break;
            case 6: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_6); break;
            case 7: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_7); break;
            case 8: nextStageOrderString = context.getString(R.string.move_to_laser_order_string_variant_8); break;
            default: throw new RuntimeException(
                    "Integer resource 'chosen path to text' set to invalid value: " +
                            context.getResources().getInteger(R.integer.chosen_path_to_test) + "\n" +
                    "Value should be between 1 and 8");
        }

        // Attempting the second stage (move to point A' and fire laser)
        wrapper.attemptExpeditionStage(nextStageOrderString);

        // Attempting the final stage (move to finish)
        wrapper.attemptExpeditionStage(context.getString(R.string.move_to_finish_order_string));





//        // ------------------------------- NEW STUFF -------------------------------------- //
//
//        // scan QR code
//        final double[]A_dash = scanQR(40); // Has been moved to RobotScanARCodeOrder
//
//        // get position and KOZ pattern
//        double koz = A_dash[0], A_dash_x = A_dash[1], A_dash_y = A_dash[2], A_dash_z = A_dash[3];
//
//        //
//        // MOVE TO POINT A' WHILE AVOIDING KOZ AND GET ROBOT IN CORRECT ORIENTATION FOR LASER
//        //
//
//        // turn laser on
//        api.laserControl(true);
//
//        // take 10 snapshots at 1-second intervals
//        int num_snaps = 10;
//        int i = 0;
//        while (i < num_snaps) {
//            api.takeSnapshot();
//            wait(1000);
//        }
//
//        // turn laser off
//        api.laserControl(false);
//    }
//
//    @Override
//    protected void runPlan2(){
//    }
//
//    @Override
//    protected void runPlan3(){
//    }
//
//
//    /**
//     * Intersection is used in AR_event
//     * @param p
//     * @return
//     */
//    private double[] Intersection(double p[][])
//    {
//        /*float AR_diagonal = 0.07071067812f;*/
//        float AR_diagonal = 0.1199103832f;
//        double[] center = new double[3];
//
//        double a = (p[1][0] - p[0][0]) * (p[3][0] - p[2][0]);
//        double b = (p[1][0] - p[0][0]) * (p[3][1] - p[2][1]);
//        double c = (p[3][0] - p[2][0]) * (p[1][1] - p[0][1]);
//
//        center[0] = (a * p[0][1] + b * p[2][0] - a * p[2][1] - c * p[0][0]) / (b - c);
//        center[1] = ((p[1][1] - p[0][1]) * (center[0] - p[0][0]) / (p[1][0] - p[0][0])) + p[0][1];
//
//        double x_l1 = Math.pow(p[0][0] - p[1][0], 2);
//        double y_l1 = Math.pow(p[0][1] - p[1][1], 2);
//        double x_l2 = Math.pow(p[3][0] - p[2][0], 2);
//        double y_l2 = Math.pow(p[3][1] - p[2][1], 2);
//        double avg = (Math.sqrt(x_l1 + y_l1) + Math.sqrt(x_l2 + y_l2)) / 2;
//
//        center[2] = avg / AR_diagonal;
//        Log.d("AR[info]: ", center[0] + ", " + center[1] + ", " + center[2]);
//        return center;
//    }
//
//    /**
//     * AR_event used for AR stuff
//     * @param px
//     * @param py
//     * @param pz
//     * @param qx
//     * @param qy
//     * @param qz
//     * @param qw
//     * @param count_max
//     * @return
//     */
//    public double[] AR_event(float px, float py, float pz, float qx, float qy, float qz, float qw, int count_max)
//    {
//        int contents = 0, count = 0;
//        double result[] = new double[3];
//
//        while (contents == 0 && count < count_max)
//        {
//            Log.d("AR[status]:", " start");
//            long start_time = SystemClock.elapsedRealtime();
//            //
//
//
//            // COMMENTED OUT BECAUSE METHOD NO LONGER EXISTS
//            //moveToWrapper(px, py, pz, qx, qy, qz, qw);
//
//
//
//            //////////////////////////////////////////////////////////////////////////////////////////////////////
//            Mat source = undistort(api.getMatNavCam(), api.getDockCamIntrinsics());
//            Kinematics robot = api.getTrustedRobotKinematics();
//            Mat ids = new Mat();
//            Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
//            List<Mat> corners = new ArrayList<>();
//
//            try
//            {
//                Aruco.detectMarkers(source, dictionary, corners, ids);
//                contents = (int) ids.get(0, 0)[0];
//
//                /*if(sent_AR) api.judgeSendDiscoveredAR(Integer.toString(contents));*/
//                Log.d("AR[status]:", " Detected");
//
//
//                double[][] AR_corners =
//                        {
//                                {(int) corners.get(0).get(0, 0)[0], (int) corners.get(0).get(0, 0)[1]},
//                                {(int) corners.get(0).get(0, 2)[0], (int) corners.get(0).get(0, 2)[1]},
//                                {(int) corners.get(0).get(0, 1)[0], (int) corners.get(0).get(0, 1)[1]},
//                                {(int) corners.get(0).get(0, 3)[0], (int) corners.get(0).get(0, 3)[1]}
//                        };
//                double[] AR_info = Intersection(AR_corners);
//
//
//                Point point = new Point(px, py, pz);
//                if(robot != null)
//                {
//                    point = robot.getPosition();
//                    Log.d("getKinematics[status]:"," Finished");
//                }
//                result[0] = point.getX() + (AR_info[0]- NAV_MAX_COL/2) / AR_info[2];
//                result[1] = point.getY();
//                result[2] = point.getZ() + (AR_info[1]- NAV_MAX_ROW/2) / AR_info[2];
//            }
//            catch (Exception e)
//            {
//                Log.d("AR[status]:", " Not detected");
//            }
//            //////////////////////////////////////////////////////////////////////////////////////////////////////
//            Log.d("AR[status]:", " stop");
//            long stop_time = SystemClock.elapsedRealtime();
//
//
//
//            Log.d("AR[count]:", " " + count);
//            Log.d("AR[total_time]:"," "+ (stop_time-start_time)/1000);
//            count++;
//        }
//        return result;
//    }
//
//    /**
//     * Stops the thread for a second
//     * @param ms
//     */
//    public static void wait(int ms) {
//        try
//        {
//            Thread.sleep(ms);
//        }
//        catch(InterruptedException ex)
//        {
//            Thread.currentThread().interrupt();
//        }
    }
}
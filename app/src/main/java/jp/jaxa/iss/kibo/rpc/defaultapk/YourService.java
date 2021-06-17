package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.content.Context;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderResult;

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

        // Attempting first stage
        RobotOrderResult result = wrapper.attemptExpeditionStage(context.getString(R.string.move_to_scan_order_string));

        // Ignoring result rn while we wait for the scan team to finish their section

        // Getting which path to follow from the testing variable located in /app/src/main/res/values/integers.xml
        String nextStageOrderString;
        switch (context.getResources().getInteger(R.integer.chosen_path_to_test)) {
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

        // Ignoring result rn while we wait for the scan team to finish their section

        // Getting which point A' is in from the testing variable located in /app/src/main/res/values/integers.xml

        orderBuilder.setPointADash(
                (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_x_value),
                (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_y_value),
                (float)context.getResources().getInteger(R.integer.chosen_point_a_dash_z_value)
        );

        // Attempting the second stage
        wrapper.attemptExpeditionStage(nextStageOrderString);

        // Attempting the final stage
        wrapper.attemptExpeditionStage(context.getString(R.string.move_to_finish_order_string));
    }

    @Override
    protected void runPlan2(){
        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }

}


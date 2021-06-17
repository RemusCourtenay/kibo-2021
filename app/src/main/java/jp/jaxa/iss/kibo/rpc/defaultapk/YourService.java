package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.content.Context;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){

        Context context = getBaseContext();
        ExpeditionWrapper wrapper = new ExpeditionWrapper(new RobotOrderBuilder(context, this.api));
        wrapper.attemptExpeditionStage(context.getString(R.string.test_order_string));

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


package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

class RobotFireLaserOrder extends RobotOrder {

    private final ImageHelper imageHelper;

    RobotFireLaserOrder(KiboRpcApi api, Context context, ImageHelper imageHelper) {
        super(api);
        this.imageHelper = imageHelper;
    }

    @Override
    protected Result attemptOrderImplementation() {
        return null; // TODO...
    }

    @Override
    public String printOrderInfo() {
        return null;
    }
}

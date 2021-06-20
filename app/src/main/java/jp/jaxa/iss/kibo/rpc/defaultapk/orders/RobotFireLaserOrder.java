package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;

class RobotFireLaserOrder extends RobotOrder {

    RobotFireLaserOrder(KiboRpcApi api) {
        super(api);
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

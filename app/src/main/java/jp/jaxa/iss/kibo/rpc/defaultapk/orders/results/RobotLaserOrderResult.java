package jp.jaxa.iss.kibo.rpc.defaultapk.orders.results;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;

public class RobotLaserOrderResult extends RobotOrderResult {

    private final Quaternion quaternion;

    public RobotLaserOrderResult(Result result, Quaternion quaternion) {
        super(result);
        this.quaternion = quaternion;
    }

    public RobotLaserOrderResult(boolean succeeded, int returnValue, String message, Quaternion quaternion) {
        super(succeeded, returnValue, message);
        this.quaternion = quaternion;
    }

    public Quaternion getTranslatedQuaternion() {
        return this.quaternion;
    }

    @Override
    public Class<? extends RobotOrderResult> getType() {
        return RobotLaserOrderResult.class;
    }
}

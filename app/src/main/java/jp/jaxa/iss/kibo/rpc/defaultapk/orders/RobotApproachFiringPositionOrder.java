package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

class RobotApproachFiringPositionOrder extends RobotOrder {

    private static final float POINT_A_QUAT_X = 0; // TODO... Move to xml file
    private static final float POINT_A_QUAT_Y = 0;
    private static final float POINT_A_QUAT_Z = (float)-0.707;
    private static final float POINT_A_QUAT_W = (float)0.707;

    private final int loopMax;
    private final Point pointADash;
    private final Quaternion quatADash;

    RobotApproachFiringPositionOrder(KiboRpcApi api, int loopMax, Point pointADash) {
        super(api);
        this.loopMax = loopMax;
        this.pointADash = pointADash;
        this.quatADash = new Quaternion(POINT_A_QUAT_X, POINT_A_QUAT_Y, POINT_A_QUAT_Z, POINT_A_QUAT_W);
    }

    @Override
    public RobotOrderResult attemptOrder() { // Code copied from moveTo
        Result result = api.moveTo(pointADash, quatADash, true);

        for(int i = 0; i < loopMax; i++){
            if(result.hasSucceeded()){
                break;
            }
            result = api.moveTo(pointADash, quatADash, true);
        }

        return new GenericRobotOrderResult(result);
    }

    @Override
    public String printOrderInfo() {
        return "Approach firing position order:\n" +
                "Target point: ["+pointADash.getX()+"]["+pointADash.getY()+"]["+pointADash.getZ()+"]\n" +
                "Target quaternion: ["+quatADash.getX()+"]["+quatADash.getY()+"]["+quatADash.getZ()+"]["+quatADash.getW()+"]";
    }

}

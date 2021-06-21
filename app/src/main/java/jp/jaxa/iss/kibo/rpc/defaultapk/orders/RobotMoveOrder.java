package jp.jaxa.iss.kibo.rpc.defaultapk.orders;


import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

class RobotMoveOrder extends RobotOrder {

    private final Point targetPoint;
    private final Quaternion targetQuaternion;
    private final int loopMax;


    RobotMoveOrder(KiboRpcApi api, int loopMax, Point targetPoint, Quaternion targetQuaternion){
        super(api);
        this.targetPoint = targetPoint;
        this.targetQuaternion = targetQuaternion;
        this.loopMax = loopMax;

    }

    @Override
    public RobotOrderResult attemptOrder() {
        Result result = api.moveTo(targetPoint, targetQuaternion, true);

        for(int i = 0; i < loopMax; i++){
            if(result.hasSucceeded()){
                break;
            }
            result = api.moveTo(targetPoint, targetQuaternion, true);
        }

        return new GenericRobotOrderResult(result);
    }

    @Override
    public String printOrderInfo() {
        return "Move order:\n" +
                "Target point: ["+targetPoint.getX()+"]["+targetPoint.getY()+"]["+targetPoint.getZ()+"]\n" +
                "Target quaternion: ["+targetQuaternion.getX()+"]["+targetQuaternion.getY()+"]["+targetQuaternion.getZ()+"]["+targetQuaternion.getW()+"]";
    }

}

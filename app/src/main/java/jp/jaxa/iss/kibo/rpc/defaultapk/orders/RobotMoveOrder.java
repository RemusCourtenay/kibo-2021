package jp.jaxa.iss.kibo.rpc.defaultapk.orders;


import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

class RobotMoveOrder extends RobotOrder {

    private final Point targetPoint;
    private final Quaternion targetQuaternion;
    private final int loopMax;


    public RobotMoveOrder(int loopMax, Point targetPoint, Quaternion targetQuaternion){
        this.targetPoint = targetPoint;
        this.targetQuaternion = targetQuaternion;
        this.loopMax = loopMax;

    }

    @Override
    protected Result attemptOrderImplementation() {
        Result result = api.moveTo(targetPoint, targetQuaternion, true);

        for(int i = 0; i < loopMax; i++){
            if(result.hasSucceeded()){
                break;
            }
            result = api.moveTo(targetPoint, targetQuaternion, true);
        }

        return result;
    }

}

package jp.jaxa.iss.kibo.rpc.defaultapk.orders;


import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

class RobotMoveOrder extends RobotOrder {

    private final Point targetPoint;
    private final Quaternion targetQuaternion;
    final int LOOP_MAX;



    public RobotMoveOrder(int loopMax, double posX, double posY, double posZ, float quaX, float quaY, float quaZ, float quaW){
        targetPoint = new Point(posX, posY, posZ);
        targetQuaternion = new Quaternion(quaX, quaY, quaZ, quaW);
        LOOP_MAX = loopMax;

    }

    @Override
    public Result attemptOrder() {
        Result result = api.moveTo(targetPoint, targetQuaternion, true);

        for(int i = 0; i < LOOP_MAX; i++){
            if(result.hasSucceeded()){
                break;
            }
            result = api.moveTo(targetPoint, targetQuaternion, true);
        }

        return result; // TODO...
    }

}

package jp.jaxa.iss.kibo.rpc.defaultapk.orders;


import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.Result;


public class MoveToHack extends KiboRpcService{

    public Result moveTo(Point point, Quaternion quart, boolean res){
        Result result = api.moveTo(point, quart, res);

        return result;
    }

}

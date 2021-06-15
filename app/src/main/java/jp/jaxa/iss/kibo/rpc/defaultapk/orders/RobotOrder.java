package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

public abstract class RobotOrder extends KiboRpcService {

    public abstract Result attemptOrder();

}

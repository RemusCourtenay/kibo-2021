package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;

public enum RobotOrderType {
    START_MISSION_ORDER(R.string.start_mission_order_key),
    MOVE_ORDER(R.string.move_order_key),
    SCAN_AR_CODE_ORDER(R.string.scan_ar_code_order_key),
    FIRE_LASER_ORDER(R.string.fire_laser_order_key),
    FINISH_MISSION_ORDER(R.string.finish_mission_order_key);

    private final int orderKey;

    RobotOrderType(int orderKey) {
        this.orderKey = orderKey;
    }

    public int getOrderKey() {
        return this.orderKey;
    }
}

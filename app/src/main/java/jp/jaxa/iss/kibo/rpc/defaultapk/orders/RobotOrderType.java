package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;

public enum RobotOrderType {
    START_MISSION_ORDER(R.string.start_mission_order_key),
    MOVE_ORDER(R.string.move_order_key),
    APPROACH_FIRING_POSITION_ORDER(R.string.approach_firing_position_order_key),
    SCAN_QR_CODE_ORDER(R.string.scan_qr_code_order_key),
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

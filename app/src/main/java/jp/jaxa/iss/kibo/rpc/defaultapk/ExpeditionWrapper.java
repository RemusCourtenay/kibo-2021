package jp.jaxa.iss.kibo.rpc.defaultapk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderBuilder;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderType;


/**
 * Wrapper class to handle entire expedition. Exists in order to make testing order sets easier
 * for team members unfamiliar with coding.
 * Class will take significant time to return as it blocks while orders are being attempted.
 * Multithreading to allow for querying of expedition status whilst running is not supported by
 * Kibo robot hardware.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class ExpeditionWrapper {

    private static final HashMap<String, RobotOrderType> STRING_ORDER_TYPE_MAP;
    static {
        STRING_ORDER_TYPE_MAP = new HashMap<>();
        STRING_ORDER_TYPE_MAP.put("START_MISSION", RobotOrderType.START_MISSION_ORDER);
        STRING_ORDER_TYPE_MAP.put("Do not use this, use the bracket format instead", RobotOrderType.MOVE_ORDER);
        STRING_ORDER_TYPE_MAP.put("SCAN_AR_CODE", RobotOrderType.SCAN_AR_CODE_ORDER);
        STRING_ORDER_TYPE_MAP.put("FIRE_LASER", RobotOrderType.FIRE_LASER_ORDER);
        STRING_ORDER_TYPE_MAP.put("PLAY_SOUND", RobotOrderType.PLAY_SOUND_ORDER);
    }
    private static final String MOVE_ORDER_PATTERN =
                    // Regex pattern for move order format, allows decimals
                    "(?:\\{\\[)" +                                      // {[
                    "((\\d+([\\.]\\d+)?)[,]){2}(\\d+([\\.]\\d+)?){1}" + // 1,2,3
                    "(?:\\]\\[)" +                                      // ][
                    "((\\d+([\\.]\\d+)?)[,]){3}(\\d+([\\.]\\d+)?){1}" + // 1,2,3,4
                    "(?:\\]\\})";                                       // ]}

    private static final String SPLIT_ORDER_CHARACTER = "|";
    // Immutable list of orders
    private final List<RobotOrder> orders;
    // Index of currently active order
    private int currentOrder = 0;


    /**
     * Sole constructor. The orders parameter must contain a full string representation of the orders for
     * an entire expedition as the order array cannot be changed once instantiated. The expedition
     * will attempt each order in the string sequentially so ordering must be retained.
     *
     * @param fullOrderString    A full string containing all orders correctly formatted
     * @see   RobotOrder
     */
    public ExpeditionWrapper(String fullOrderString) {
        RobotOrderBuilder orderBuilder = new RobotOrderBuilder(
                STRING_ORDER_TYPE_MAP,
                MOVE_ORDER_PATTERN,
                SPLIT_ORDER_CHARACTER
        );
        this.orders = Collections.unmodifiableList(orderBuilder.buildOrders(fullOrderString));
        // TODO...
    }

    /**
     * Starts the wrapper iterating through it's orders. Will cause it's caller to block for a
     * long period as multithreading of orders is not implemented.
     */
    public void startExpedition() {
        // TODO...
    }
}

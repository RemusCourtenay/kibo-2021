package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;

/**
 * Standard builder class for setting up RobotOrder objects. Exists so that specific robot order
 * implementations can remain package-private.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class RobotOrderBuilder {

    private final Context context;

    private final Map<String, RobotOrderType> stringOrderTypeMap;
    private final Pattern moveOrderPattern;
    private final String orderSplitCharacter;

    public RobotOrderBuilder(Context context) {
        this.context = context;
        this.stringOrderTypeMap = buildStringOrderTypeMapFromStringsFile();
        this.moveOrderPattern = Pattern.compile(context.getString(R.string.move_order_regex_pattern)); // TODO... add error check
        this.orderSplitCharacter = context.getString(R.string.order_split_character);
    }

    /**
     * Public builder method that allows access from outside of package to instantiate RobotOrder
     * objects. Attempts to discern what type of order each input is and then wrap it in the
     * correct RobotOrder implementation.
     *
     * @param fullOrderString   the full string containing each movement order within the section
     * @return                  an array of robot orders
     */
    public List<RobotOrder> buildOrders(String fullOrderString) { // TODO... Comment
        ArrayList<RobotOrder> orders = new ArrayList<>();
        String[] orderTexts = fullOrderString.split(this.orderSplitCharacter);

        for (String orderText: orderTexts) {
            RobotOrderType type;
            if ((type = this.stringOrderTypeMap.get(orderText)) != null) {
                RobotOrder order;
                if (type == RobotOrderType.START_MISSION_ORDER) {
                    order = buildStartMissionOrder();
                } else if (type == RobotOrderType.SCAN_AR_CODE_ORDER) {
                    order = buildScanARCodeOrder();
                } else if (type == RobotOrderType.FIRE_LASER_ORDER) {
                    order = buildFireLaserOrder();
                } else if (type == RobotOrderType.PLAY_SOUND_ORDER) {
                    order = buildPlaySoundOrder();
                } else if (type == RobotOrderType.MOVE_ORDER) {
                    throw new RuntimeException("Move orders should be written using bracket format");
                } else {
                    throw new RuntimeException("RobotOrderType: " + type.name() + " not implemented in buildOrders");
                }
                orders.add(order);
            } else if (fitsMoveOrderFormat(orderText)) {
                orders.add(buildMoveOrder());
            } else {
                throw new RuntimeException("Order: " + orderText + " doesn't fit any format described by the strings.xml file");
            }
        }

        return orders;
    }

    RobotMoveOrder buildMoveOrder() {
        return null; // TODO...
    }

    RobotMoveOrder buildStartMissionOrder() {
        return null; // TODO...
    }

    RobotScanARCodeOrder buildScanARCodeOrder() {
        return null; // TODO...
    }

    RobotFireLaserOrder buildFireLaserOrder() {
        return null; // TODO...
    }

    RobotPlaySoundOrder buildPlaySoundOrder() {
        return null; // TODO...
    }

    private Map<String, RobotOrderType> buildStringOrderTypeMapFromStringsFile() {
        HashMap<String, RobotOrderType> stringOrderTypeMap = new HashMap<>();

        // Big shoutout to android for making me have to do this....
        stringOrderTypeMap.put(context.getString(R.string.start_mission_order_key), RobotOrderType.START_MISSION_ORDER);
        stringOrderTypeMap.put(context.getString(R.string.move_order_key), RobotOrderType.MOVE_ORDER);
        stringOrderTypeMap.put(context.getString(R.string.scan_ar_code_order_key), RobotOrderType.SCAN_AR_CODE_ORDER);
        stringOrderTypeMap.put(context.getString(R.string.fire_laser_order_key), RobotOrderType.FIRE_LASER_ORDER);
        stringOrderTypeMap.put(context.getString(R.string.play_sound_order_key), RobotOrderType.PLAY_SOUND_ORDER);

        return stringOrderTypeMap;
    }

    private boolean fitsMoveOrderFormat(String orderText) {
        Matcher moveOrderMatcher = this.moveOrderPattern.matcher(orderText);
        return moveOrderMatcher.matches();
    }

}

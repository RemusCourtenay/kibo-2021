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


        // Trying to figure out what type of order each chunk of the string is
        for (String orderText: orderTexts) {
            RobotOrderType type;

            // if .get() returns null then the type isn't in the string to order map
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

            // If it's not in the map then it's probably a move order, checking it fits the format so we don't get errors later
            } else if (fitsMoveOrderFormat(orderText)) {
                orders.add(buildMoveOrder());

            // Doesn't fit any of the mapped values and isn't a correctly formatted move order so throwing exception
            } else {
                throw new RuntimeException("Order: " + orderText + " doesn't fit any format described by the strings.xml file");
            }
        }
        return orders;
    }

    /**
     * Constructors for each order type
     */

    private RobotMoveOrder buildMoveOrder() {
        return null; // TODO...
    }

    private RobotMoveOrder buildStartMissionOrder() {
        return null; // TODO...
    }

    private RobotScanARCodeOrder buildScanARCodeOrder() {
        return null; // TODO...
    }

    private RobotFireLaserOrder buildFireLaserOrder() {
        return null; // TODO...
    }

    private RobotPlaySoundOrder buildPlaySoundOrder() {
        return null; // TODO...
    }


    /**
     * Helper function for building the order string decoder. Maps strings from the strings.xml
     * file to orderTypes. Maps a value to moveOrderType but this value shouldn't be used, move
     * orders should be written using the bracket format instead.
     *
     * @return A map of strings to order types for decoding order strings
     */
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

    /**
     * Helper function that checks if possible move order strings fit the format defined in the
     * strings.xml file.
     *
     * @param orderText An order chunk that is possibly a move order
     * @return          whether or not the chunk was a valid move order
     */
    private boolean fitsMoveOrderFormat(String orderText) {
        Matcher moveOrderMatcher = this.moveOrderPattern.matcher(orderText);
        return moveOrderMatcher.matches();
    }

}

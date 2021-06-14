package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard builder class for setting up RobotOrder objects. Exists so that specific robot order
 * implementations can remain package-private.
 *
 * @author Remus Courtenay
 * @version 0
 * @since 1.8
 */
public class RobotOrderBuilder {

    private final Map<String, RobotOrderType> stringOrderTypeMap;
    private final Pattern moveOrderPattern;
    private final String orderSplitCharacter;

    public RobotOrderBuilder(Map<String, RobotOrderType> stringOrderTypeMap, String moveOrderPattern, String orderSplitCharacter) {
        this.stringOrderTypeMap = stringOrderTypeMap;
        this.moveOrderPattern = Pattern.compile(moveOrderPattern); // TODO... add error check
        this.orderSplitCharacter = orderSplitCharacter;
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
                if (type == RobotOrderType.SCAN_AR_CODE_ORDER) {
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
            }
        }

        return orders;
    }

    RobotMoveOrder buildMoveOrder() {
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

    private boolean fitsMoveOrderFormat(String orderText) {
        Matcher moveOrderMatcher = this.moveOrderPattern.matcher(orderText);
        return moveOrderMatcher.matches();
    }

}

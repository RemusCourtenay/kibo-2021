package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class QuaternionHelper {


    private static final double KIBO_CAM_FOV = 130;
    private static final double KIBO_CAM_WIDTH_IN_PIXELS = 1280;
    private static final double DEGREES_PER_PIXEL = KIBO_CAM_FOV/KIBO_CAM_WIDTH_IN_PIXELS;

    public static Quaternion translateAdjustment(KiboRpcApi api, double[] adjustmentAmounts) {

        double xDiff = adjustmentAmounts[0];
        double yDiff = adjustmentAmounts[1];

        double totalDiff = Math.sqrt(xDiff*xDiff + yDiff*yDiff); // length of hypotenuse in pixels;



        double numDegreesToRotate = totalDiff * DEGREES_PER_PIXEL;
        if (xDiff < 0) {
            numDegreesToRotate = -numDegreesToRotate; // have to rotate counter clockwise if target is to the left
        }



        // Straight up vector is [x=0,y=1,z=0]
        // i.e. 0 + 1i
        // angle as complex number is xDiff + yDiff(i)
        // so we multiply them together, multiply by i gives xDiff(i) - yDiff
        // so rotation vector is now [x=-yDiff, y=xDiff, z=0]
        // quaternion equation is [w=angle, x=xsin(angle/2), y=ysin(angle/2), z=zsin(angle/2)

        Quaternion rotatedQuaternion = new Quaternion(
                (float)numDegreesToRotate,
                (float)(-yDiff*Math.sin(numDegreesToRotate/2)),
                (float)(xDiff*Math.sin(numDegreesToRotate/2)),
                (float)0);

        // get current Quaternion
        Kinematics currentKinematics = api.getTrustedRobotKinematics();
        Quaternion currentQuaternion = currentKinematics.getOrientation(); // TODO... Check this is not failing

        // get translated Quaternion

        // Components of the first quaternion.
        final double q1a = rotatedQuaternion.getW();
        final double q1b = rotatedQuaternion.getX();
        final double q1c = rotatedQuaternion.getY();
        final double q1d = rotatedQuaternion.getZ();
        // Components of the second quaternion.
        final double q2a = currentQuaternion.getW();
        final double q2b = currentQuaternion.getX();
        final double q2c = currentQuaternion.getY();
        final double q2d = currentQuaternion.getZ();
        // Components of the product.
        final double w = q1a * q2a - q1b * q2b - q1c * q2c - q1d * q2d;
        final double x = q1a * q2b + q1b * q2a + q1c * q2d - q1d * q2c;
        final double y = q1a * q2c - q1b * q2d + q1c * q2a + q1d * q2b;
        final double z = q1a * q2d + q1b * q2c - q1c * q2b + q1d * q2a;

        Quaternion translatedQuaternion = new Quaternion(
                (float)w,
                (float)x,
                (float)y,
                (float)z);

        return translatedQuaternion;
    }

}

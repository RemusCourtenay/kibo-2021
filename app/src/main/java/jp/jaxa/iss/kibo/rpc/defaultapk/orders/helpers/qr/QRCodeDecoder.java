package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.qr;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.RobotOrderException;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotDecodeQRCodeResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class QRCodeDecoder { // TODO... Comments

    // TODO... move to strings.xml and get from context
    private static final String SUCCESSFUL_MESSAGE = ""; // TODO...
    private static final String NULL_MESSAGE = ""; // TODO...
    private static final String INVALID_INPUT_ERROR_MESSAGE= "QR scan results didn't fit format described in strings.xml file";

    private final Pattern qrCodeScanResultPattern;
    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;

    public QRCodeDecoder(Context context) {
        qrCodeScanResultPattern = Pattern.compile(context.getString(R.string.qr_code_scan_result_pattern));
        qrCodeScanResultSplitCharacter = context.getString(R.string.qr_code_scan_result_split_character);
        qrCodeScanResultInnerSplitCharacter = context.getString(R.string.qr_code_scan_result_inner_split_character);
    }

    public RobotOrderResult decodeQRCodeString(String scanResultString) {
        Log.d("Received QR Code, Attempting to decode: ", "Code: " + scanResultString);

        RobotOrderResult result;

        if (!(result = isValidQRCodeOutput(scanResultString)).hasSucceeded()) {
            return result;
        } else {
            return new RobotDecodeQRCodeResult(
                    true,
                    0,
                    SUCCESSFUL_MESSAGE,
                    splitValidResults(scanResultString));
        }
    }

    private RobotOrderResult isValidQRCodeOutput(String scanResultString) {  // TODO... Comment

        if (scanResultString == null) {
            return new RobotDecodeQRCodeResult(
                    false,
                    1,
                    NULL_MESSAGE,
                    null);
        }
        Matcher qrCodeScanResultMatcher = this.qrCodeScanResultPattern.matcher(scanResultString);
        if (qrCodeScanResultMatcher.matches()) {
            return new RobotDecodeQRCodeResult(
                    true,
                    0,
                    "", // no message needed will get caught immediately
                    null
            );
        } else {
            return new RobotDecodeQRCodeResult(
                    false,
                    2,
                    writeErrorMessage(scanResultString),
                    null
            );
        }
    }

    private double[] splitValidResults(String scanResultString) {
        // Removing first { and last }, safe because we know that input fits pattern
        scanResultString = scanResultString.substring(1,scanResultString.length()-1);

        return Arrays.stream(scanResultString.split(this.qrCodeScanResultSplitCharacter))
                .mapToDouble(i ->
                        Double.parseDouble(
                                i.split(this.qrCodeScanResultInnerSplitCharacter)[1]))
                .toArray();
    }

    private String writeErrorMessage(String scanResultString) {
        return INVALID_INPUT_ERROR_MESSAGE + "\n" +
                "Invalid input for reference: " + scanResultString;

    }

}

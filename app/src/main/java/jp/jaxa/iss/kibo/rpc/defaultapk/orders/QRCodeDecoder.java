package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.jaxa.iss.kibo.rpc.defaultapk.R;

class QRCodeDecoder { // TODO... Comments

    private static final String INVALID_INPUT_ERROR_MESSAGE= "QR scan results didn't fit format described in strings.xml file";

    private final Pattern qrCodeScanResultPattern;
    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;

    QRCodeDecoder(Context context) {
        qrCodeScanResultPattern = Pattern.compile(context.getString(R.string.qr_code_scan_result_pattern));
        qrCodeScanResultSplitCharacter = context.getString(R.string.qr_code_scan_result_split_character);
        qrCodeScanResultInnerSplitCharacter = context.getString(R.string.qr_code_scan_result_inner_split_character);
    }

    DecodeResult decodeQRCodeString(String scanResultString) {
        Log.d("Recieved QR Code, Attempting to decode: ", "Code: " + scanResultString);
        DecodeResult decodeResult = new DecodeResult(isValidQRCodeOutput(scanResultString));

        if (decodeResult.wasSuccessful()) {
            decodeResult.setResults(splitValidResults(scanResultString));
        } else {
            decodeResult.setException(new RobotOrderException(writeErrorMessage(scanResultString)));
        }

        return decodeResult;
    }

    private boolean isValidQRCodeOutput(String scanResultString) {  // TODO... Comment
        if (scanResultString == null) {
            return false;
        }
        Matcher qrCodeScanResultMatcher = this.qrCodeScanResultPattern.matcher(scanResultString);
        return qrCodeScanResultMatcher.matches();
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

class DecodeResult {

    private final boolean wasSuccessful;
    private double[] results;
    private RobotOrderException exception;

    DecodeResult(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
    }

    void setResults(double[] results) {
        this.results = results;
    }

    void setException(RobotOrderException exception) {
        this.exception = exception;
    }

    public double[] getResults() {
        return this.results;
    }

    public boolean wasSuccessful() {
        return this.wasSuccessful;
    }

    public RobotOrderException getException() {
        return this.exception;
    }


}

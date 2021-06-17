package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

import static jp.jaxa.iss.kibo.rpc.defaultapk.ImageHelper.getImgBinBitmap;

class RobotScanARCodeOrder extends RobotOrder {

    private final int loopMax;
    private final Pattern qrCodeScanResultPattern;
    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;

    private double[] scanResult;

    RobotScanARCodeOrder(KiboRpcApi api, int loopMax, Pattern qrCodeScanResultPattern, String qrCodeScanResultSplitCharacter, String qrCodeScanResultInnerSplitCharacter) {
        super(api);
        this.loopMax = loopMax;
        this.qrCodeScanResultPattern = qrCodeScanResultPattern;
        this.qrCodeScanResultSplitCharacter = qrCodeScanResultSplitCharacter;
        this.qrCodeScanResultInnerSplitCharacter = qrCodeScanResultInnerSplitCharacter;
    }

    @Override
    protected Result attemptOrderImplementation() {

        String scanResultString = readQR(); // Get result from other teams code here

        if (isValidQRCodeOutput(scanResultString)) {
            api.sendDiscoveredQR(scanResultString); // send the content of QR code for judge
            scanResult = decodeQRCodeString(scanResultString);
        } else {
            throw new RobotOrderException(
                    "Result of QR code scan does not fit the format described in strings.xml\n" +
                            "Result for reference: " + scanResultString);
        }




        return null; // TODO... should return a Result
    }

    @Override
    public String printOrderInfo() {
        return null;
    } // TODO...

    /**
     * Overriding the standard attemptOrder() method to allow for the addition of the scan result
     */
    @Override
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation(), this.scanResult);
    }


    private double[] decodeQRCodeString(String scanResultString) {

        return Arrays.stream(scanResultString.split(this.qrCodeScanResultSplitCharacter)) // TODO... Add to strings.xml
                .mapToDouble(i ->
                        Double.parseDouble(
                                i.split(this.qrCodeScanResultInnerSplitCharacter)[1])) // TODO... Add to strings.xml
                .toArray();

    }

    private boolean isValidQRCodeOutput(String scanResultString) {  // TODO... Comment
        Matcher qrCodeScanResultMatcher = this.qrCodeScanResultPattern.matcher(scanResultString);
        return qrCodeScanResultMatcher.matches();
    }



    /**
     * readQR is used to read a QR code
     * @return String content of the QR code
     */
    private String readQR() { // TODO... Comment
        int count = 0;
        String contents = null;
        api.flashlightControlFront(0f);
        while (contents == null && count < this.loopMax) {
            if (count < 40) { // Magic number
                api.flashlightControlFront((count+1)*0.025f);
            }
            BinaryBitmap bitmap = getImgBinBitmap(api.getMatNavCam(), api.getDockCamIntrinsics()); // Different cameras?
            try {
                com.google.zxing.Result result = new QRCodeReader().decode(bitmap); // Different type of Result?
                contents = result.getText();
            }
            catch (Exception e) { // Catch Exception??
                Log.d("QR[status]:", " Not detected");
            }
            count++;
        }
        api.flashlightControlFront(0f); // Turning flashlight on twice?
        return contents;
    }




}

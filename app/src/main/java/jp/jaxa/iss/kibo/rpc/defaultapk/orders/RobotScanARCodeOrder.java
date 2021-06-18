package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

import static jp.jaxa.iss.kibo.rpc.defaultapk.ImageHelper.getImgBinBitmap;

class RobotScanARCodeOrder extends RobotOrder {

    private final int loopMax;
    private final Pattern qrCodeScanResultPattern;
    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;

    private final QRCodeReader qrCodeReader;

    private double[] scanResult;

    RobotScanARCodeOrder(KiboRpcApi api, int loopMax, Pattern qrCodeScanResultPattern, String qrCodeScanResultSplitCharacter, String qrCodeScanResultInnerSplitCharacter) {
        super(api);
        this.loopMax = loopMax;
        this.qrCodeScanResultPattern = qrCodeScanResultPattern;
        this.qrCodeScanResultSplitCharacter = qrCodeScanResultSplitCharacter;
        this.qrCodeScanResultInnerSplitCharacter = qrCodeScanResultInnerSplitCharacter;
        this.qrCodeReader = new QRCodeReader();
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

        return Arrays.stream(scanResultString.split(this.qrCodeScanResultSplitCharacter))
                .mapToDouble(i ->
                        Double.parseDouble(
                                i.split(this.qrCodeScanResultInnerSplitCharacter)[1]))
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
        String contents = null;

        // Immediately overwritten in while loop?
        api.flashlightControlFront(0f); // TODO... Move to values.xml

        for (int count = 0; count < loopMax; count++) {
            if (count < 40) { // Magic number
                api.flashlightControlFront((count+1)*0.025f);
            }
            BinaryBitmap bitmap = getImgBinBitmap(api.getMatNavCam(), api.getDockCamIntrinsics()); // Different cameras?
            try {
                qrCodeReader.reset();
                com.google.zxing.Result result = qrCodeReader.decode(bitmap);
                if ((contents = result.getText()) != null) {
                    break;
                }
            }
            catch (NotFoundException | FormatException | ChecksumException e) {
                Log.d("QR[status]:", e.getMessage());
            }
        }

        api.flashlightControlFront(0f); // Turning flashlight on twice?
        return contents;
    }




}

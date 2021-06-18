package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

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

import static jp.jaxa.iss.kibo.rpc.defaultapk.ImageHelper.getBinaryBitmapFromMatImage;

class RobotScanARCodeOrder extends RobotOrder { // TODO... Comment

    private final int loopMax;
    private final Pattern qrCodeScanResultPattern;
    private final String qrCodeScanResultSplitCharacter;
    private final String qrCodeScanResultInnerSplitCharacter;
    private final double flashlightOriginalBrightnessForScan;
    private final double flashlightFinalBrightnessForScan;

    private final QRCodeReader qrCodeReader;

    private double[] scanResult;

    RobotScanARCodeOrder(KiboRpcApi api, int loopMax, Pattern qrCodeScanResultPattern, String qrCodeScanResultSplitCharacter, String qrCodeScanResultInnerSplitCharacter, double flashlightOriginalBrightnessForScan, double flashlightFinalBrightnessForScan) {
        super(api);
        this.loopMax = loopMax;
        this.qrCodeScanResultPattern = qrCodeScanResultPattern;
        this.qrCodeScanResultSplitCharacter = qrCodeScanResultSplitCharacter;
        this.qrCodeScanResultInnerSplitCharacter = qrCodeScanResultInnerSplitCharacter;
        this.flashlightOriginalBrightnessForScan = flashlightOriginalBrightnessForScan;
        this.flashlightFinalBrightnessForScan = flashlightFinalBrightnessForScan;

        this.qrCodeReader = new QRCodeReader();
    }

    @Override
    protected Result attemptOrderImplementation() {

        String scanResultString = readQR(); // Get result from other teams code here

        if (isValidQRCodeOutput(scanResultString)) {
            api.sendDiscoveredQR(scanResultString); // send the content of QR code for judge
            scanResult = decodeQRCodeString(scanResultString);
        } else {
            throw new RobotOrderException( // Will probably never happen
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
        String contents;

        for (int count = 0; count < loopMax; count++) {

            // Gradually increasing brightness of flashlight
            api.flashlightControlFront(getCalculatedBrightness(loopMax, count));

            // Getting image from nav cam as bitmap
            BinaryBitmap bitmap = getBinaryBitmapFromMatImage(api.getMatNavCam(), api.getNavCamIntrinsics());

            // If read successful then return
            if ((contents = readQRCodeFromBitmap(bitmap)) != null) {
                api.flashlightControlFront((float)0); // Turning flashlight off?
                return contents;
            }
        }
        api.flashlightControlFront((float)0); // Turning flashlight off?
        throw new RobotOrderException("Unable to retrieve QR code");
    }

    private float getCalculatedBrightness(int loopMax, int count) {
        double maxCount = (double)(loopMax-1);

        // Camera is expected to go from dark to light so here we find the positive difference between the two
        double totalChangeInBrightness = flashlightFinalBrightnessForScan - flashlightOriginalBrightnessForScan;
        // Originally apply none of the change (count = 0), then finally apply all of the change (count = maxCount)
        double percentOfChangeToApply =  count/maxCount;

        // Return a float value equal to the initial brightness plus some percentage of the change
        return (float)(this.flashlightOriginalBrightnessForScan+(totalChangeInBrightness*percentOfChangeToApply));
    }

    private String readQRCodeFromBitmap(BinaryBitmap bitmap) {
        try {
            qrCodeReader.reset();
            return qrCodeReader.decode(bitmap).getText();
        } catch (NotFoundException | FormatException | ChecksumException e) { // TODO... Do something with error messages
            return null;
        }
    }


}

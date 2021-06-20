package jp.jaxa.iss.kibo.rpc.defaultapk.orders;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

class RobotScanQRCodeOrder extends RobotOrder { // TODO... Comment

    private final QRCodeReaderWrapper qrCodeReaderWrapper;
    private final QRCodeDecoder qrCodeDecoder;


    private double[] scanResult;

    RobotScanQRCodeOrder(KiboRpcApi api, QRCodeReaderWrapper qrCodeReaderWrapper, QRCodeDecoder qrCodeDecoder) {
        super(api);

        this.qrCodeReaderWrapper = qrCodeReaderWrapper;
        this.qrCodeDecoder = qrCodeDecoder;
    }

    @Override
    protected Result attemptOrderImplementation() {
        String scanResultString = qrCodeReaderWrapper.readQR(); // Get result from other teams code here
        DecodeResult decodeResult = qrCodeDecoder.decodeQRCodeString(scanResultString);


        if (decodeResult.wasSuccessful()) {
            api.sendDiscoveredQR(scanResultString);
            scanResult = decodeResult.getResults();
        } else {
            throw decodeResult.getException();
        }

        return null; // TODO... should return a Result
    }

    @Override
    public String printOrderInfo() {
        return "Scan QR code order:";
    } // TODO...

    /**
     * Overriding the standard attemptOrder() method to allow for the addition of the scan result
     * Kinda cursed won't lie
     */
    @Override
    public RobotOrderResult attemptOrder() {
        return new RobotOrderResult(attemptOrderImplementation(), this.scanResult);
    }
}

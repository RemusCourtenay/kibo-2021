package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Board;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.GenericRobotOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotARTagReadOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class ARTagReaderWrapper {

    private static final String FOUND_LESS_THAN_FOUR_TAGS_MESSAGE = ""; // TODO...
    private static final String SUCCESS_MESSAGE = ""; // TODO...
    private static final int markerDictionaryID = Aruco.DICT_5X5_250;

    private ARTagCollection arTagCollection;

    public ARTagReaderWrapper(Context context) { // TODO... get static values from xml files via context

    }

    /**
     * Attempts to find the AR tags present in an image and if possible create a Board object from
     * them. Returns a Result value detailing the level of success that occurred.
     * The image argument should be 'cleaned' prior to calling this method as finding tags is a
     * slow process.
     *
     * @param cleanMatImage : An image that has been cropped/scaled to a relevant size
     *
     * @return : RobotOrderResult detailing the success of the operation and possibly containing
     *           bundled objects
     */
    public RobotOrderResult attemptReadImageForARTags(Mat cleanMatImage) {
        RobotOrderResult result;
        Board board;

        // Failed to get tags, returning the result object created by the getTagsFromCleanImage() method
        if (!((result = getTagsFromCleanImage(cleanMatImage)).hasSucceeded())) {
            return result;

        // Found tags but not enough to make a board with, returning the incomplete list of tags and a result detailing the partial success
        } else if (this.arTagCollection.getNumTags() == 4) {
            return new RobotARTagReadOrderResult(true, 1, FOUND_LESS_THAN_FOUR_TAGS_MESSAGE, this.arTagCollection, null);

        // Successfully found both tags and board, returning positive result with bundled tags + board
        } else {
            board = getBoardFromTags(this.arTagCollection);
            return new RobotARTagReadOrderResult(true, 0, SUCCESS_MESSAGE, this.arTagCollection, board);
        }
    }


    /**
     * attempts to use the inbuilt Aruco detectMarkers() function to locate the location of each
     * AR tag in the inputted image and returns them in a more functional ARTag object format.
     *
     * @param cleanMatImage : image which contains one to four AR tags
     * @return : null if the read was successful, otherwise a RobotARTagReadOrderResult with
     *           false succeeded, relevant error message, non-zero return value and null values
     *           for all other bundled info
     */
    private RobotOrderResult getTagsFromCleanImage(Mat cleanMatImage) { // TODO...
        Mat ids = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        List<Mat> corners = new ArrayList<>();
        Aruco.detectMarkers(cleanMatImage, dictionary, corners, ids);
        this.arTagCollection = new ARTagCollection(ids, corners); // set value here
        return new GenericRobotOrderResult(true, 0, ""); // Return a result value dictating success (null) or not success (Result value)
    }

    /**
     * Attempts to use the Aruco Board setup methods to create a board object from the tags.
     * Returns the setup board.
     *
     * @param collection : ARTagCollection
     * @return : null if succeeded, otherwise a relevant Result object
     */

    private Board getBoardFromTags(ARTagCollection collection) {
        Mat ids  = collection.getTagIDsMat();
        List<Mat> corners = collection.getTagCornersMat();
        return Board.create(corners, Aruco.getPredefinedDictionary(markerDictionaryID), ids);
    }
}

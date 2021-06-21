package jp.jaxa.iss.kibo.rpc.defaultapk.orders.helpers.ar;

import android.content.Context;

import org.opencv.aruco.Board;
import org.opencv.core.Mat;

import java.util.List;

import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotARTagReadOrderResult;
import jp.jaxa.iss.kibo.rpc.defaultapk.orders.results.RobotOrderResult;

public class ARTagReaderWrapper {

    private static final String FOUND_LESS_THAN_FOUR_TAGS_MESSAGE = ""; // TODO...
    private static final String SUCCESS_MESSAGE = ""; // TODO...

    private ARTagCollection arTagCollection;
    private Board board;

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

        // Failed to get tags, returning the result object created by the getTagsFromCleanImage() method
        if ((result = getTagsFromCleanImage(cleanMatImage)) != null) {
            return result;

        // Found tags but not enough to make a board with, returning the incomplete list of tags and a result detailing the partial success
        } else if (this.arTagCollection.getNumTags() == 4) {
            return new RobotARTagReadOrderResult(true, 1, FOUND_LESS_THAN_FOUR_TAGS_MESSAGE, this.arTagCollection, null);

        // Failed to create board, returning the result object created by the getBoardFromTags() method
        } else if ((result = getBoardFromTags(this.arTagCollection.getARTags())) != null) {
            return result;

        // Successfully found both tags and board, returning positive result with bundled tags + board
        } else {
            return new RobotARTagReadOrderResult(true, 0, SUCCESS_MESSAGE, this.arTagCollection, this.board);
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
        this.arTagCollection = null; // set value here
        return null; // Return a result value dictating success (null) or not success (Result value)
    }

    /**
     * Attempts to use the Aruco Board setup methods to create a board object from the tags.
     * If this is successful returns null and sets the value of this.board, otherwise returns a
     * Result object detailing what went wrong.
     *
     * @param arTagsList : List of AR tags found in image
     * @return : null if succeeded, otherwise a relevant Result object
     */
    private RobotOrderResult getBoardFromTags(List<ARTag> arTagsList) {
        this.board = null; // set value here
        return null; // Return a result value dictating success (null) or not success (Result value)
    }
}

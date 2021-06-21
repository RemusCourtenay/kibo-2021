package jp.jaxa.iss.kibo.rpc.defaultapk.orders;


import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import jp.jaxa.iss.kibo.rpc.defaultapk.TestResources;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class QRCodeDecoderTest {

    private static final String EXAMPLE_INPUT = "{\"p\":1,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final double[] EXAMPLE_INPUT_RESULTS = new double[]{1,10.23,-8.12,5.45};
    private static final String EXAMPLE_POSITIVE_SIGN_INPUT = "{\"p\":1,\"x\":10.23,\"y\":+8.12,\"z\":5.45}";
    private static final double[] EXAMPLE_POSITIVE_SIGN_RESULTS = new double[]{1, 10.23, 8.12, 5.45};
    private static final String INCORRECT_INPUT = "";
    private static final String NULL_INPUT = null;
    private static final String EMPTY_INPUT = "";
    private static final String INPUT_WITH_MISSING_NUMBER = "{\"p\":,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_INCORRECT_LETTER = "{\"r\":1,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_UPPERCASE_LETTER = "{\"P\":1,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_TRAILING_DECIMAL_POINT = "{\"p\":1,\"x\":10.,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_P_NUMBER_BELOW_VALID_RANGE = "{\"p\":0,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_P_NUMBER_ABOVE_VALID_RANGE = "{\"p\":9,\"x\":10.23,\"y\":-8.12,\"z\":5.45}";
    private static final String INPUT_WITH_MISSING_SECTION = "{\"p\":9,\"x\":10.23,\"y\":-8.12}";
    private static final String INPUT_WITH_NON_DECIMAL_CHARACTER = "{\"p\":1,\"x\":10.23,\"y\":-8.12,\"z\":a}";

    private static final String ACTUAL_INPUT_THAT_WAS_OUTPUTTED = "{\"p\":5,\"x\":11.07,\"y\":-9.80,\"z\":5.40}";
    private static final double[] ACTUAL_INPUT_RESULTS = new double[]{5,11.07,-9.8,5.4};

    @ClassRule
    public static final TestResources testResources = new TestResources();

    private QRCodeDecoder decoder;

    @Before
    public void before() {

        decoder = new QRCodeDecoder(testResources.mockContext);

    }

    @Test
    public void validPatternTest() {

        DecodeResult result = decoder.decodeQRCodeString(EXAMPLE_INPUT);

        if (result.wasSuccessful()) {
            assertArrayEquals("QRCodeDecoder successfully asserted that the input was valid but failed to decode the input correctly\n",
                    EXAMPLE_INPUT_RESULTS,
                    result.getResults(),
                    0);
        } else {
            fail("QRCodeDecoder threw an exception despite being passed a valid input: "  +
                    EXAMPLE_INPUT + "\n" +
                    "Thrown error message: " +
                    result.getException().getMessage());
        }

    }

    @Test
    public void validPatternWithPositiveSignTest() {
        DecodeResult result = decoder.decodeQRCodeString(EXAMPLE_POSITIVE_SIGN_INPUT);

        if (result.wasSuccessful()) {
            assertArrayEquals(
                    "QRCodeDecoder successfully asserted that the input was valid but failed to decode the input correctly\n",
                    EXAMPLE_POSITIVE_SIGN_RESULTS,
                    result.getResults(),
                    0);
        } else {
            fail("QRCodeDecoder threw an exception despite being passed a valid input: "  +
                    EXAMPLE_INPUT + "\n" +
                    "Thrown error message: " +
                    result.getException().getMessage());
        }
    }

    @Test
    public void actualInputTest() {
        DecodeResult result = decoder.decodeQRCodeString(ACTUAL_INPUT_THAT_WAS_OUTPUTTED);

        if (result.wasSuccessful()) {
            assertArrayEquals("QRCodeDecoder successfully asserted that the input was valid but failed to decode the input correctly\n",
                    ACTUAL_INPUT_RESULTS,
                    result.getResults(),
                    0);
        } else {
            fail("QRCodeDecoder threw an exception despite being passed a valid input: "  +
                    ACTUAL_INPUT_THAT_WAS_OUTPUTTED + "\n" +
                    "Thrown error message: " +
                    result.getException().getMessage());
        }
    }


    @Test
    public void incorrectPatternTest() {
        DecodeResult result = decoder.decodeQRCodeString(INCORRECT_INPUT);

        assertFalse("Decoder returned success for invalid input: " + INCORRECT_INPUT,result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: ", result.getException().getMessage());
    }

    @Test
    public void nullPatternTest() {
        DecodeResult result = decoder.decodeQRCodeString(NULL_INPUT);
        assertFalse("Decoder returned success for invalid input: " + NULL_INPUT, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid null input",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: null", result.getException().getMessage());


    }

    @Test
    public void emptyPatternTest() {
        DecodeResult result = decoder.decodeQRCodeString(EMPTY_INPUT);
        assertFalse("Decoder returned success for invalid input: " + EMPTY_INPUT, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid empty input",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: ", result.getException().getMessage());


    }

    @Test
    public void patternWithMissingNumberTest() {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_MISSING_NUMBER);

        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_MISSING_NUMBER, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with missing number",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":,\"x\":10.23,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());
    }

    @Test
    public void patternWithIncorrectLetterTest() {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_INCORRECT_LETTER);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_INCORRECT_LETTER, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with incorrect letter",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"r\":1,\"x\":10.23,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());
    }

    @Test
    public void patternWithUppercaseLetterTest() {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_UPPERCASE_LETTER);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_UPPERCASE_LETTER, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with uppercase letter",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"P\":1,\"x\":10.23,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());

    }

    @Test
    public void patternWithTrailingDecimalPointTest()  {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_TRAILING_DECIMAL_POINT);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_TRAILING_DECIMAL_POINT, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with trailing decimal point",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":1,\"x\":10.,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());


    }

    @Test
    public void patternWithPNumberBelowValidRangeTest()  {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_P_NUMBER_BELOW_VALID_RANGE);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_P_NUMBER_BELOW_VALID_RANGE, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with p number below valid range (1-8)",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":0,\"x\":10.23,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());


    }

    @Test
    public void patternWithPNumberAboveValidRangeTest()  {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_P_NUMBER_ABOVE_VALID_RANGE);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_P_NUMBER_ABOVE_VALID_RANGE, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with p number above valid range (1-8)",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":9,\"x\":10.23,\"y\":-8.12,\"z\":5.45}", result.getException().getMessage());


    }

    @Test
    public void patternWithMissingSectionTest() {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_MISSING_SECTION);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_MISSING_SECTION, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with a missing section",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":9,\"x\":10.23,\"y\":-8.12}", result.getException().getMessage());


    }

    @Test
    public void patternWithNonDecimalCharacterTest() {
        DecodeResult result = decoder.decodeQRCodeString(INPUT_WITH_NON_DECIMAL_CHARACTER);
        assertFalse("Decoder returned success for invalid input: " + INPUT_WITH_NON_DECIMAL_CHARACTER, result.wasSuccessful());
        assertEquals(
                "QRCodeDecoder successfully threw a RobotOrderException but the message differed from what was expected for invalid input with a non-decimal character",
                "QR scan results didn't fit format described in strings.xml file\n" +
                        "Invalid input for reference: {\"p\":1,\"x\":10.23,\"y\":-8.12,\"z\":a}", result.getException().getMessage());


    }





}

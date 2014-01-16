package koda.project.quantization;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class UniformQuantizerTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final int COLS = 8;
    private static final int ROWS = 8;
    private static final int CV_8U = 0;
    private static Mat TEST_DATA = new Mat();
    private static Mat TEST_DATA_QUANTIZED_2_LEVELS = new Mat();
    private static Mat TEST_DATA_QUANTIZED_8_LEVELS = new Mat();

    private static void assertEqualsIntArrays(int[] arr1, int[] arr2) {
        assertEquals(arr1.length, arr2.length);
        for (int i = 0; i < arr1.length; ++i)
            assertEquals(arr1[i], arr2[i]);
    }

    private static void assertEqualsMat(Mat mat1, Mat mat2) {
        assertEquals(mat1.rows(), mat2.rows());
        assertEquals(mat1.cols(), mat2.cols());
        assertEquals(mat1.type(), mat2.type());
        for (int i = 0; i < mat1.rows(); ++i)
            for (int j = 0; j < mat1.cols(); ++j)
                assertEquals(mat1.get(i, j)[0], mat2.get(i, j)[0]);
    }

    @Before
    public void setUp() throws Exception {
        TEST_DATA.create(ROWS, COLS, CV_8U);
        TEST_DATA.put(0, 0, new double[] { //
                56.0, 157.0, 177.0, 157.0, 49.0, 22.0, 15.0, 198.0, //
                        155.0, 17.0, 104.0, 22.0, 244.0, 23.0, 180.0, 116.0, //
                        11.0, 134.0, 55.0, 52.0, 168.0, 182.0, 124.0, 216.0, //
                        106.0, 197.0, 16.0, 220.0, 155.0, 101.0, 157.0, 150.0, //
                        96.0, 253.0, 138.0, 153.0, 8.0, 58.0, 206.0, 144.0, //
                        114.0, 86.0, 23.0, 15.0, 124.0, 195.0, 209.0, 254.0, //
                        36.0, 229.0, 197.0, 181.0, 63.0, 37.0, 212.0, 195.0, //
                        59.0, 121.0, 15.0, 36.0, 239.0, 251.0, 202.0, 197.0 });

        TEST_DATA_QUANTIZED_2_LEVELS.create(TEST_DATA.size(), TEST_DATA.type());
        TEST_DATA_QUANTIZED_2_LEVELS.put(0, 0, new double[] { //
                69.0, 192.0, 192.0, 192.0, 69.0, 69.0, 69.0, 192.0, //
                        192.0, 69.0, 69.0, 69.0, 192.0, 69.0, 192.0, 69.0, //
                        69.0, 192.0, 69.0, 69.0, 192.0, 192.0, 69.0, 192.0, //
                        69.0, 192.0, 69.0, 192.0, 192.0, 69.0, 192.0, 192.0, //
                        69.0, 192.0, 192.0, 192.0, 69.0, 69.0, 192.0, 192.0, //
                        69.0, 69.0, 69.0, 69.0, 69.0, 192.0, 192.0, 192.0, //
                        69.0, 192.0, 192.0, 192.0, 69.0, 69.0, 192.0, 192.0, //
                        69.0, 69.0, 69.0, 69.0, 192.0, 192.0, 192.0, 192.0 });

        TEST_DATA_QUANTIZED_8_LEVELS.create(TEST_DATA.size(), TEST_DATA.type());
        TEST_DATA_QUANTIZED_8_LEVELS.put(0, 0, new double[] { //
                54.0, 146.0, 177.0, 146.0, 54.0, 23.0, 23.0, 177.0, //
                        146.0, 23.0, 115.0, 23.0, 238.0, 23.0, 177.0, 115.0, //
                        23.0, 115.0, 54.0, 54.0, 177.0, 177.0, 115.0, 207.0, //
                        115.0, 177.0, 23.0, 207.0, 146.0, 84.0, 146.0, 146.0, //
                        84.0, 238.0, 146.0, 146.0, 23.0, 54.0, 207.0, 146.0, //
                        115.0, 84.0, 23.0, 23.0, 115.0, 177.0, 207.0, 238.0, //
                        23.0, 207.0, 177.0, 177.0, 54.0, 23.0, 207.0, 177.0, //
                        54.0, 115.0, 23.0, 23.0, 238.0, 238.0, 207.0, 177.0 });
    }

    @Test
    public void testGetOptimalLevels() {
        Quantizer q = new UniformQuantizer();
        q.setQuantizedData(TEST_DATA);

        // dla 2 poziomow
        q.setNumberOfLevels(2);
        try {
            assertEqualsIntArrays(new int[] { 69, 192 }, q.getOptimalLevels());
        } catch (QuantizationException e) {
            fail(e.getMessage());
        }

        // dla 8 poziomow
        q.setNumberOfLevels(8);
        try {
            assertEqualsIntArrays(new int[] { 23, 54, 84, 115, 146, 177, 207,
                    238 }, q.getOptimalLevels());
        } catch (QuantizationException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetQuantizedData() {
        Quantizer q = new UniformQuantizer();
        q.setQuantizedData(TEST_DATA);

        // dla 2 poziomow
        q.setNumberOfLevels(2);
        try {
            assertEqualsMat(TEST_DATA_QUANTIZED_2_LEVELS, q.getQuantizedData());
        } catch (QuantizationException e) {
            fail(e.getMessage());
        }

        // dla 8 poziomow
        q.setNumberOfLevels(8);
        try {
            assertEqualsMat(TEST_DATA_QUANTIZED_8_LEVELS, q.getQuantizedData());
        } catch (QuantizationException e) {
            fail(e.getMessage());
        }
    }
}

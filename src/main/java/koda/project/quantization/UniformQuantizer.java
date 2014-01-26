package koda.project.quantization;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;

public class UniformQuantizer implements Quantizer {

    private Integer m;
    private Mat data;

    public UniformQuantizer() {}

    public UniformQuantizer(int numberOfLevels) {
        this.m = numberOfLevels;
    }

    private void validate() throws QuantizationException {
        if (m == null)
            throw new QuantizationException(
                    "Brak ustawionej liczby poziomów kwantyzacji.");
        if (m < 2)
            throw new QuantizationException(
                    "Wymagane są przynajmniej 2 poziomy kwantyzacji.");
        else if (data == null)
            throw new QuantizationException("Brak danych do kwantyzacji.");
    }

    private void multiplyAndAddIntMat(Mat mat, double multiply, double add) {
        for (int i = 0; i < mat.rows(); ++i)
            for (int j = 0; j < mat.cols(); ++j)
                mat.put(i, j, (int) (mat.get(i, j)[0] * multiply + add));
    }

    @Override
    public void setNumberOfLevels(int m) {
        this.m = m;
    }

    @Override
    public void setQuantizedData(Mat data) {
        this.data = data;
    }

    @Override
    public int[] getOptimalLevels() throws QuantizationException {
        validate();

        // tablica z poziomami
        int[] result = new int[m];

        // obliczam minimum i maksimum wartości danych
        MinMaxLocResult minMax = Core.minMaxLoc(data);
        double min = minMax.minVal;
        double max = minMax.maxVal;

        // odległość między poziomami
        double distance = (max - min) / m;

        // wartość pierwszego poziomu
        double start = distance / 2 + min;

        // wartości kolejnych poziomów
        for (int i = 0; i < m; ++i)
            result[i] = (int) (distance * i + start);

        return result;
    }

    @Override
    public int[] getOptimalBoundaries() throws QuantizationException {
        validate();

        // tablica z granicami
        int[] result = new int[m + 1];

        // obliczam minimum i maksimum wartości danych
        MinMaxLocResult minMax = Core.minMaxLoc(data);
        double min = minMax.minVal;
        double max = minMax.maxVal;

        // długość przedziału
        double distance = (max - min) / m;

        // wartość pierwszej granicy
        double start = min;

        // wartości kolejnych granic
        for (int i = 0; i < result.length; ++i)
            result[i] = (int) (distance * i + start);

        return result;
    }

    @Override
    public Mat getQuantizedData() throws QuantizationException {
        validate();

        // tworzę obiekt wyjściowy
        Mat result = new Mat();
        result.create(data.size(), data.type());

        // obliczam minimum i maksimum wartości danych
        MinMaxLocResult minMax = Core.minMaxLoc(data);
        double min = minMax.minVal;
        double max = minMax.maxVal;

        // odległość między poziomami
        double distance = (max - min) / m;

        // odejmij min, dzielenie modulo distance
        data.convertTo(result, -1, 1.0, -1.0 * min);
        multiplyAndAddIntMat(result, 1 / (distance + 1), 0);

        // mnożenie przez distance i przesunięcie o min + distance / 2
        multiplyAndAddIntMat(result, distance, distance / 2 + min);
        return result;
    }

    @Override
    public Mat quantize(Mat data) throws QuantizationException {
        setQuantizedData(data);
        return getQuantizedData();
    }

}

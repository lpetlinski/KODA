package koda.project.quantization;

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
        else if (data == null)
            throw new QuantizationException("Brak danych do kwantyzacji.");
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
        
        // TODO obliczanie poziomow
        return null;
    }

    @Override
    public Mat getQuantizedData() throws QuantizationException {
        validate();
        
        // TODO kwantyzacja danych o rozkladzie rownomiernym
        return null;
    }

    @Override
    public Mat quantize(Mat data) throws QuantizationException {
        setQuantizedData(data);
        return getQuantizedData();
    }

}

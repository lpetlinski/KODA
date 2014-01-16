package koda.project.quantization;

import org.opencv.core.Mat;

public interface Quantizer {

    /**
     * Ustawia liczbę poziomów kwantyzacji
     */
    public void setNumberOfLevels(int m);

    /**
     * Ustawia dane, które mają zostać poddane kwantyzacji
     * 
     * @param data
     */
    public void setQuantizedData(Mat data);

    /**
     * Zwraca optymalne poziomy kwantyzacji {l_1, l_2, l_3, ... l_m}
     * 
     * @return
     * @throws QuantizationException
     */
    public int[] getOptimalLevels() throws QuantizationException;

    /**
     * Zwraca dane poddane kwantyzacji
     * 
     * @return
     * @throws QuantizationException
     */
    public Mat getQuantizedData() throws QuantizationException;

    /**
     * Kwantyzacja danych
     * 
     * @param data
     * @return
     * @throws QuantizationException
     */
    public Mat quantize(Mat data) throws QuantizationException;
}

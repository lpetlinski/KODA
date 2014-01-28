package koda.project.requantization;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class MeanSquareComputing {
	/**
	 * Computes MeanSquareError for given requantized image relative to original image.
	 * @param originalImageUrl Url of original image to load
	 * @param requantizedImageUrl Url of requantized image to load
	 * @return Mean square error for each r,g,b color component
	 */
	public double[] ComputeMeanSquareError(String originalImageUrl, String requantizedImageUrl) {
		Mat originalImage = Highgui.imread(originalImageUrl,
				Highgui.CV_LOAD_IMAGE_COLOR);
		Mat requantizedImage = Highgui.imread(requantizedImageUrl,
				Highgui.CV_LOAD_IMAGE_COLOR);

		if (originalImage.cols() != requantizedImage.cols()
				|| originalImage.rows() != requantizedImage.rows()) {
			throw new IllegalArgumentException("Images are not same size");
		}

		double[] result = new double[3];
		result[0] = 0;
		result[1] = 0;
		result[2] = 0;

		for (int x = 0; x < originalImage.cols(); x++) {
			for (int y = 0; y < originalImage.rows(); y++) {
				double[] originalPixel = originalImage.get(y, x);
				double[] requantizedPixel = requantizedImage.get(y, x);
				for(int i=0; i<3; i++){
					result[i] += Math.pow(originalPixel[i]-requantizedPixel[i], 2);
				}
			}
		}
		
		for(int i=0; i<3; i++){
			result[i] = result[i] / (originalImage.cols()*originalImage.rows());
		}
		return result;
	}
}

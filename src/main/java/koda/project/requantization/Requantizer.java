package koda.project.requantization;

import java.util.List;
import java.util.Vector;

import koda.project.lloyd.DiscreteProbability;
import koda.project.lloyd.MaxLloydQuantizator;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Requantizer {
	
	/**
	 * Simple requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, by removing two lowest bits.
	 * 
	 * @param originalImageUrl Url of image to load.
	 */
	public void SimpleRequantize(String originalImageUrl){
		this.SimpleRequantize(originalImageUrl, "");
	}
	
	/**
	 * Simple requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, by removing two lowest bits.
	 * Also it saves requantized image in given new image url.
	 * 
	 * @param originalImageUrl Url of image to load.
	 * @param newImageUrl Url of image to save requantized image.
	 */
	public void SimpleRequantize(String originalImageUrl, String newImageUrl){
		Mat image = Highgui.imread(originalImageUrl, Highgui.CV_LOAD_IMAGE_COLOR);
		
		Mat requantizedImage = new Mat(image.rows(), image.cols(), image.type());
		this.SimpleRequantizeColors(image, requantizedImage);
		if(!newImageUrl.isEmpty()){
			Highgui.imwrite(newImageUrl, requantizedImage);
		}
	}
	
	/**
	 * Simple requantization of colors. It removes two lowest bits.
	 * NOTICE: It generally should change representation from 8bpp to 6bpp, but all pixels are rescaled back to 8bpp, to have nice view of image.
	 * 
	 * @param original Original mat of pixels to change representation.
	 * @param newOne Mat of pixels with new representations of pixels.
	 */
	private void SimpleRequantizeColors(Mat original, Mat newOne){
		for(int i=0; i<original.rows(); i++){
			for(int j = 0; j<original.cols(); j++){
				double[] pixel = original.get(i,j);
				double newFirstpixel = (((int)pixel[0]) >> 2 << 2);
				double newSecondpixel = (((int)pixel[1]) >> 2 << 2);
				double newThirdpixel = (((int)pixel[2]) >> 2 << 2);
				newOne.put(i, j, newFirstpixel, newSecondpixel, newThirdpixel);
			}
		}
	}

	/**
	 * Requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, using max-lloyd algorithm to determine optimal quantization levels.
	 * 
	 * @param originalImageUrl Url of image to load.
	 */
	public void Requantize(String originalImageUrl){
		this.Requantize(originalImageUrl, "");
	}
	
	/**
	 * Requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, using max-lloyd algorithm to determine optimal quantization levels.
	 * Also it saves requantized image in given new image url.
	 * 
	 * @param originalImageUrl Url of image to load.
	 * @param newImageUrl Url of image to save requantized image.
	 */
	public void Requantize(String originalImageUrl, String newImageUrl) {
		Mat image = Highgui.imread(originalImageUrl, Highgui.CV_LOAD_IMAGE_COLOR);
		List<Mat> bgrPlates = new Vector<>();
		Core.split(image, bgrPlates);

		double[] blueResult = this.GetOptimalQuantizationLevelsForColor(PlatesColor.Blue,
				bgrPlates);
		double[] greenResult = this.GetOptimalQuantizationLevelsForColor(PlatesColor.Green,
				bgrPlates);
		double[] redResult = this.GetOptimalQuantizationLevelsForColor(PlatesColor.Red,
				bgrPlates);
		
		Mat requantizedImage = new Mat(image.rows(), image.cols(), image.type());
		this.RequantizeColors(image, requantizedImage, blueResult, greenResult, redResult);
		if(!newImageUrl.isEmpty()){
			Highgui.imwrite(newImageUrl, requantizedImage);			
		}
	}
	
	/**
	 * Applies  quantization levels on all planes (rgb) in given image and saves it in new one.
	 * 
	 * @param original Mat of pixels of original image.
	 * @param newOne Mat of pixels with new image.
	 * @param blueQuantizationLevels Optimal quantization levels for blue color.
	 * @param greenQuantizationLevels Optimal quantization levels for green color.
	 * @param redQuantizationLevels Optimal quantization levels for red color.
	 */
	private void RequantizeColors(Mat original, Mat newOne, double[] blueQuantizationLevels, double[] greenQuantizationLevels, double[] redQuantizationLevels){
		for(int i=0; i<original.rows(); i++){
			for(int j = 0; j<original.cols(); j++){
				double[] pixel = original.get(i, j);
				double newBlue = this.GetCorrespondingLevel(pixel[PlatesColor.Blue.GetValue()], blueQuantizationLevels)*4;
				double newGreen = this.GetCorrespondingLevel(pixel[PlatesColor.Green.GetValue()], greenQuantizationLevels)*4;
				double newRed = this.GetCorrespondingLevel(pixel[PlatesColor.Red.GetValue()], redQuantizationLevels)*4;
				newOne.put(i, j, newBlue, newGreen, newRed);
			}
		}
	}
	
	/**
	 * Gets the new level of quantization for given old value.
	 * 
	 * @param value Value to find new quantization level for.
	 * @param levels Array of optimal quantization levels
	 * 
	 * @return optimal quantization level for given value.
	 */
	private int GetCorrespondingLevel(double value, double[] levels){
		int i = 0;
		for(; i<levels.length; i++){
			if(value <= levels[i+1]){
				break;
			}
		}
		return i;
	}
	
	/**
	 * Returns optimal quantization levels for given color from given list of planes.
	 * 
	 * @param color Color to get optimal quantization levels for.
	 * @param images List of rgb plates of image.
	 * 
	 * @return Array of optimal quantization levels for given color.
	 */
	private double[] GetOptimalQuantizationLevelsForColor(PlatesColor color,
			List<Mat> images) {
		double[] histogram = this.GetHistogramForColor(color, images);
		return this.GetOptimalQuantizationLevelsForHistogram(histogram);
	}

	/**
	 * Returns histogram for given color from given list list of rgb plates.
	 * 
	 * @param color Color to get optimal quantization levels for.
	 * @param images List of rgb paltes of image.
	 * 
	 * @return Histogram for given color.
	 */
	private double[] GetHistogramForColor(PlatesColor color, List<Mat> images) {
		List<Mat> tmpImages = new Vector<>();
		tmpImages.add(images.get(color.GetValue()));
		MatOfInt channels = new MatOfInt(0);
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat ranges = new MatOfFloat(0.0f, 255.0f);
		Mat histogram = new Mat();
		Imgproc.calcHist(tmpImages, channels, new Mat(), histogram, histSize,
				ranges);
		return this.GetHistogram(histogram);
	}

	/**
	 * Returns optimal quantization levels for given histogram.
	 * 
	 * @param histogram Histogram to get optimal quantization levels for.
	 * 
	 * @return Optimal quantization levels.
	 */
	private double[] GetOptimalQuantizationLevelsForHistogram(double[] histogram) {
		final double[] tmphist = histogram;
		DiscreteProbability probability = new DiscreteProbability() {

			@Override
			public int GetProbabilityLevels() {
				return 256;
			}

			@Override
			public double GetProbability(int level) {
				return tmphist[level];
			}
		};

		MaxLloydQuantizator quantizator = new MaxLloydQuantizator(probability,
				64, 0.0000000000000001);
		return quantizator.RunQuantization();
	}

	/**
	 * Converts histogram mat to array representation, and scales it to probability. 
	 * 
	 * @param matHistogram One dimensional mat with histogram data.
	 * 
	 * @return Array representation of histogram.
	 */
	private double[] GetHistogram(Mat matHistogram) {
		double[] result = new double[256];
		for (int i = 0; i < 256; i++) {
			result[i] = matHistogram.get(i, 0)[0];
		}
		this.ChangeHistogramToProbability(result);
		return result;
	}

	/**
	 * Scales histogram to probability function.
	 * 
	 * @param histogram Histogram to scale.
	 */
	private void ChangeHistogramToProbability(double[] histogram) {
		double sum = 0;
		for (int i = 0; i < 256; i++) {
			sum += histogram[i];
		}
		for (int i = 0; i < 256; i++) {
			histogram[i] = histogram[i] / sum;
		}
	}
}

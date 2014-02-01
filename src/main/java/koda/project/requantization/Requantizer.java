package koda.project.requantization;

import java.util.List;
import java.util.Vector;

import koda.project.Application;
import koda.project.lloyd.DiscreteProbability;
import koda.project.lloyd.MaxLloydQuantizator;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Requantizer {
    
    private int levels;
    private int bitsToShift;
    
    public Requantizer(int levels){
        this.levels = levels;
        bitsToShift = 8 - (int) (Math.log(levels) / Math.log(2));
    }
    
    public Requantizer(){
        // domyslnie
        this(64);
    }
	
	/**
	 * Simple requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, by removing two lowest bits.
	 * 
	 * @param originalImageUrl Url of image to load.
	 */
	public void simpleRequantize(String originalImageUrl){
		this.simpleRequantize(originalImageUrl, "");
	}
	
	/**
	 * Simple requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, by removing two lowest bits.
	 * Also it saves requantized image in given new image url.
	 * 
	 * @param originalImageUrl Url of image to load.
	 * @param newImageUrl Url of image to save requantized image.
	 */
	public void simpleRequantize(String originalImageUrl, String newImageUrl){
		Mat image = Highgui.imread(originalImageUrl, Highgui.CV_LOAD_IMAGE_COLOR);
		
		Mat requantizedImage = new Mat(image.rows(), image.cols(), image.type());
		this.simpleRequantizeColors(image, requantizedImage);
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
	private void simpleRequantizeColors(Mat original, Mat newOne){
		for(int i=0; i<original.rows(); i++){
			for(int j = 0; j<original.cols(); j++){
				double[] pixel = original.get(i,j);
				double newFirstpixel = (((int)pixel[0]) >> bitsToShift << bitsToShift);
				double newSecondpixel = (((int)pixel[1]) >> bitsToShift << bitsToShift);
				double newThirdpixel = (((int)pixel[2]) >> bitsToShift << bitsToShift);
				newOne.put(i, j, newFirstpixel, newSecondpixel, newThirdpixel);
			}
		}
	}

	/**
	 * Requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, using max-lloyd algorithm to determine optimal quantization levels.
	 * 
	 * @param originalImageUrl Url of image to load.
	 */
	public void requantize(String originalImageUrl){
		this.requantize(originalImageUrl, "");
	}
	
	/**
	 * Requantization of given image. It changes representation from 8 bits per pixel to 6 bpp, using max-lloyd algorithm to determine optimal quantization levels.
	 * Also it saves requantized image in given new image url.
	 * 
	 * @param originalImageUrl Url of image to load.
	 * @param newImageUrl Url of image to save requantized image.
	 */
	public void requantize(String originalImageUrl, String newImageUrl) {
		Mat image = Highgui.imread(originalImageUrl, Highgui.CV_LOAD_IMAGE_COLOR);
		List<Mat> bgrPlates = new Vector<>();
		Core.split(image, bgrPlates);

		double[] blueResult = this.getOptimalQuantizationLevelsForColor(PlatesColor.Blue,
				bgrPlates);
		double[] greenResult = this.getOptimalQuantizationLevelsForColor(PlatesColor.Green,
				bgrPlates);
		double[] redResult = this.getOptimalQuantizationLevelsForColor(PlatesColor.Red,
				bgrPlates);
		
		Mat requantizedImage = new Mat(image.rows(), image.cols(), image.type());
		this.requantizeColors(image, requantizedImage, blueResult, greenResult, redResult);
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
	private void requantizeColors(Mat original, Mat newOne, double[] blueQuantizationLevels, double[] greenQuantizationLevels, double[] redQuantizationLevels){
		double mult = 256./levels;
	    for(int i=0; i<original.rows(); i++){
			for(int j = 0; j<original.cols(); j++){
				double[] pixel = original.get(i, j);
				double newBlue = this.getCorrespondingLevel(pixel[PlatesColor.Blue.getValue()], blueQuantizationLevels)*mult;
				double newGreen = this.getCorrespondingLevel(pixel[PlatesColor.Green.getValue()], greenQuantizationLevels)*mult;
				double newRed = this.getCorrespondingLevel(pixel[PlatesColor.Red.getValue()], redQuantizationLevels)*mult;
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
	public static int getCorrespondingLevel(double value, double[] levels){
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
	private double[] getOptimalQuantizationLevelsForColor(PlatesColor color,
			List<Mat> images) {
		double[] histogram = this.getHistogramForColor(color, images);
		return this.getOptimalQuantizationLevelsForHistogram(histogram);
	}

	/**
	 * Returns histogram for given color from given list list of rgb plates.
	 * 
	 * @param color Color to get optimal quantization levels for.
	 * @param images List of rgb paltes of image.
	 * 
	 * @return Histogram for given color.
	 */
	private double[] getHistogramForColor(PlatesColor color, List<Mat> images) {
		List<Mat> tmpImages = new Vector<>();
		tmpImages.add(images.get(color.getValue()));
		MatOfInt channels = new MatOfInt(0);
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat ranges = new MatOfFloat(0.0f, 255.0f);
		Mat histogram = new Mat();
		Imgproc.calcHist(tmpImages, channels, new Mat(), histogram, histSize,
				ranges);
		return this.getHistogram(histogram);
	}

	/**
	 * Returns optimal quantization levels for given histogram.
	 * 
	 * @param histogram Histogram to get optimal quantization levels for.
	 * 
	 * @return Optimal quantization levels.
	 */
	private double[] getOptimalQuantizationLevelsForHistogram(double[] histogram) {
		final double[] tmphist = histogram;
		DiscreteProbability probability = new DiscreteProbability() {

			@Override
			public int getProbabilityLevels() {
				return 256;
			}

			@Override
			public double getProbability(int level) {
				return tmphist[level];
			}
		};

		MaxLloydQuantizator quantizator = new MaxLloydQuantizator(probability,
		        levels, Application.ERROR_THRESHOLD);
		return quantizator.runQuantization();
	}

	/**
	 * Converts histogram mat to array representation, and scales it to probability. 
	 * 
	 * @param matHistogram One dimensional mat with histogram data.
	 * 
	 * @return Array representation of histogram.
	 */
	private double[] getHistogram(Mat matHistogram) {
		double[] result = new double[256];
		for (int i = 0; i < 256; i++) {
			result[i] = matHistogram.get(i, 0)[0];
		}
		this.changeHistogramToProbability(result);
		return result;
	}

	/**
	 * Scales histogram to probability function.
	 * 
	 * @param histogram Histogram to scale.
	 */
	private void changeHistogramToProbability(double[] histogram) {
		double sum = 0;
		for (int i = 0; i < 256; i++) {
			sum += histogram[i];
		}
		for (int i = 0; i < 256; i++) {
			histogram[i] = histogram[i] / sum;
		}
	}
}

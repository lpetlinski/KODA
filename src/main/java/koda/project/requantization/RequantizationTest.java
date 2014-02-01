package koda.project.requantization;

import org.opencv.core.Core;

public class RequantizationTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {

		Requantizer r = new Requantizer();
		r.requantize("e:\\flag.jpg", "e:\\flag1.jpg");
		r.simpleRequantize("e:\\flag.jpg", "e:\\flag2.jpg");
		
		MeanSquareComputing mse = new MeanSquareComputing();
		
		System.out.println("MSE ALGORITHM");
		double[] result = mse.computeMeanSquareError("e:\\flag.jpg", "e:\\flag1.jpg");
		System.out.println("RED: " + result[PlatesColor.Red.getValue()]);
		System.out.println("GREEN: " + result[PlatesColor.Green.getValue()]);
		System.out.println("BLUE: " + result[PlatesColor.Blue.getValue()]);
		
		System.out.println("MSE SIMPLE");
		result = mse.computeMeanSquareError("e:\\flag.jpg", "e:\\flag2.jpg");
		System.out.println("RED: " + result[PlatesColor.Red.getValue()]);
		System.out.println("GREEN: " + result[PlatesColor.Green.getValue()]);
		System.out.println("BLUE: " + result[PlatesColor.Blue.getValue()]);
		
		System.out.println("Done");
	}

}

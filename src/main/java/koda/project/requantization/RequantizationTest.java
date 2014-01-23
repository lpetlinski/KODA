package koda.project.requantization;

import org.opencv.core.Core;

public class RequantizationTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {

		Requantizer r = new Requantizer();
		r.Requantize("e:\\flag.jpg", "e:\\flag1.jpg");
		r.SimpleRequantize("e:\\flag.jpg", "e:\\flag2.jpg");
		
		MeanSquareComputing mse = new MeanSquareComputing();
		
		System.out.println("MSE ALGORITHM");
		double[] result = mse.ComputeMeanSquareError("e:\\flag.jpg", "e:\\flag1.jpg");
		System.out.println("RED: " + result[PlatesColor.Red.GetValue()]);
		System.out.println("GREEN: " + result[PlatesColor.Green.GetValue()]);
		System.out.println("BLUE: " + result[PlatesColor.Blue.GetValue()]);
		
		System.out.println("MSE SIMPLE");
		result = mse.ComputeMeanSquareError("e:\\flag.jpg", "e:\\flag2.jpg");
		System.out.println("RED: " + result[PlatesColor.Red.GetValue()]);
		System.out.println("GREEN: " + result[PlatesColor.Green.GetValue()]);
		System.out.println("BLUE: " + result[PlatesColor.Blue.GetValue()]);
		
		System.out.println("Done");
	}

}

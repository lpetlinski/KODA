package koda.project.requantization;

import org.opencv.core.Core;

public class RequantizationTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {

		Requantizer r = new Requantizer();
		r.Requantize();
		r.SimpleRequantize();
		System.out.println("Done");
	}

}

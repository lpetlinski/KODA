package koda.project.requantization;

/**
 * Enum for colors of planes in rgb image.
 * 
 * @author £ukasz Petliñski
 *
 */
public enum PlatesColor {
	Blue(0), Red(2), Green(1);

	private int number;

	private PlatesColor(int number) {
		this.number = number;
	}

	public int GetValue() {
		return this.number;
	}
};
package koda.project.lloyd;

/**
 * @author Łukasz
 *
 * Interface for discrete function that should be quantized.
 */
public interface DiscreteProbability {
	/**
	 * Returns levels of discrete function (ie. histogram of gray scale picture has 256 levels).
	 *  
	 * @return Levels of discrete probability function.
	 */
	public int getProbabilityLevels();
	
	/**
	 * Returns probability of given level.
	 * 
	 * @param Level Level to get probability for.
	 * @return Probability of given level.
	 */
	public double getProbability(int level);
}

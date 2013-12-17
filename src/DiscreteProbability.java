/**
 * @author £ukasz
 *
 * Interface for discrete function that should be quantized.
 */
public interface DiscreteProbability {
	/**
	 * Returns levels of discrete function (ie. histogram of gray scale picture has 256 levels).
	 *  
	 * @return Levels of discrete probability function.
	 */
	int GetProbabilityLevels();
	
	/**
	 * Returns probability of given level.
	 * 
	 * @param Level Level to get probability for.
	 * @return Probability of given level.
	 */
	double GetProbability(int level);
}

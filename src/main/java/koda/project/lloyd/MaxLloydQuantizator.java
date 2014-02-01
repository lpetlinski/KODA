package koda.project.lloyd;

/**
 * @author Łukasz
 * 
 *         Class implementing max lloyd algorithm.
 */
public class MaxLloydQuantizator {

    public int intervals;
    public double intervalLevels[];
    public double thresholds[];
    public double meanSquareErrors[];
    public double errorThreshold;
    public DiscreteProbability probability;

    /**
     * Constructor.
     * 
     * @param probability
     *            Class implementing discrete probability interface of function
     *            to quantize.
     * @param intervals
     *            Number of intervals to quantize function into.
     * @param errorThreshold
     *            Minimal error threshold to stop quantization at.
     */
    public MaxLloydQuantizator(DiscreteProbability probability, int intervals,
            double errorThreshold) {
        this.probability = probability;
        this.intervals = intervals;
        this.errorThreshold = errorThreshold;
        this.intervalLevels = new double[intervals];
        this.thresholds = new double[intervals + 1];
        this.meanSquareErrors = new double[intervals];
    }

    /**
     * Runs max lloyd quantization algorithm.
     * 
     * @return Thresholds of optimal quantization.
     */
    public double[] runQuantization() {

        double intervalDiff = probability.getProbabilityLevels()
                / (double) intervals;

        for (int i = 0; i < intervals; i++) {
            intervalLevels[i] = intervalDiff * i + intervalDiff / 2;
        }

        this.thresholds[0] = 0;
        this.thresholds[intervals] = this.probability.getProbabilityLevels() - 1;

        double lastMeanSquareError;
        double actualMeanSquareError = Double.MAX_VALUE;
        do {
            lastMeanSquareError = actualMeanSquareError;
            this.computeThresholds();

            this.computeIntervals();

            actualMeanSquareError = this.computeMeanSquareErrors();

        } while ((lastMeanSquareError - actualMeanSquareError)
                / actualMeanSquareError > errorThreshold);

        return this.thresholds;
    }

    /**
     * Computes mean-square errors for each interval level.
     * 
     * @return Sum of all mean square errors.
     */
    private double computeMeanSquareErrors() {
        double result = 0;
        for (int i = 0; i < this.intervals; i++) {
            double tmp = 0;
            for (int j = (int) this.thresholds[i]; j < (int) this.thresholds[i + 1]; j++) {
                tmp += Math.pow((double) j - this.intervalLevels[i], 2)
                        * this.probability.getProbability(j);
            }
            this.meanSquareErrors[i] = tmp;
            result += tmp;
        }
        return result;
    }

    /**
     * Computes interval levels.
     */
    private void computeIntervals() {
        for (int i = 0; i < this.intervals; i++) {
            double upper = 0;
            double lower = 0;
            for (int j = (int) this.thresholds[i]; j < (int) this.thresholds[i + 1]; j++) {
                upper += j * this.probability.getProbability(j);
                lower += this.probability.getProbability(j);
            }
            this.intervalLevels[i] = upper / lower;
        }
    }

    /**
     * Computes thresholds.
     */
    private void computeThresholds() {
        for (int i = 1; i < this.intervals; i++) {
            this.thresholds[i] = (this.intervalLevels[i] + this.intervalLevels[i - 1]) / 2;
        }
    }
}

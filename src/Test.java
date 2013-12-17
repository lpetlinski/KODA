import java.util.Random;

public class Test {
	public static void main(String[] args) {
		final int levels = 256;
		final double pdf[] = new double[levels];
		Random r = new Random();
		for(int i=0; i<levels; i++){
			pdf[i] = Math.abs(r.nextDouble()/Double.MAX_VALUE);
		}
		
		DiscreteProbability prob = new DiscreteProbability() {
			
			@Override
			public int GetProbabilityLevels() {
				return levels;
			}
			
			@Override
			public double GetProbability(int level) {
				return pdf[level];
			}
		};
		
		MaxLloydQuantizator a = new MaxLloydQuantizator(prob, 8, 0.0001);
		double[] result = a.RunQuantization();
		for(int i=0; i<result.length; i++){
			System.out.println(result[i]);
		}
	}
}

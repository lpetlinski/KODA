package koda.project.function;

import koda.project.Application;
import koda.project.helper.DataSourceHelper;
import koda.project.lloyd.DiscreteProbability;
import koda.project.lloyd.MaxLloydQuantizator;
import koda.project.requantization.Requantizer;
import koda.project.ui.Console;

public class LevelsCalculation {

    private static void printBoundariesAndLevels(double[] boundaries,
            double[] levels, double meanSquareError) {
        if (boundaries.length == 0 || levels.length == 0)
            return;
        java.io.Console out = Application.CONSOLE;
        out.printf("Optymalne granice przedziałów kwantyzacji:"
                + Console.NEW_LINE + "[");
        StringBuilder bounds = new StringBuilder();
        for (double b : boundaries)
            bounds.append((int)b).append(", ");
        out.printf(bounds.substring(0, bounds.length() - 2) + "]"
                + Console.NEW_LINE);

        out.printf("Optymalne poziomy rekonstrukcji danych:" + Console.NEW_LINE);
        StringBuilder lvls = new StringBuilder();
        for (double l : levels)
            lvls.append((int)l).append(", ");
        out.printf(lvls.substring(0, lvls.length() - 2) + Console.NEW_LINE);
        out.printf("Błąd średniokwadratowy: %.2f" + Console.NEW_LINE,
                meanSquareError);
    }

    private static DiscreteProbability createProbability(Double[] data) {
        double max = Double.MIN_VALUE;
        for (Double a : data)
            if (a > max)
                max = a;
        final double[] pdf = new double[(int)max + 1];
        double step = 1. / data.length;
        for (Double a : data)
            pdf[a.intValue()] += step;

        DiscreteProbability probability = new DiscreteProbability() {

            @Override
            public int getProbabilityLevels() {
                return pdf.length;
            }

            @Override
            public double getProbability(int level) {
                return pdf[level];
            }
        };
        return probability;
    }

    private static double computeError(Double[] data, double[] levels) {
        double error = 0.;
        for (Double value : data) {
            double newValue = (int)levels[Requantizer.getCorrespondingLevel(value, levels)];
            error += Math.pow(newValue - value, 2);
        }
        return error / data.length;
    }

    public static void calculate(String dataSource, int numOfLevels)
            throws Exception {
        Double[] data = DataSourceHelper.readFromTextFile(dataSource);
        MaxLloydQuantizator quantizator = new MaxLloydQuantizator(
                createProbability(data), numOfLevels,
                Application.ERROR_THRESHOLD);
        double[] levels = quantizator.runQuantization();
        printBoundariesAndLevels(quantizator.thresholds, levels,
                computeError(data, levels));
    }
}

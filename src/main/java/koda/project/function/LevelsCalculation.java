package koda.project.function;

import org.opencv.core.Mat;

import koda.project.Application;
import koda.project.helper.DataSourceHelper;
import koda.project.quantization.QuantizationException;
import koda.project.quantization.Quantizer;
import koda.project.quantization.UniformQuantizer;
import koda.project.ui.Console;

public class LevelsCalculation {

    private static void printBoundariesAndLevels(int[] boundaries, int[] levels) {
        if (boundaries.length == 0 || levels.length == 0)
            return;
        java.io.Console out = Application.CONSOLE;
        out.printf("Optymalne granice przedziałów kwantyzacji:"
                + Console.NEW_LINE + "[");
        StringBuilder bounds = new StringBuilder();
        for (int b : boundaries)
            bounds.append(b).append(", ");
        out.printf(bounds.substring(0, bounds.length() - 2) + "]"
                + Console.NEW_LINE);

        out.printf("Optymalne poziomy rekonstrukcji danych:" + Console.NEW_LINE);
        StringBuilder lvls = new StringBuilder();
        for (int l : levels)
            lvls.append(l).append(", ");
        out.printf(lvls.substring(0, lvls.length() - 2) + Console.NEW_LINE);
    }

    private static void perform(Quantizer q, Mat data, int numOfLevels)
            throws QuantizationException {
        q.setQuantizedData(data);
        q.setNumberOfLevels(numOfLevels);
        int[] boundaries = q.getOptimalBoundaries();
        int[] levels = q.getOptimalLevels();
        printBoundariesAndLevels(boundaries, levels);
    }

    public static void uniform(String dataSource, int numOfLevels)
            throws Exception {
        perform(new UniformQuantizer(),
                DataSourceHelper.readFromTextFile(dataSource), numOfLevels);
    }

    public static void normal(String dataSource, int numOfLevels) {
        // TODO jak bedzie implementacja to odkomentowac
        /*
         * perform(new NormalQuantizer(),
         * DataSourceHelper.readFromTextFile(dataSource), numOfLevels);
         */
    }

    public static void laplace(String dataSource, int numOfLevels) {
        // TODO jak bedzie implementacja to odkomentowac
        /*
         * perform(new LaplaceQuantizer(),
         * DataSourceHelper.readFromTextFile(dataSource), numOfLevels);
         */
    }
}

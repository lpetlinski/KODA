package koda.project.function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import koda.project.Application;
import koda.project.helper.DataSourceHelper;
import koda.project.lloyd.DiscreteProbability;
import koda.project.lloyd.MaxLloydQuantizator;

public class Requantization {

    private static String createNewName(String filePath, int levels,
            String suffix) {
        File oldFile = new File(filePath);
        String oldName = FilenameUtils.removeExtension(oldFile.getName());
        String extension = FilenameUtils.getExtension(oldFile.getName());
        return FilenameUtils.getFullPath(filePath)
                + DataSourceHelper.RESULTS_DIR_NAME + "/" + oldName + levels
                + suffix + (extension.isEmpty() ? "" : "." + extension);
    }

    private static List<String> getFilesToProcess(String dataSource,
            boolean isDir) {
        List<String> filesToProcess = null;
        if (isDir)
            filesToProcess = DataSourceHelper.getFilesPathsFromDir(dataSource);
        else {
            filesToProcess = new ArrayList<String>();
            filesToProcess.add(dataSource);
            File file = new File(dataSource);
            File resultDir = new File(file.getParentFile(),
                    DataSourceHelper.RESULTS_DIR_NAME);
            if (!resultDir.exists())
                resultDir.mkdir();
        }
        return filesToProcess;
    }

    private static double performLloydForChannels(List<Mat> channels, int levels)
            throws Exception {
        double meanError = 0.;
        for (Mat channel : channels) {
            byte buff[] = new byte[(int) channel.total()];
            // załaduj cały kanał
            channel.get(0, 0, buff);
            double errorSum = 0.;
            // oblicz prawdopodobieństwa wartości
            final double[] pdf = new double[Application.ORIGINAL_LEVELS];
            double probStep = 1. / buff.length;
            for (int i = 0; i < buff.length; ++i) {
                int value = buff[i] & 0xFF;
                if (value >= Application.ORIGINAL_LEVELS)
                    throw new Exception("Błędna wartość: " + value);
                pdf[value] += probStep;
            }
            DiscreteProbability prob = new DiscreteProbability() {
                @Override
                public int GetProbabilityLevels() {
                    return Application.ORIGINAL_LEVELS;
                }

                @Override
                public double GetProbability(int level) {
                    return pdf[level];
                }
            };
            MaxLloydQuantizator q = new MaxLloydQuantizator(prob, levels,
                    Application.ERROR_THRESHOLD);
            double[] thresholds = q.RunQuantization();
            for (int i = 0; i < buff.length; ++i) {
                int oldValue = buff[i] & 0xFF;
                int newValue = 0;
                int j = 0;
                while (oldValue > thresholds[j]) {
                    newValue = (int) q.intervalLevels[j];
                    ++j;
                }
                buff[i] = (byte) newValue;
                errorSum += Math.pow(oldValue - newValue, 2);
            }
            // podmień wartości
            channel.put(0, 0, buff);
            meanError += errorSum / buff.length;
        }
        return meanError;
    }

    public static void performReduction(String dataSource, boolean isDir,
            int levels) throws Exception {
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        int ratio = Application.ORIGINAL_LEVELS / levels;
        for (String filePath : filesPaths) {
            Mat img = DataSourceHelper.readFromImage(filePath);
            if (!img.empty()) {
                byte buff[] = new byte[(int) (img.total() * img.channels())];
                // załaduj cały obrazek
                img.get(0, 0, buff);
                double errorSum = 0.;
                for (int i = 0; i < buff.length; ++i) {
                    int originalValue = buff[i] & 0xFF;
                    int newValue = originalValue / ratio * ratio;
                    buff[i] = (byte) newValue;
                    errorSum += Math.pow(originalValue - newValue, 2);
                }
                // podmień wartości
                img.put(0, 0, buff);
                Application.CONSOLE.printf(
                        "[redukcja] Błąd średniokwadratowy: %.2f (%s)"
                                + koda.project.ui.Console.NEW_LINE, errorSum
                                / buff.length, filePath);
                Highgui.imwrite(createNewName(filePath, levels, "_reduction"),
                        img);
            } else
                throw new Exception(filePath + " nie zawiera danych");
        }
    }

    public static void performLloyd(String dataSource, boolean isDir, int levels)
            throws Exception {
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        for (String filePath : filesPaths) {
            Mat img = DataSourceHelper.readFromImage(filePath);
            if (!img.empty()) {
                List<Mat> channels = new ArrayList<Mat>();
                // podziel wg barw
                Core.split(img, channels);
                double meanError = performLloydForChannels(channels, levels);
                // nadpisz stary obrazek
                Core.merge(channels, img);
                Application.CONSOLE.printf(
                        "[LloydMax] Błąd średniokwadratowy: %.2f (%s)"
                                + koda.project.ui.Console.NEW_LINE, meanError,
                        filePath);
                Highgui.imwrite(createNewName(filePath, levels, "_lloyd"), img);
            } else
                throw new Exception(filePath + " nie zawiera danych");
        }
    }

    public static void performBoth(String dataSource, boolean isDir, int levels)
            throws Exception {
        performReduction(dataSource, isDir, levels);
        performLloyd(dataSource, isDir, levels);
    }

}

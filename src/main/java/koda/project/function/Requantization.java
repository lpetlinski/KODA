package koda.project.function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import koda.project.Application;
import koda.project.helper.DataSourceHelper;
import koda.project.requantization.MeanSquareComputing;
import koda.project.requantization.PlatesColor;
import koda.project.requantization.Requantizer;

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

    public static void performReduction(String dataSource, boolean isDir,
            int levels) throws Exception {
        Requantizer r = new Requantizer(levels);
        MeanSquareComputing mse = new MeanSquareComputing();
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        for (String filePath : filesPaths) {
            String newFileName = createNewName(filePath, levels, "_simple");
            r.SimpleRequantize(filePath, newFileName);
            double[] result = mse.ComputeMeanSquareError(filePath, newFileName);
            double meanError = (result[PlatesColor.Red.GetValue()]
                    + result[PlatesColor.Green.GetValue()] + result[PlatesColor.Blue
                    .GetValue()]) / 3;
            Application.CONSOLE.printf(
                    "[simple] Błąd średniokwadratowy: %.2f (%s)"
                            + koda.project.ui.Console.NEW_LINE, meanError,
                    filePath);
        }
    }

    public static void performLloyd(String dataSource, boolean isDir, int levels)
            throws Exception {
        Requantizer r = new Requantizer(levels);
        MeanSquareComputing mse = new MeanSquareComputing();
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        for (String filePath : filesPaths) {
            String newFileName = createNewName(filePath, levels, "_lloyd");
            r.Requantize(filePath, newFileName);
            double[] result = mse.ComputeMeanSquareError(filePath, newFileName);
            double meanError = (result[PlatesColor.Red.GetValue()]
                    + result[PlatesColor.Green.GetValue()] + result[PlatesColor.Blue
                    .GetValue()]) / 3;
            Application.CONSOLE.printf(
                    "[LloydMax] Błąd średniokwadratowy: %.2f (%s)"
                            + koda.project.ui.Console.NEW_LINE, meanError,
                    filePath);
        }
    }

    public static void performBoth(String dataSource, boolean isDir, int levels)
            throws Exception {
        performReduction(dataSource, isDir, levels);
        performLloyd(dataSource, isDir, levels);
    }

}

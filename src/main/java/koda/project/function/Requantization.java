package koda.project.function;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import koda.project.Application;
import koda.project.helper.DataSourceHelper;

public class Requantization {

    private static String createNewName(String filePath, String suffix) {
        File oldFile = new File(filePath);
        String oldName = FilenameUtils.removeExtension(oldFile.getName());
        String extension = FilenameUtils.getExtension(oldFile.getName());
        return FilenameUtils.getFullPath(filePath) + oldName + suffix
                + (extension.isEmpty() ? "" : "." + extension);
    }

    private static List<String> getFilesToProcess(String dataSource,
            boolean isDir) {
        List<String> filesToProcess = null;
        if (isDir)
            filesToProcess = DataSourceHelper.getFilesPathsFromDir(dataSource);
        else {
            filesToProcess = new ArrayList<String>();
            filesToProcess.add(dataSource);
        }
        return filesToProcess;
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
                Highgui.imwrite(createNewName(filePath, "_reduction"), img);
            } else
                throw new Exception(filePath + " nie zawiera danych");
        }
    }

    public static void performLloyd(String dataSource, boolean isDir, int levels) {
        // TODO rekwantyzacja przez Lloyda
    }

    public static void performBoth(String dataSource, boolean isDir, int levels)
            throws Exception {
        performReduction(dataSource, isDir, levels);
        performLloyd(dataSource, isDir, levels);
    }

}

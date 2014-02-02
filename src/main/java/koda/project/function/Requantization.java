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

/**
 * Klasa do obsługi funkcjonalności związanej z rekwantyzacją obrazów
 * naturalnych
 * 
 * @author Edward Miedziński
 * 
 */
public class Requantization {

    /**
     * Tworzy nazwę nowego pliku do zapisania
     * 
     * @param filePath
     *            ścieżka starego pliku
     * @param levels
     *            liczba poziomów
     * @param suffix
     *            przyrostek w nazwie
     * @return nazwa pliku
     */
    private static String createNewName(String filePath, int levels,
            String suffix) {
        File oldFile = new File(filePath);
        String oldName = FilenameUtils.removeExtension(oldFile.getName());
        String extension = FilenameUtils.getExtension(oldFile.getName());
        return FilenameUtils.getFullPath(filePath)
                + DataSourceHelper.RESULTS_DIR_NAME + "/" + oldName + levels
                + suffix + (extension.isEmpty() ? "" : "." + extension);
    }

    /**
     * Pobiera nazwy plików do przetworzenia
     * 
     * @param dataSource
     *            nazwa pliku lub katalogu
     * @param isDir
     *            czy to katalog
     * @return lista ścieżek do plików
     */
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

    /**
     * Wykonanie rekwantyzacji przez obcięcie najmniej znaczących bitów
     * 
     * @param dataSource
     *            nazwa pliku
     * @param isDir
     *            czy jest katalogiem
     * @param levels
     *            liczba poziomów
     * @throws Exception
     */
    public static void performReduction(String dataSource, boolean isDir,
            int levels) throws Exception {
        Requantizer r = new Requantizer(levels);
        MeanSquareComputing mse = new MeanSquareComputing();
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        for (String filePath : filesPaths) {
            String newFileName = createNewName(filePath, levels, "_simple");
            r.simpleRequantize(filePath, newFileName);
            double[] result = mse.computeMeanSquareError(filePath, newFileName);
            double meanError = (result[PlatesColor.Red.getValue()]
                    + result[PlatesColor.Green.getValue()] + result[PlatesColor.Blue
                    .getValue()]) / 3;
            Application.CONSOLE.printf(
                    "[simple] Błąd średniokwadratowy: %.2f (%s)"
                            + koda.project.ui.Console.NEW_LINE, meanError,
                    filePath);
        }
    }

    /**
     * Wykonanie rekwantyzacji algorytmem Lloyda Maxa
     * 
     * @param dataSource
     *            nazwa pliku
     * @param isDir
     *            czy jest katalogiem
     * @param levels
     *            liczba poziomów
     * @throws Exception
     */
    public static void performLloyd(String dataSource, boolean isDir, int levels)
            throws Exception {
        Requantizer r = new Requantizer(levels);
        MeanSquareComputing mse = new MeanSquareComputing();
        List<String> filesPaths = getFilesToProcess(dataSource, isDir);
        for (String filePath : filesPaths) {
            String newFileName = createNewName(filePath, levels, "_lloyd");
            r.requantize(filePath, newFileName);
            double[] result = mse.computeMeanSquareError(filePath, newFileName);
            double meanError = (result[PlatesColor.Red.getValue()]
                    + result[PlatesColor.Green.getValue()] + result[PlatesColor.Blue
                    .getValue()]) / 3;
            Application.CONSOLE.printf(
                    "[LloydMax] Błąd średniokwadratowy: %.2f (%s)"
                            + koda.project.ui.Console.NEW_LINE, meanError,
                    filePath);
        }
    }

    /**
     * Wykonanie rekwantyzacji na dwa sposoby (obcięcie bitów i algotytm Lloyda
     * Maxa)
     * 
     * @param dataSource
     *            nazwa pliku
     * @param isDir
     *            czy jest katalogiem
     * @param levels
     *            liczba poziomów
     * @throws Exception
     */
    public static void performBoth(String dataSource, boolean isDir, int levels)
            throws Exception {
        performReduction(dataSource, isDir, levels);
        performLloyd(dataSource, isDir, levels);
    }

}

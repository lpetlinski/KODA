package koda.project.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class DataSourceHelper {

    private static String COLS_DELIMITER = " ";
    public static String RESULTS_DIR_NAME = "results";

    public static Double[] readFromTextFile(String filePath) throws Exception {
        List<String> lines = FileUtils.readLines(new File(filePath));
        if (lines.isEmpty())
            throw new Exception("Brak danych");
        Vector<Double> data = new Vector<Double>();
        for (String line : lines) {
            String[] cols = line.split(COLS_DELIMITER);
            for (int j = 0; j < cols.length; ++j)
                data.add(Double.parseDouble(cols[j]));
        }
        return data.toArray(new Double[data.size()]);
    }

    public static List<String> getFilesPathsFromDir(String dirPath) {
        List<String> result = new ArrayList<String>();
        File dir = new File(dirPath);
        for (File file : dir.listFiles())
            if (!file.isDirectory() && file.canRead())
                result.add(file.getAbsolutePath());
        File resultDir = new File(dir, RESULTS_DIR_NAME);
        if(!resultDir.exists())
            resultDir.mkdir();
        return result;
    }

    public static Mat readFromImage(String filePath) {
        return Highgui.imread(filePath);
    }
}

package koda.project.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class DataSourceHelper {

    private static String COLS_DELIMITER = " ";
    private static int OPENCV_DATA_TYPE = 4; // 32-bit int

    public static Mat readFromTextFile(String filePath) throws Exception {
        Mat mat = new Mat();
        List<String> lines = FileUtils.readLines(new File(filePath));
        if (lines.isEmpty())
            throw new Exception("Brak danych");
        int[][] data = new int[lines.size()][];
        int colNum = -1;
        for (int i = 0; i < lines.size(); ++i) {
            String[] cols = lines.get(i).split(COLS_DELIMITER);
            data[i] = new int[cols.length];
            if (colNum > -1 && colNum != cols.length || colNum == 0)
                throw new Exception("Format danych jest nieprawidłowy");
            else
                colNum = cols.length;
            for (int j = 0; j < cols.length; ++j)
                data[i][j] = Integer.parseInt(cols[j]);
        }
        mat.create(data.length, data[0].length, OPENCV_DATA_TYPE);
        for (int i = 0; i < data.length; ++i)
            for (int j = 0; j < data[i].length; ++j)
                mat.put(i, j, data[i][j]);
        return mat;
    }

    public static List<String> getFilesPathsFromDir(String dirPath) {
        List<String> result = new ArrayList<String>();
        File dir = new File(dirPath);
        for (File file : dir.listFiles())
            if (!file.isDirectory() && file.canRead())
                result.add(file.getAbsolutePath());
        return result;
    }

    public static Mat readFromImage(String filePath) {
        return Highgui.imread(filePath);
    }
}

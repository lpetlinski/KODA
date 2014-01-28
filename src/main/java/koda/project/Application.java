package koda.project;

import koda.project.function.LevelsCalculation;
import koda.project.function.Requantization;
import koda.project.ui.Console;
import koda.project.ui.Mode;
import koda.project.ui.Type;

import org.apache.log4j.Logger;
import org.opencv.core.Core;

public class Application {

    public static int ORIGINAL_LEVELS = 256;
    public static double ERROR_THRESHOLD = 0.0001;
    public static java.io.Console CONSOLE;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Console info = Console.read();
        CONSOLE = info.getConsole();
        try {
            if (info.getMode().equals(Mode.levels)) {

                if (info.getType().equals(Type.levelsUniform))
                    LevelsCalculation.uniform(info.getDataSource(),
                            info.getNumberOfLevels());

                else if (info.getType().equals(Type.levelsNormal))
                    LevelsCalculation.normal(info.getDataSource(),
                            info.getNumberOfLevels());

                else if (info.getType().equals(Type.levelsLaplace))
                    LevelsCalculation.laplace(info.getDataSource(),
                            info.getNumberOfLevels());

            } else if (info.getMode().equals(Mode.requantization)) {

                if (info.getType().equals(Type.requantizationReduction))
                    Requantization.performReduction(info.getDataSource(),
                            info.getFromDirectory(), info.getNumberOfLevels());

                else if (info.getType().equals(Type.requantizationLloyd))
                    Requantization.performLloyd(info.getDataSource(),
                            info.getFromDirectory(), info.getNumberOfLevels());

                else if (info.getType().equals(Type.requantizationBoth))
                    Requantization.performBoth(info.getDataSource(),
                            info.getFromDirectory(), info.getNumberOfLevels());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger(Application.class).error(e.getMessage());
        }
    }
}

package koda.project;

import koda.project.function.LevelsCalculation;
import koda.project.function.Requantization;
import koda.project.ui.Console;
import koda.project.ui.Mode;
import koda.project.ui.Type;

import org.opencv.core.Core;

public class Application {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Console info = Console.read();
        if (info.getMode().equals(Mode.levels)) {

            if (info.getType().equals(Type.levelsUniform))
                LevelsCalculation.uniform(info.getDataSource());

            else if (info.getType().equals(Type.levelsNormal))
                LevelsCalculation.normal(info.getDataSource());

            else if (info.getType().equals(Type.levelsLaplace))
                LevelsCalculation.laplace(info.getDataSource());

        } else if (info.getMode().equals(Mode.requantization)) {

            if (info.getType().equals(Type.requantizationReduction))
                Requantization.performReduction(info.getDataSource(),
                        info.getFromDirectory());

            else if (info.getType().equals(Type.requantizationLloyd))
                Requantization.performLloyd(info.getDataSource(),
                        info.getFromDirectory());

            else if (info.getType().equals(Type.requantizationBoth))
                Requantization.performBoth(info.getDataSource(),
                        info.getFromDirectory());
        }
    }
}

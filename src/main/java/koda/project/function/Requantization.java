package koda.project.function;

public class Requantization {

    public static void performReduction(String dataSource, boolean isDir) {
        // TODO rekwantyzacja przez usuniecie bitow
    }

    public static void performLloyd(String dataSource, boolean isDir) {
        // TODO rekwantyzacja przez Lloyda
    }

    public static void performBoth(String dataSource, boolean isDir) {
        performReduction(dataSource, isDir);
        performLloyd(dataSource, isDir);
    }

}

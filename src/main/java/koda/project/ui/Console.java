package koda.project.ui;

import java.io.File;

public class Console {

    public static final String NEW_LINE = System.getProperty("line.separator");
    private static final String QUIT = "0";
    private static java.io.Console CONSOLE;

    private Mode mode;
    private Type type;
    private boolean fromDirectory;
    private String dataSource;
    private int numberOfLevels;

    private Console() {
        CONSOLE = System.console();
        fromDirectory = false;
    }

    private void checkQuit(String text) {
        boolean isQuit = text.contains(QUIT);
        if (isQuit)
            System.exit(0);
    }

    public static Console read() {
        Console console = new Console();
        console.readMode();
        console.readType();
        console.readDataSource();
        if (console.mode.equals(Mode.levels))
            console.readNumberOfLevels();
        return console;
    }

    private void readMode() {
        String command;
        boolean read = false;
        do {
            command = CONSOLE.readLine("Tryb aplikacji:" + NEW_LINE
                    + "1 - wyznaczenie granic kwantyzacji" + NEW_LINE
                    + "2 - rekwantyzacja obrazow" + NEW_LINE + "0 - wyjscie"
                    + NEW_LINE);
            if (command.contains("1")) {
                mode = Mode.levels;
                read = true;
            } else if (command.contains("2")) {
                mode = Mode.requantization;
                read = true;
            }
            checkQuit(command);
        } while (!read);
    }

    private void readType() {
        String command;
        boolean read = false;
        if (mode.equals(Mode.levels))
            do {
                command = CONSOLE.readLine("Rozklad zrodla danych:" + NEW_LINE
                        + "1 - rownomierny" + NEW_LINE + "2 - normalny"
                        + NEW_LINE + "3 - Laplace'a" + NEW_LINE + "0 - wyjscie"
                        + NEW_LINE);
                if (command.contains("1")) {
                    type = Type.levelsUniform;
                    read = true;
                } else if (command.contains("2")) {
                    type = Type.levelsNormal;
                    read = true;
                } else if (command.contains("3")) {
                    type = Type.levelsLaplace;
                    read = true;
                }
                checkQuit(command);
            } while (!read);
        else {
            do {
                command = CONSOLE.readLine("Algorytm:" + NEW_LINE
                        + "1 - Lloyda-Maxa" + NEW_LINE
                        + "2 - usuniecie najmniej znaczacych bitow" + NEW_LINE
                        + "3 - oba powyzsze (porownanie)" + NEW_LINE
                        + "0 - wyjscie" + NEW_LINE);
                if (command.contains("1")) {
                    type = Type.requantizationLloyd;
                    read = true;
                } else if (command.contains("2")) {
                    type = Type.requantizationReduction;
                    read = true;
                } else if (command.contains("3")) {
                    type = Type.requantizationBoth;
                    read = true;
                }
                checkQuit(command);
            } while (!read);
        }
    }

    private void readDataSource() {
        String command;
        boolean read = false;
        do {
            command = CONSOLE.readLine("Sciezka do pliku"
                    + (mode.equals(Mode.requantization) ? " lub katalogu" : "")
                    + " z danymi:" + NEW_LINE);
            File file = new File(command);
            if (file.canRead()) {
                if (mode.equals(Mode.requantization)) {
                    if (file.isDirectory())
                        fromDirectory = true;
                    dataSource = file.getAbsolutePath();
                    read = true;
                } else {
                    if (file.isFile()) {
                        dataSource = file.getAbsolutePath();
                        read = true;
                    } else
                        CONSOLE.printf("%1$s nie jest plikiem" + NEW_LINE,
                                command);
                }
            } else
                CONSOLE.printf("Nie mozna odczytac: %1$s" + NEW_LINE, command);
            checkQuit(command);
        } while (!read);
    }

    private void readNumberOfLevels() {
        String command;
        boolean read = false;
        do {
            command = CONSOLE.readLine("Liczba poziomow rekonstrukcji:"
                    + NEW_LINE);
            if (!command.isEmpty()) {
                numberOfLevels = Integer.parseInt(command);
                read = true;
            }
            checkQuit(command);
        } while (!read);
    }

    public Mode getMode() {
        return mode;
    }

    public Type getType() {
        return type;
    }

    public Boolean getFromDirectory() {
        return fromDirectory;
    }

    public String getDataSource() {
        return dataSource;
    }

    public int getNumberOfLevels() {
        return numberOfLevels;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mode != null ? mode : "null").append(NEW_LINE)
                .append(type != null ? type : "null").append(NEW_LINE)
                .append(fromDirectory ? "directory" : "file").append(NEW_LINE)
                .append(dataSource != null ? dataSource : "null")
                .append(NEW_LINE)
                .append(numberOfLevels != 0 ? numberOfLevels : "null");
        return sb.toString();
    }

    public java.io.Console getConsole() {
        return CONSOLE;
    }
}

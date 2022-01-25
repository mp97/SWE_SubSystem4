package src.common;

public class Constants {
    // Define color constants
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";

    //System Responses
    public static final String SUCCESS = TEXT_GREEN + "ACTION: performed successfully" + TEXT_RESET;
    public static final String FAILURE = TEXT_RED + "ACTION: not performed successfully" + TEXT_RESET;
}

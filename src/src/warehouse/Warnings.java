package src.warehouse;

import static src.common.Constants.*;

public class Warnings {
    private final String color;
    private final String message;

    public Warnings(String color, String message) {
        this.color = color;
        this.message = message;
    }

    @Override
    public String toString() {
        return color + message + TEXT_RESET;
    }
}

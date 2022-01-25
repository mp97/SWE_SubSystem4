package src.shipping.order;
/**
 * A list of available geographic regions to which delivery can take place
 */
public enum Continent {
    EU("Europe"),
    RU("Russia"),
    AS("Asia"),
    AF("Africa"),
    NA("North America"),
    SA("South America"),
    AU("Australia"),
    ;

    private final String name;

    Continent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

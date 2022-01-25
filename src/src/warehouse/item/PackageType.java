package src.warehouse.item;

/**
 * Class PackageType
 * defines the different type of Package that exist
 */
public enum PackageType {
    HOT("Used for products that need to be kept warm."),
    WET("Used for products that are prone to leaking."),
    RADIATON("Used for products that get shipped via wormhole."),
    STANDARD("Standard packaging material with no special properties.");

    private String description;

    PackageType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

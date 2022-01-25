package src.warehouse.item;

/**
 * Class PackageDimensions
 * defines the dimensions of the package
 */
public class PackageDimensions {

    private final int length;
    private final int width;
    private final int height;

    //Constructor
    public PackageDimensions(int length, int width, int height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    //Getters
    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "dimensions: [" + length + ", " + width + ", " + height + ']';
    }
}

package src.warehouse.item;

import java.util.ArrayList;
import java.util.List;

public class Package extends Item {

    private final PackageDimensions dimensions;
    private final List<PackageType> packageTypes;

    //TODO: Implementation of Product
    private Object content;

    public Package(int IID, String name, String description, double weight, double price, PackageDimensions dimensions, PackageType... packageTypes) {
        super(IID, name, description, weight, price);
        this.dimensions = dimensions;
        this.packageTypes = new ArrayList<>();
        if(packageTypes.length == 0){
            this.packageTypes.add(PackageType.STANDARD);
        }else {
            for(PackageType p : packageTypes){
                this.packageTypes.add(p);
            }
        }
    }

    public void pack(Object content){
        this.content = content;
    }

    //getters
    public PackageDimensions getDimensions() {
        return dimensions;
    }

    public List<PackageType> getPackageTypes() {
        return packageTypes;
    }

    @Override
    public String toString() {
        return super.toString() + " with Package " + dimensions +
                ", packageTypes=" + packageTypes +
                ", content=" + content +
                '}';
    }
}

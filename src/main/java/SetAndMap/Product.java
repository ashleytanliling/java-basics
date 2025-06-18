package SetAndMap;

//Product = immutable >>> therefore use record

public record Product(String sku, String name, String mfgr, Category category) {
}

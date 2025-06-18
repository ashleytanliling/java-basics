package SetAndMap;

public class InventoryItem {

    private Product product;
    private double price;

    private int qtyTotal;       //inventory = cart + aisles + warehouse
    private int qtyReserved;    //in carts, but not yet sold
    private int qtyReorder;     //number to order when qtyLow is triggered
    private int qtyLow;         //trigger to order more pdt

    public InventoryItem(Product product, double price, int qtyTotal, int qtyLow) {
        this.product = product;
        this.price = price;
        this.qtyTotal = qtyTotal;
        this.qtyReorder = qtyLow;
        this.qtyLow = qtyLow;
    }

    public Product getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    //add item to cart
    public boolean reserveItem(int qty) {
        //(inventory - qty in others' carts) >= qty requested
        if ((qtyTotal - qtyReserved) >= qty) {
            qtyReserved += qty; //reserve qty
            return true;
        }
        return false;
    }

    //rm item from cart / during cart abandonment
    public void releaseItem(int qty) {
        qtyReserved -= qty;
    }

    //cart checkout
    public boolean sellItem(int qty) {
        if (qtyTotal >= qty) {
            qtyTotal -= qty;
            qtyReserved -= qty;
            if (qtyTotal <= qtyLow) {
                placeInventoryOrder();
            }
            return true;
        }
        return false;
    }

    private void placeInventoryOrder() {
        System.out.printf("Ordering qty %d : %s%n", qtyReorder, product);
    }

    @Override
    public String toString() {
        return "%s, $%.2f : [%04d,% 2d]".formatted(product, price, qtyTotal, qtyReserved);
    }
}

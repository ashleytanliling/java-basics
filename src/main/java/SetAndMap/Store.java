package SetAndMap;

import java.time.LocalDate;
import java.util.*;

public class Store {

    private static Random random = new Random();

    private Map<String, InventoryItem> inventory;

    private NavigableSet<Cart> carts = new TreeSet<>(Comparator.comparing(Cart::getId));

    private Map<Category, Map<String, InventoryItem>> aisleInventory;   //by category -> then pdt name

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        Store myStore = new Store();
        myStore.stockStore();
//        myStore.listInventory();

        myStore.stockAisles();      //ordered by enum Category -> then product name (alphabetical)
//        myStore.listProductsByCategory();

        myStore.manageStoreCarts();
        myStore.listProductsByCategory(false, true); // [checkout -> qtyTotal ↓, qtyReserved ↑]

        myStore.carts.forEach(System.out::println); //checkout -> cart3 removed from carts
        myStore.abandonCarts(); //release pdts fr carts, rm cart fr carts
        myStore.listProductsByCategory(false, true);    //qtyReserved -> qtyTotal
        myStore.carts.forEach(System.out::println);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void stockStore() {
        inventory = new HashMap<>();

        List<Product> products = new ArrayList<>(List.of(
                new Product("A100", "apple", "local", Category.PRODUCE),
                new Product("B100", "banana", "local", Category.PRODUCE),
                new Product("P100", "pear", "local", Category.PRODUCE),
                new Product("L103", "lemon", "local", Category.PRODUCE),
                new Product("M201", "milk", "farm", Category.DAIRY),
                new Product("Y001", "yogurt", "farm", Category.DAIRY),
                new Product("C333", "cheese", "farm", Category.DAIRY),
                new Product("R777", "rice chex", "Nabisco", Category.CEREAL),
                new Product("G111", "granola", "Nat Valley", Category.CEREAL),
                new Product("BB11", "ground beef", "butcher", Category.MEAT),
                new Product("CC11", "chicken", "butcher", Category.MEAT),
                new Product("BC11", "bacon", "butcher", Category.MEAT),
                new Product("BC77", "coke", "coca cola", Category.BEVERAGE),
                new Product("BC88", "coffee", "value", Category.BEVERAGE),
                new Product("BC99", "tea", "herbal", Category.BEVERAGE)
        ));

        products.forEach(p -> inventory.put(p.sku(), new InventoryItem(p, random.nextDouble(0, 1.25), 1000, 5)));
    }

    private void stockAisles() {
        aisleInventory = new EnumMap<>(Category.class); //by Category
        for (InventoryItem item : inventory.values()) {
            Category aisle = item.getProduct().category();

            //check if alr in aisleInventory
            Map<String, InventoryItem> productMap = aisleInventory.get(aisle);
            if (productMap == null) {
                productMap = new TreeMap<>();   //TreeMap alphabetical name sort
            }
            productMap.put(item.getProduct().name(), item);
            aisleInventory.put(aisle, productMap);
        }
    }

    private void listInventory() {
        System.out.println("-".repeat(30));
        inventory.values().forEach(System.out::println);
    }

    private void listProductsByCategory() {
        listProductsByCategory(true, false);
    }

    //overload
    private void listProductsByCategory(boolean includeHeader, boolean includeDetail) {
        aisleInventory.keySet().forEach(k -> {
            if (includeHeader) System.out.println("----------\n" + k + "\n----------"); //Category
            if (!includeDetail) {
                aisleInventory.get(k).keySet().forEach(System.out::println);    //.get(Category), print each pdt name
            } else {
                aisleInventory.get(k).values().forEach(System.out::println);    //.get(Category), print each pdt
            }
        });
    }

    private void manageStoreCarts() {
        //CartType = nested enum = implicitly static
        Cart cart1 = new Cart(Cart.CartType.PHYSICAL, 1);
        carts.add(cart1);

        InventoryItem item = aisleInventory.get(Category.PRODUCE).get("apple");
        cart1.addItem(item, 6);
        cart1.addItem(aisleInventory.get(Category.PRODUCE).get("pear"), 5);
        cart1.addItem(aisleInventory.get(Category.BEVERAGE).get("coffee"), 1);
        System.out.println(cart1);  //Cart{id=1, cartDate=2025-06-15, products={P100=5, BC88=1, A100=6}}

        cart1.removeItem(aisleInventory.get(Category.PRODUCE).get("pear"), 2);
        System.out.println(cart1);  //Cart{id=1, cartDate=2025-06-15, products={P100=3, BC88=1, A100=6}}

        Cart cart2 = new Cart(Cart.CartType.VIRTUAL, 1);
        carts.add(cart2);
        cart2.addItem(inventory.get("L103"), 20);
        cart2.addItem(inventory.get("B100"), 10);
        System.out.println(cart2);  //Cart{id=2, cartDate=2025-06-15, products={B100=10, L103=20}}

        Cart cart3 = new Cart(Cart.CartType.VIRTUAL, 0);
        carts.add(cart3);
        cart3.addItem(inventory.get("R777"), 998);  //qtyTotal=1000, qtyLow=5 >>> order 998, triggered re-order
        System.out.println(cart3);  //Cart{id=3, cartDate=2025-06-16, products={R777=998}}
        if (!checkOutCart(cart3)) {
            System.out.println("Something went wrong, could not check out");
        }

        Cart cart4 = new Cart(Cart.CartType.PHYSICAL, 0);
        carts.add(cart4);
        cart4.addItem(aisleInventory.get(Category.BEVERAGE).get("tea"), 1);
        System.out.println(cart4);  //Cart{id=4, cartDate=2025-06-16, products={BC99=1}}
    }

    private boolean checkOutCart(Cart cart) {
        for (var cartItem : cart.getProducts().entrySet()) {    //sku=qty
            var item = inventory.get(cartItem.getKey());
            int qty = cartItem.getValue();
            if (!item.sellItem(qty)) return false;
        }
        //executed .sellItem() successfully for every item
        cart.printSalesSlip(inventory);
        carts.remove(cart);
        return true;
    }

    //rm carts (ytd & earlier)
    //carts added according to dates (oldest to latest)
    private void abandonCarts() {
        int dayOfYear = LocalDate.now().getDayOfYear();
        Cart lastCart = null;
        for (Cart cart : carts) {
            if (cart.getCartDate().getDayOfYear() == dayOfYear) {   //keep today's carts
                break;
            }
            lastCart = cart;    //lastCart = last cart of prev day
        }

        //to adjust reserved qty in inventory by subtracting abandoned pdt's qty
        var oldCarts = carts.headSet(lastCart, true);
        Cart abandonedCart = null;
        while ((abandonedCart = oldCarts.pollFirst()) != null) {
            for (String sku : abandonedCart.getProducts().keySet()) {
                InventoryItem item = inventory.get(sku);
                item.releaseItem(abandonedCart.getProducts().get(sku));
            }
        }
    }


}

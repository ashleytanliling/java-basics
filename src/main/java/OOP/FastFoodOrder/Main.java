package OOP.FastFoodOrder;

public class Main {
    public static void main(String[] args) {

//        //  (1) test Burger class
//        Burger burger = new Burger("regular", 4.00);
//        burger.addToppings("bacon", "cheese", "mayo");
//        burger.printItem();

//        //  (2) test MealOrder class - with empty constructor
//        MealOrder regularMeal = new MealOrder();
//        regularMeal.addBurgerToppings("Bacon", "Cheese", "tartar");
//        regularMeal.setDrinkSize("large");
//        regularMeal.printItemizedList();

        //  (3) test MealOrder class - constructor with 3 arguments
        MealOrder secondMeal = new MealOrder("turkey", "7-up", "wedges");
        secondMeal.addBurgerToppings("Lettuce", "Cheese", "mayo");
        secondMeal.setDrinkSize("small");
        secondMeal.printItemizedList();

//        //  (4) test DeluxeBurger class
//        //  DeluxeBurger = base price (4.0 -> 8.5), no extra cost (5 toppings, drink, side)
//        MealOrder deluxeMeal = new MealOrder("deluxe", "root beer", "waffle fries");
//        deluxeMeal.addBurgerToppings("AVOCADO", "BACON", "LETTUCE", "CHEESE", "MAYO");
//        deluxeMeal.setDrinkSize("small");
//        deluxeMeal.printItemizedList();
    }

}

package SetAndMapTopics.CourseListTreeMap;

import java.time.LocalDate;
import java.util.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  TreeMap
//  - .compute() .merge() .headMap() .tailMap()
//  - .lastKey()  .lastEntry()    .getValue() .lowerKey() .lowerEntry()
//  - .firstKey() .higherKey() .higherEntry() .pollFirstEntry() .descendingMap() - mods original TreeMap
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Main {

    //  k = courseId_studentId, v = Purchase
    private static Map<String, Purchase> purchases = new LinkedHashMap<>();

    //  k = name, v = Student
    private static NavigableMap<String, Student> students = new TreeMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        Course jmc = new Course("jmc101", "Java Master Class", "Java");
        Course python = new Course("pyt101", "Python Master Class", "Python");

        addPurchase("Mary Martin", jmc, 129.99);
        addPurchase("Andy Martin", jmc, 139.99);
        addPurchase("Mary Martin", python, 149.99);
        addPurchase("Joe Jones", jmc, 149.99);
        addPurchase("Bill Brown", python, 119.99);

        addPurchase("Chuck Cheese", python, 119.99);
        addPurchase("Davey Jones", jmc, 139.99);
        addPurchase("Eva East", python, 139.99);
        addPurchase("Fred Forker", jmc, 139.99);
        addPurchase("Gred Brady", python, 129.99);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println("Purchases - LinkedHashMap (sort by insertion order) ----------");
        //key = courseId_studentId, value = purchase
        purchases.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));  //sort by insertion order
        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println("Students - TreeMap (sort by alphabetical order) ----------");
        //key = name, value = student       //student = [studentId] : courseNames
        students.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));   //sort by alphabetical name
        //note : TreeMap's key = String = has naturalOrder, alr implement Comparable
        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        System.out.println(".compute() - daily sales ----------");
        //to get sales for each day -
        NavigableMap<LocalDate, List<Purchase>> datedPurchases = new TreeMap<>();

        //  .compute()
        for (Purchase p : purchases.values()) {     //loop thro List<Purchase>
            datedPurchases.compute(p.purchaseDate(),    //key = purchaseDate
                    (pdate, plist) -> {     //pdate = purchaseDate as key, plist = curr value
                        //1st purchas for the date, plist == null
                        List<Purchase> list = (plist == null) ? new ArrayList() : plist;
                        list.add(p);
                        return list;
                    });
        }
        //key = pdate, value = List<Purchase>>
        datedPurchases.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v)); //sort by chronological order

        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //  .headMap()  .tailMap()
        int currentYear = LocalDate.now().getYear();

        LocalDate firstDay = LocalDate.ofYearDay(currentYear, 1);   //2025-01-01
        LocalDate week1 = firstDay.plusDays(7);                     //2025-01-08
        Map<LocalDate, List<Purchase>> week1Purchases = datedPurchases.headMap(week1);  //default = excl. 2025-01-08 (1-7)
        Map<LocalDate, List<Purchase>> week2Purchases = datedPurchases.tailMap(week1);  //default = incl. 2025-01-08 (8,9,10...)

        System.out.println(".headMap() ----------");
        week1Purchases.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));
        System.out.println(".tailMap() ----------");
        week2Purchases.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));

        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //  .merge()
        System.out.println(".merge() - weekly sales ----------");
        displayStats(1, week1Purchases);
        System.out.println();
        displayStats(2, week2Purchases);

        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //  .lastKey()  .lastEntry()    .getValue() .lowerKey() .lowerEntry()
        System.out.println("daily sales (latest to oldest) ----------");
        LocalDate lastDate = datedPurchases.lastKey();  //e.g.  2025-01-14
        var previousEntry = datedPurchases.lastEntry();
        //e.g.  2025-01-14=[Purchase[courseId=pyt101, studentId=9, price=129.99, yr=2025, dayOfYear=14]]

        while (previousEntry != null) {
            List<Purchase> lastDaysData = previousEntry.getValue();
            System.out.println(lastDate + " purchases : " + lastDaysData.size());

            //  .floorKey() & .floorEntry() -> infinite loop
            LocalDate prevDate = datedPurchases.lowerKey(lastDate);
            previousEntry = datedPurchases.lowerEntry(lastDate);
            lastDate = prevDate;
        }

        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //  .descendingMap() .firstKey() .higherKey() .higherEntry() .pollFirstEntry()
        System.out.println("daily sales (latest to oldest) ----------");
        var reversed = datedPurchases.descendingMap();
        LocalDate firstDate = reversed.firstKey();
        var nextEntry = reversed.pollFirstEntry();

        while (nextEntry != null) {
            List<Purchase> lastDatsData = nextEntry.getValue();
            System.out.println(firstDate + " purchases : " + lastDatsData.size());

            LocalDate nextDate = reversed.higherKey(firstDate);
            //nextEntry = reversed.higherEntry(firstDate);
            nextEntry = reversed.pollFirstEntry();
            firstDate = nextDate;
        }

        System.out.println("-".repeat(90));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        datedPurchases.forEach((k, v) -> System.out.println(k + ": " + v)); //  .descendingMap() - mods original TreeMap
        System.out.println("End");

    }   //end of main()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void addPurchase(String name, Course course, double price) {
        Student existingStudent = students.get(name);
        if (existingStudent == null) {
            existingStudent = new Student(name, course);
            students.put(name, existingStudent);
        } else {
            existingStudent.addCourse(course);
        }

        int day = new Random().nextInt(1, 15);  //1 to 14   01Jan-14Jan
        String key = course.courseId() + "_" + existingStudent.getId();
        int year = LocalDate.now().getYear();
        Purchase purchase = new Purchase(course.courseId(), existingStudent.getId(), price, year, day);
        purchases.put(key, purchase);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //  .merge()
    private static void displayStats(int period, Map<LocalDate, List<Purchase>> periodData) {

        //  k = courseId, v = n(purchases)
        Map<String, Integer> weeklyCounts = new TreeMap<>();

        //  k = LocalDate, v = List<Purchase>
        periodData.forEach((key, value) -> {
            System.out.println("key = " + key + ", value = " + value);

            for (Purchase p : value) {
                //  1 = initial added, prev = curr key in map, curr = curr value i.e. 1
                weeklyCounts.merge(p.courseId(), 1, (prev, curr) -> {
                    return prev + curr;
                });
            }
        });
        System.out.printf("Week %d Purchases = %s%n", period, weeklyCounts);
    }
}

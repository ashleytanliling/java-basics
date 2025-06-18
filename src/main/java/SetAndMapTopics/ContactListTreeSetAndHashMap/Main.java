package SetAndMapTopics.ContactListTreeSetAndHashMap;

import java.util.*;

//  String  - natural order
//  HashSet - unordered,    LinkedHashSet - insertion order,    TreeSet - Comparable/Comparator
//  HashMap - unordered,    LinkedHashMap - insertion order,    TreeMap - Comparable/Comparator

//  Set - add 1st, no over-write
//  Map - add last, over-writes

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  TreeSet
//  - .ceiling(>=) .higher(>) .floor(<=) .lower(<) .headSet() .tailSet() .subSet()
//  - other methods =    .first() .last() .pollFirst() .pollLast() .descendingSet() - mods original HashSet

//  HashMap
//  - merge, compute, replaceAll
//  - other methods =   .get() .getOrDefault() .put() .putIfAbsent() .clear() .keySet() - mods original HashMap
//                      .containsKey() .remove() .retainAll() .values() .entrySet()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Main {

    public static void main(String[] args) {

        List<Contact> phones = ContactData.getData("phone");    //9 entries, 3 dups
        List<Contact> emails = ContactData.getData("email");    //6 entries, 1 dup

        //  List
        List<Contact> fullList = new ArrayList<>(phones);
        fullList.addAll(emails);
        printSet("Phones-Emails List (no lost data, dups, insertion order)", fullList); //9+6

        //  TreeSet
        //since Contact class did not implement Comparable ---
        Comparator<Contact> sortByName = Comparator.comparing(Contact::getName);    //order by name

        NavigableSet<Contact> fullSet = new TreeSet<>(sortByName);
        fullSet.addAll(phones);
        fullSet.addAll(emails);
        printSet("Phones-Emails TreeSet (lost data, no dups, ordered by name)", fullSet);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  TreeSet
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        setMethods(fullSet);   //.ceiling(>=) .higher(>) .floor(<=) .lower(<) .headSet() .tailSet() .subSet()
        //other methods =    .first() .last() .pollFirst() .pollLast() .descendingSet() - mods original HashSet

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  HashMap
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        mapMethods(fullList);   //merge, compute, replaceAll
        //other methods =   .get() .getOrDefault() .put() .putIfAbsent() .clear() .keySet() - mods original HashMap
        //                  .containsKey() .remove() .retainAll() .values() .entrySet()

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  TreeSet methods - ceiling higher floor lower headSet tailSet subSet
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setMethods(NavigableSet<Contact> fullSet) {

        System.out.println("-".repeat(150));

        //  -   head / floor / lower
        //  Charlie Brown               <<-
        //  Daffy Duck          <-      <<-
        //  Linus Van Pelt
        //  Lucy Van Pelt
        //  Maid Marion
        //  Mickey Mouse
        //  Minnie Mouse
        //  Robin Hood          <-
        //  -   tail / ceiling / higher

        //  Daffy's ceiling = Daffy             |       Daffy's higher = Linus Van Pelt         <-
        //  Daisy's ceiling = Linus Van Pelt     |       Daisy's higher = Linus Van Pelt
        //  Robin's ceiling = Robin              |       Robin's higher = null
        //  Snoopy's ceiling = null              |       Snoopy's higher = null

        //  Daffy's floor = Daffy             |       Daffy's lower = Charlie                   <<-
        //  Daisy's floor = Daffy            |       Daisy's lower = Daffy
        //  Charlie's floor = Charlie          |       Charlie's lower = null
        //  Archie's floor = null              |       Archie's lower = null

        //  .ceiling(>=)  .floor(<=)    - inclusive
        //  .higher(>)   .lower(<)      - exclusive

        Contact daffy = new Contact("Daffy Duck");  //in data
        Contact daisy = new Contact("Daisy Duck");  //not in data
        Contact robin = new Contact("Robin Hood");
        Contact snoopy = new Contact("Snoopy");     //not in data ('S' - inserted at end of TreeSet)
        Contact charlie = new Contact("Charlie Brown");
        Contact archie = new Contact("Archie");     //not in data ('A' - inserted at top of TreeSet)

        System.out.println("-".repeat(90));
        System.out.println(".ceiling() .higher()");
        System.out.println("-".repeat(90));

        for (Contact c : List.of(daffy, daisy, robin, snoopy)) {
            System.out.printf("ceiling(%s)=%s%n", c.getName(), fullSet.ceiling(c));
            System.out.printf("higher(%s)=%s%n", c.getName(), fullSet.higher(c));
        }

        System.out.println("-".repeat(90));
        System.out.println(".floor() .lower()");
        System.out.println("-".repeat(90));

        for (Contact c : List.of(daffy, daisy, charlie, archie)) {
            System.out.printf("floor(%s)=%s%n", c.getName(), fullSet.floor(c));
            System.out.printf("lower(%s)=%s%n", c.getName(), fullSet.lower(c));
        }

        Contact marion = new Contact("Maid Marion");
        var headSet = fullSet.headSet(marion, true);  //default exclude
        printSet(".headSet()", headSet);

        var tailSet = fullSet.tailSet(marion, false);    //default include
        printSet(".tailSet()", tailSet);

        Contact linus = new Contact("Linus Van Pelt");
        //default - include linus, exclude marion
        var subset = fullSet.subSet(linus, false, marion, true);
        printSet(".subSet()", subset);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  HashMap methods - merge compute replaceAll
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void mapMethods(List<Contact> fullList) {

        System.out.println("-".repeat(150));

        Map<String, Contact> contacts = new HashMap<>();

        //  put & merge
        for (Contact c : fullList) {
            Contact duplicate = contacts.put(c.getName(), c);
            if (duplicate != null) {        //key alr exist
                contacts.put(c.getName(), c.mergeContactData(duplicate));
            }
        }
        printMap("put & merge", contacts);

        //  .merge(k,v,BiFn)
        contacts.clear();
        fullList.forEach(c -> contacts.merge(c.getName(), c,
                //(previous, current) -> previous.mergeContactData(current) //from this
                Contact::mergeContactData   //to this
        ));
        printMap(".merge(k,v,BiFn)", contacts);

        //  .computeIfAbsent(k, BiFn(k)) - add 2 new contacts
        for (String contactName : new String[]{"Daisy Duck", "Daffy Duck", "Scrooge McDuck"}) {
            contacts.computeIfAbsent(contactName, k -> new Contact(k));
        }
        printMap(".computeIfAbsent(k, BiFn(k))", contacts);

        System.out.println();

        //  .computeIfPresent(k, BiFn(k,v)) - add 3 emails
        for (String contactName : new String[]{"Daisy Duck", "Daffy Duck", "Scrooge McDuck"}) {
            contacts.computeIfPresent(contactName, (k, v) -> {
                v.addEmail("Fun Place");
                return v;
            });
        }
        printMap(".computeIfPresent(k, BiFn(k,v))", contacts);

        //  .replaceAll(BiFn(k,v)) - mod 2 emails
        contacts.replaceAll((k, v) -> {
            String newEmail = k.replaceAll(" ", "") + "@funplace.com";
            v.replaceEmailIfExists("DDuck@funplace.com", newEmail);     //now Daisy & Daffy diff email add
            return v;
        });
        printMap(".replaceAll(BiFn(k,v))", contacts);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void printSet(String header, Collection<Contact> contacts) {
        System.out.println("-".repeat(90));
        System.out.println(header);
        System.out.println("-".repeat(90));
        contacts.forEach(System.out::println);
    }

    public static void printMap(String header, Map<String, Contact> contacts) {
        System.out.println("-".repeat(90));
        System.out.println(header);
        System.out.println("-".repeat(90));
        contacts.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));
    }
}

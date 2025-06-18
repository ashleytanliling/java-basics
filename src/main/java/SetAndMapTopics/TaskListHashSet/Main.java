package SetAndMapTopics.TaskListHashSet;

import java.util.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  HashSet

//  - Comparable, Comparator
//  - union             .addAll()
//  - difference        .removeAll()
//  - intersect         .retainAll()
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Main {

    public static void main(String[] args) {

        //  Comparable sort
        Set<Task> allTasks = TaskData.getTasks("all");
        sortAndPrint("All Tasks - Comparable sort (project -> description)", allTasks);

        //  Comparator sort
        Comparator<Task> sortByPriority = Comparator.comparing(Task::getPriority); //according to enum : HIGH -> LOW

        Set<Task> annsTasks = TaskData.getTasks("Ann");
        sortAndPrint("Ann's Tasks - Comparator sort (by priority)", annsTasks, sortByPriority);

        //  union - a,b,c
        Set<Task> bobsTasks = TaskData.getTasks("Bob");
        Set<Task> carolsTasks = TaskData.getTasks("Carol");
        List<Set<Task>> sets = List.of(annsTasks, bobsTasks, carolsTasks);

        Set<Task> assignedTasks = getUnion(sets);
        sortAndPrint("Assigned Tasks - Union (ann,bob,carol)", assignedTasks);

        //  union - a,b,c, all
        Set<Task> everyTask = getUnion(List.of(allTasks, assignedTasks));
        sortAndPrint("Every Task (The True ALl Tasks) - Union (ann,bob,carol, allTasks)", everyTask);

        //  difference - (some assignedTasks not in allTasks)
        Set<Task> missingTasks = getDifference(everyTask, allTasks);
        sortAndPrint("Missing Tasks - in assignedTasks, not in allTasks", missingTasks);

        //  difference -
        Set<Task> unassignedTasks = getDifference(allTasks, assignedTasks);
        sortAndPrint("Unassigned Tasks - in allTasks, not in assignedTasks", unassignedTasks, sortByPriority);

        //  union of all overlap
        Set<Task> overlap = getUnion(List.of(
                getIntersect(annsTasks, bobsTasks), //a ∩ b = b ∩ a (no need)
                getIntersect(bobsTasks, carolsTasks),
                getIntersect(annsTasks, carolsTasks)
        ));
        sortAndPrint("Assigned to Multiples (show first assignee)", overlap, sortByPriority);

        //  loop individualTasks & compare each with overlap
        List<Task> overlapping = new ArrayList<>();
        for (Set<Task> set : sets) {
            Set<Task> dups = getIntersect(set, overlap);   //loop: annsTasks vs overlap, bobsTasks vs overlap, carolsTasks vs overlap
            overlapping.addAll(dups);
        }

        Comparator<Task> priorityNatural = sortByPriority.thenComparing(Comparator.naturalOrder()); //priority -> project -> description
        sortAndPrint("Overlapping (show all assignee) priority -> project -> description", overlapping, priorityNatural);
    }

    public static void sortAndPrint(String header, Collection<Task> collection, Comparator<Task> sorter) {
        System.out.println("_".repeat(90));
        System.out.println(header);
        System.out.println("_".repeat(90));

        List<Task> list = new ArrayList<>(collection);
        list.sort(sorter);
        list.forEach(System.out::println);
    }

    //  overload
    public static void sortAndPrint(String header, Collection<Task> collection) {
        sortAndPrint(header, collection, null); //use Comparable's .compareTo()
    }

    //  ∪nion   - .addAll()
    private static Set<Task> getUnion(List<Set<Task>> sets) {
        Set<Task> union = new HashSet<>();
        for (var taskSet : sets) {
            union.addAll(taskSet);
        }
        return union;
    }

    //  a ∩ b = b ∩ a   - .retainAll()
    private static Set<Task> getIntersect(Set<Task> a, Set<Task> b) {
        Set<Task> intersect = new HashSet<>(a);
        intersect.retainAll(b);
        return intersect;
    }

    //  a - b   - .removeAll()
    private static Set<Task> getDifference(Set<Task> a, Set<Task> b) {
        Set<Task> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }

    //  same - (a - b) + (b - a)
    //  same - (a U b) - (a ∩ b)
}

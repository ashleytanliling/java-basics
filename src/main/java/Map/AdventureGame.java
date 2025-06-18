package Map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdventureGame {

    //  adventureMap = site, next/location                  e.g. road, at the end...
    //  location = description, nextPlaces/directions       e.g. at the end..., W:hill...
    //  nextPlaces = direction/compass, destination         e.g. W, hill
    private static final String GAME_SITES = """
            road, at the end of the road, W: hill,E:well house,S:valley,N:forest
            hill, on top of hill with a view in all directions, N:forest, E:road
            well house, inside a well house for a small spring, W:road,N:lake,S:stream
            valley, in a forest valley beside a tumbling stream, N:road,W:hill,E:stream
            forest, at the edge of a thick dark forest, S:road,E:lake
            lake, by an alpine lake surrounded by wildflowers, W:forest,S:well house
            stream, near a stream with a rocky bed, W:valley, N:well house
            """;

    private enum Compass {
        E, N, S, W;

        private static final String[] directions = {"East", "North", "South", "West"};

        //don't want to override toString as I want Compass to print single characters as well
        public String getString() {
            return directions[this.ordinal()];
        }
    }

    private record Location(String description, Map<Compass, String> nextPlaces) {
    }

    private String lastPlace;

    private Map<String, Location> adventureMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //allow players to customize the map
    public AdventureGame(String customSites) {
        loadSites(GAME_SITES);
        if (customSites != null) {
            loadSites(customSites);
        }
    }

    public AdventureGame() {
        this(null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //String GAME_SITES -> String[] parts
    private void loadSites(String data) {
        for (String s : data.split("\\R")) {    //  \\R splits Unicode line-break - all OS (java 8)
            String[] parts = s.split(",", 3);
            Arrays.asList(parts).replaceAll(String::trim);

            Map<Compass, String> nextPlaces = loadDirections(parts[2]);
            Location location = new Location(parts[1], nextPlaces);
            adventureMap.put(parts[0], location);
        }
    }

    //S:stream, W:road, N:lake -> {S=stream,W=road,N=lake}
    private Map<Compass, String> loadDirections(String nextPlaces) {
        Map<Compass, String> directions = new HashMap<>();
        List<String> nextSteps = Arrays.asList(nextPlaces.split(","));
        nextSteps.replaceAll(String::trim);

        for (String nextPlace : nextSteps) {
            String[] splits = nextPlace.split(":");
            Compass compass = Compass.valueOf(splits[0].trim());
            String destination = splits[1].trim();
            directions.put(compass, destination);
        }
        return directions;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //prints curr location & where player can go next
    private void visit(Location location) {
        System.out.printf("*** You are standing %s *** %n", location.description);
        System.out.println("\tFrom here, you can see:");

        location.nextPlaces.forEach((k, v) -> {
            //  e.g. A forest to the North (N)
            System.out.printf("\t● A %s to the %s (%s) %n", v, k.getString(), k);
        });
        System.out.print("Select Your Compass (Q to quit) >> ");
    }

    //move player
    public void move(String direction) {
        var nextPlaces = adventureMap.get(lastPlace).nextPlaces;
        String nextPlace = null;
        if ("ESWN".contains(direction)) {
            nextPlace = nextPlaces.get(Compass.valueOf(direction));
            if (nextPlace != null) {    //null = no available move e.g. N is not in nextPlaces
                play(nextPlace);
            } else {
                System.out.println("!! Invalid direction, try again!!"); //e.g. options=N,E & choice=W
            }
        } else {
            System.out.println("!! Invalid direction, try again!!");    //e.g. ! E/S/W/N
        }
    }

    public void play(String site) {
        if (adventureMap.containsKey(site)) {
            Location next = adventureMap.get(site);
            lastPlace = site;
            visit(next);
        } else {
            System.out.println(site + " is an invalid site");
        }
    }
}

package Map;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

//        AdventureGame game = new AdventureGame();
//        System.out.println("-".repeat(30));
//        game.play("road");

        //customize game sites
        String mySites = """
                lake, at the edge of Lake Tim, E:ocean,W:forest,S:well house, N:cave
                ocean, on Tim's beach before an angry sea, W:lake
                cave, at the mouth of Tim's bat cave, E: ocean, W: forest, S: lake
                """;

        AdventureGame game = new AdventureGame(mySites);
        System.out.println("-".repeat(30));
        game.play("lake");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String direction = scanner.nextLine().trim().toUpperCase().substring(0, 1);
            if (direction.equals("Q")) break;
            game.move(direction);
        }
    }
}

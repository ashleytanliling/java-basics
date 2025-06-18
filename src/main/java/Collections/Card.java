package Collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record Card(Suit suit, String face, int rank) {

    //  nested enum = default static
    public enum Suit {
        CLUB, DIAMOND, HEART, SPADE;

        public char getImage() {
            return (new char[]{9827, 9830, 9829, 9824}[this.ordinal()]);
        }
    }

    public static Comparator<Card> sortRankReversedSuit() {
        return Comparator.comparing(Card::rank).reversed().thenComparing(Card::suit);
    }

    @Override
    public String toString() {

        //for "10", 2 digits to be retrieved
        int index = face.equals("10") ? 2 : 1;
        String faceString = face.substring(0, index);
        return "%s%c(%d)".formatted(faceString, suit.getImage(), rank);
    }

    //cardNumber - 2 to 10
    //rank - 0. cos lowest card = 2, minus 2 to get 0.
    public static Card getNumericCard(Suit suit, int cardNumber) {
        if (cardNumber > 1 && cardNumber < 11) {
            return new Card(suit, String.valueOf(cardNumber), cardNumber - 2);
        }
        System.out.println("Invalid Numeric card selected");
        return null;
    }

    //abbrev = J Q K A
    //J - rank 9
    //Ace - rank highest, 12
    public static Card getFaceCard(Suit suit, char abbrev) {
        int charIndex = "JQKA".indexOf(abbrev); //0/1/2/3
        if (charIndex > -1) {
            return new Card(suit, "" + abbrev, charIndex + 9);  //rank = 0/1/2/3 + 9
        }
        System.out.println("Invalid Face card selected");
        return null;
    }

    public static List<Card> getStandardDeck() {
        List<Card> deck = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (int i = 2; i <= 10; i++) {
                deck.add(getNumericCard(suit, i));  //i = cardNumber (from 2 to 10)
            }
            for (char c : new char[]{'J', 'Q', 'K', 'A'}) {
                deck.add(getFaceCard(suit, c));     //c = J/Q/K/A
            }
        }
        return deck;
    }

    //overload
    public static void printDeck(List<Card> deck) {
        printDeck(deck, "Current Deck", 4);
    }

    public static void printDeck(List<Card> deck, String description, int rows) {
        System.out.println("-".repeat(30));
        if (description != null) {
            System.out.println(description);
        }
        int cardsInRow = deck.size() / rows;
        for (int i = 0; i < rows; i++) {
            int startIndex = i * cardsInRow;    //0->13->26->39
            int endIndex = startIndex + cardsInRow;
            deck.subList(startIndex, endIndex).forEach(c -> System.out.print(c + " "));
            System.out.println();
        }
    }
}

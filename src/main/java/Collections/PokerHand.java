package Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PokerHand {

    private List<Card> hand;
    private List<Card> keepers;
    private List<Card> discards;

    private Ranking score = Ranking.NONE;

    private int playerNo;   //player order = placement from dealer. 1 = immediately next to dealer. dealer is last player.

    public PokerHand(int playerNo, List<Card> hand) {
        hand.sort(Card.sortRankReversedSuit()); //rtns Comparator
        this.hand = hand;
        this.playerNo = playerNo;
        keepers = new ArrayList<>(hand.size());
        discards = new ArrayList<>(hand.size());
    }

    @Override
    public String toString() {
        return "%d. %-16s Rank:%d %-40s Best:%-7s Worst:%-6s %s".formatted(playerNo, score, score.ordinal(), hand,
                Collections.max(hand, Comparator.comparing(Card::rank)),
                Collections.min(hand, Comparator.comparing(Card::rank)),
                (discards.size() > 0) ? "Discards: " + discards : "");
    }

    private void setRank(int faceCount) {
        //freq of face card (J/Q/K/A)
        switch (faceCount) {
            case 4 -> score = Ranking.FOUR_OF_A_KIND;
            case 3 -> {
                if (score == Ranking.NONE) score = Ranking.THREE_OF_A_KIND;
                else score = Ranking.FULL_HOUSE;
            }
            case 2 -> {
                if (score == Ranking.NONE) score = Ranking.ONE_PAIR;
                else if (score == Ranking.THREE_OF_A_KIND) score = Ranking.FULL_HOUSE;
                else score = Ranking.TWO_PAIR;
            }
        }
    }

    public void evalHand() {

        List<String> faceList = new ArrayList<>(hand.size());   //capacity set to n(card on hand)
        hand.forEach(card -> faceList.add(card.face()));        //2-10,J,Q,K,A

        List<String> duplicateFaceCards = new ArrayList<>();
        faceList.forEach(face -> {
            //(yet to be in duplicateFaceCards   &&      >1 in faceList)
            if (!duplicateFaceCards.contains(face) && Collections.frequency(faceList, face) > 1) {
                duplicateFaceCards.add(face);
            }
        });

        for (String duplicate : duplicateFaceCards) {
            int start = faceList.indexOf(duplicate);
            int last = faceList.lastIndexOf(duplicate);
            setRank(last - start + 1);  //count
            List<Card> sub = hand.subList(start, last + 1);
            keepers.addAll(sub);    //cards to keep
        }

        pickDiscards();
    }

    private void pickDiscards() {
        List<Card> temp = new ArrayList<>(hand);
        temp.removeAll(keepers);

        int rankedCards = keepers.size();   //3 or more cards on hand
        Collections.reverse(temp);          //order low-rank cards first
        int index = 0;                      //looping index - discard pile 0/1/2 cards, i.e. max 3 cards
        for (Card c : temp) {
            //c.rank() - to keep high-rank cards (9 & above, i.e. J/Q/K/A)
            if (index++ < 3 && (rankedCards > 2 || c.rank() < 9)) discards.add(c);
            else keepers.add(c);
        }
    }
}

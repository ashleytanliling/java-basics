package Collections;

public enum Ranking {
    //exclude - straight, straight flush, royal flush & five of a kind
    //note : THREE_OF_A_KIND + TWO_OF_A_KIND (in 1 hand) = FULL_HOUSE
    NONE, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND;

    @Override
    public String toString() {
        return this.name().replace('_', ' ');
    }
}

package Set;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class Theatre {

    //nested class
    class Seat implements Comparable<Seat> {
        private String seatNum;
        private boolean reserved;

        public Seat(char rowChar, int seatNo) {
            this.seatNum = "%c%03d".formatted(rowChar, seatNo).toUpperCase();
        }

        @Override
        public String toString() {
            return seatNum;
        }

        @Override
        public int compareTo(Seat o) {
            return seatNum.compareTo(o.seatNum);
        }
    }

    private String theatreName;
    private int seatsPerRow;
    private NavigableSet<Seat> seats;

    public Theatre(String theatreName, int rows, int totalSeats) {
        this.theatreName = theatreName;
        this.seatsPerRow = totalSeats / rows;

        seats = new TreeSet<>();
        for (int i = 0; i < totalSeats; i++) {
            char rowChar = (char) (i / seatsPerRow + (int) 'A');    //first row = 'A'
            int seatInRow = i % seatsPerRow + 1;    //+1 to rm seat# 0  //0 % 10 = 0, 1 % 10 = 1
            seats.add(new Seat(rowChar, seatInRow));
        }
    }

    public void printSeatMap() {
        String separatorLine = "-".repeat(90);
        System.out.printf("%1$s%n%2$s Seat Map%n%1$s%n", separatorLine, theatreName);   //prints - line name line. (line = %1$s)

        int index = 0;
        for (Seat s : seats) {
            System.out.printf("%-8s%s",
                    s.seatNum + ((s.reserved) ? "(\u25CF)" : ""),
                    ((index++ + 1) % seatsPerRow == 0) ? "\n" : "");    //line break at end of seats row
        }
        System.out.println(separatorLine);
    }

    public String reserveSeat(char row, int seat) {

        Seat requestedSeat = new Seat(row, seat);
        Seat requested = seats.floor(requestedSeat);    //no .get() for Sets

        //if mo elt <= passedElt, .floor() rtns null
        //if (null is returned || rtn seat# diff from requested seat#)
        if (requested == null || !requested.seatNum.equals(requestedSeat.seatNum)) {
            System.out.print("--> No such seat: " + requestedSeat);
            System.out.printf(": Seat must be between %s and %s%n", seats.first().seatNum, seats.last().seatNum);
        } else {
            if (!requested.reserved) {
                requested.reserved = true;  //reserve the seat
                return requested.seatNum;
            } else {
                System.out.println("Seat's already resevered.");
            }
        }
        return null;
    }

    //min>0 - cos seat# starts from 1
    //can't bk >1row
    //max - min + 1 more than number of seats they want to bk
    //&& first row picked is really in my available seats
    private boolean validate(int count, char first, char last, int min, int max) {
        boolean result = (min > 0 || seatsPerRow >= count || (max - min + 1) >= count);
        result = result && seats.contains(new Seat(first, min));
        if (!result) {
            System.out.printf("Invalid! %1$d seats between " + "%2$c[%3$d-%4$d]-%5$c[%3$d-%4$d] Try again", count, first, min, max, last);
            System.out.printf(": Seat must be between %s and %s%n", seats.first().seatNum, seats.last().seatNum);
        }
        return result;
    }

    //  reserve contiguous seats, within 1 row
    public Set<Seat> reserveSeats(int count, char minRow, char maxRow, int minSeat, int maxSeat) {

        char lastValid = seats.last().seatNum.charAt(0);
        maxRow = (maxRow < lastValid) ? maxRow : lastValid;

        if (!validate(count, minRow, maxRow, minSeat, maxSeat)) {
            return null;
        }

        NavigableSet<Seat> selected = null;

        for (char letter = minRow; letter <= maxRow; letter++) {
            NavigableSet<Seat> contiguous = seats.subSet(
                    new Seat(letter, minSeat), true,
                    new Seat(letter, maxSeat), true
            );

            int index = 0;
            Seat first = null;
            for (Seat current : contiguous) {
                if (current.reserved) { //if seat is reserved
                    index = 0;  //reset index to 0
                    continue;   //go back up & check next set in this subset
                }
                //unreserved seat found!
                first = (index == 0) ? current : first; //(index == 0) i.e. first unreserved seat found
                if (++index == count) { //==count i.e. found enough seats required
                    selected = contiguous.subSet(first, true, current, true);
                    break;  //break out from Seat for-loop
                }
            }
            if (selected != null) {
                break;  //breaks out from char for-loop
            }
        }

        Set<Seat> reservedSeats = null;
        if (selected != null) {
            selected.forEach(s -> s.reserved = true);
            reservedSeats = new TreeSet<>(selected);
        }
        return reservedSeats;
    }
}

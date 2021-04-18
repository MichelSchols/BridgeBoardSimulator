/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peeters.frank.bridge.deal;

import peeters.frank.bridge.filter.HandFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author frankpeeters
 */
public class SampleOfBoards {

    private final List<Board> boards;

    public SampleOfBoards() {
        this.boards = new ArrayList<Board>();
    }

    public SampleOfBoards(List<Board> boards) {
        this.boards = boards;
    }

    public int countHands(HandFilter filter, Direction direction) {
        int count = 0;
        for (Board board : boards) {
            if (filter.accepts(board.hand(direction))) {
                count++;
            }
        }
        return count;
    }

    public int getSampleSize() {
        return boards.size();
    }

    public Propabilities estimatedPropability() {
        if (getSampleSize() == 0) {
            return null;
        }
        double sumProbes = 0.0;
        double sumProbesValidNorth = 0.0;
        for (Board board : boards) {
            sumProbes += 1 + board.getInvalidProbes();
            sumProbesValidNorth += 1 + board.getUnacceptedProbesValidNorth();
        }
        Propabilities p = new Propabilities();
        p.total = getSampleSize() / sumProbes;
        p.south = sumProbesValidNorth / sumProbes;
        return p;
    }

    public double propabilityFilter(HandFilter filter, Direction direction) {
        if (getSampleSize() == 0) {
            return -1;
        }
        double count = 0.0;
        for (Board board : boards) {
            if (filter.accepts(board.hand(direction))) {
                count++;
            }
        }

        return count / getSampleSize();
    }

    public List<Board> getBoards() {
        return boards;
    }

    public Board getBoard(int nr) {
        if (nr < 1 || nr > boards.size()) {
            return null;
        }
        return boards.get(nr - 1);
    }

    public int[] freqTableOfControls(Direction direction) {
        int[] distr = new int[13];
        for (Board board : boards) {
            distr[board.hand(direction).controls()]++;
        }
        return distr;
    }

    public int[] freqTableOfLosers(Direction direction) {
        int[] distr = new int[25];
        for (Board board : boards) {
            int losers2 = (int) (board.hand(direction).losers() * 2);
            distr[losers2]++;
        }
        return distr;
    }

    @Override
    public String toString() {
        String STROKE = "--------------------"
            + "--------------------" + "--------------------";

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= boards.size(); i++) {
            Board b = boards.get(i - 1);
            //       if (!b.getComment().isEmpty())
            {
                int length;
                String istring = i + "";
                length = 58 - istring.length();
                sb.append(i).append(".").append(STROKE.substring(0, length)).append("\n");
                sb.append(b.toString());
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    public String stringBareView() {
        StringBuilder sb = new StringBuilder();
        int[] order = {0, 3, 1, 2};
        for (int nr = 1; nr <= boards.size(); nr++) {
            Board board = boards.get(nr - 1);
            for (int i = 0; i < 4; i++) {
                Direction direction = Direction.values()[order[i]];
                sb.append(board.hand(direction).toString());
                sb.append("; ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}

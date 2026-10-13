package com.oxipro.ranking.glicko2;

public class MatchResult {

    public final double opponentRating;
    public final double opponentRd;
    public final double score;

    /**
     * A Match result that combines the opponent settings needed to compute the rating of the player.
     * @param opponentRating The rating of the opponent.
     * @param opponentRd The rating deviation of the opponent.
     * @param score The score of the match. Between 0 and 1. (can be 0.5 for a draw)
     */
    public MatchResult(double opponentRating, double opponentRd, double score) {
        this.opponentRating = opponentRating;
        this.opponentRd = opponentRd;
        this.score = score;
    }

}

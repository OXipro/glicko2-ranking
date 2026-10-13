package com.oxipro.ranking.glicko2;

import java.util.List;

public class Glicko2Engine {

    public final double TAU;
    public final double SCALE;
    public final double PERIOD;


    /**
     * Initialize the settings of the glicko2 Rating Engine
     * @param TAU The change of Volatility over time (τ)
     * @param SCALE The scale of the rating system, converts glicko2 format to yours.
     * @param PERIOD The time in ms of a period.
     */
    public Glicko2Engine(double TAU, double SCALE, double PERIOD) {
        this.TAU = TAU;
        this.SCALE = SCALE;
        this.PERIOD = PERIOD;
    }

    /**
     * Initialize the settings of the glicko2 Rating Engine with default values.
     */
    public Glicko2Engine() {
        this.TAU = 0.5;
        this.SCALE = 173.7178;
        this.PERIOD = 4 * 24 * 60 * 60 * 1000L;
    }

    /**
     * Updates the player's RD in function of elapsed time since the latest ranking.
     */
    public void age(PlayerRating playerRating, long now) {
    }

    /**
     * Updates the Player Rating in function of his played matches.
     * @param playerRating The player
     * @param resultList the results
     */
    public void update(PlayerRating playerRating, List<MatchResult> resultList) {
    }


}

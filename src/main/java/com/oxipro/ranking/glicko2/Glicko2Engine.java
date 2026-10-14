package com.oxipro.ranking.glicko2;

import java.util.List;

public class Glicko2Engine {

    public final double TAU;
    public final double EPSILON;
    public final double SCALE;
    public final double PERIOD;
    public final Glicko2Math math;


    /**
     * Initialize the settings of the glicko2 Rating Engine
     * @param TAU The change of Volatility over time (τ)
     * @param EPSILON Convergence tolerance for the volatility iteration
     * @param SCALE The scale of the rating system, converts glicko2 format to yours.
     * @param PERIOD The time in ms of a period.
     */
    public Glicko2Engine(double TAU, double EPSILON, double SCALE, double PERIOD) {
        this.TAU = TAU;
        this.EPSILON = EPSILON;
        this.SCALE = SCALE;
        this.PERIOD = PERIOD;
        this.math = new Glicko2Math(TAU, EPSILON, SCALE);
    }

    public Glicko2Engine(double TAU, double SCALE, double PERIOD) {
        this(TAU, 0.000001, SCALE, PERIOD);
    }

    /**
     * Initialize the settings of the glicko2 Rating Engine with default values.
     */
    public Glicko2Engine() {
        this(0.5, 0.000001, 173.7178, 4 * 24 * 60 * 60 * 1000L);
    }

    /**
     * Updates the player's RD in function of elapsed time since the latest ranking.
     */
    public PlayerRating age(PlayerRating playerRating, long now) {
        long deltaTime = now - playerRating.getLastUpdate();
        double periodElapsed = (double) deltaTime / this.PERIOD;

        double phi = math.convertDeviation(playerRating.rd);

        double phiAged = Math.sqrt(Math.pow(phi, 2) + Math.pow(playerRating.volatility, 2) * periodElapsed);
        playerRating.rd = this.SCALE * phiAged;
        return playerRating;
    }

    /**
     * Updates the Player Rating in function of his played matches.
     * @param playerRating The player
     * @param resultList the results
     */
    public PlayerRating update(PlayerRating playerRating, List<MatchResult> resultList) {

        double phi = math.convertDeviation(playerRating.rd);
        double micro = math.convertRanking(playerRating.rating);

        if (resultList == null || resultList.isEmpty()) {
            double phiPrime = math.preRatingGrd(phi, playerRating.volatility);
            playerRating.rd = this.SCALE * phiPrime;
            return playerRating;
        }

        double vTermSum = 0;
        double deltaTermSum = 0;
        for (MatchResult result : resultList) {

            double phiJ = math.convertDeviation(result.opponentRd);
            double microJ = math.convertRanking(result.opponentRating);
            double g = math.computeG(phiJ);
            double e = math.computeE(micro, microJ, g);

            double vTerm = Math.pow(g, 2) * e * (1 - e);
            vTermSum = vTermSum + vTerm;

            double deltaTerm = g * (result.score - e);
            deltaTermSum = deltaTermSum + deltaTerm;
        }

        double v = 1/vTermSum;
        double delta = v * deltaTermSum;

        double newVol = math.newVol(playerRating.volatility, delta, v, phi);
        double newGrd = math.newGrd(phi, newVol, v);
        double newMu = math.newMu(micro, newGrd, delta, v);

        playerRating.rating = this.SCALE * newMu + 1500;
        playerRating.rd = this.SCALE * newGrd;
        playerRating.volatility = newVol;

        return playerRating;
    }

}

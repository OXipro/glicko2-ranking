package com.oxipro.ranking.glicko2;

public class PlayerRating {

    double rating;
    double volatility;
    double rd;
    long lastUpdate;

    /**
     * Creates a new PlayerRating class that stores all the necessary data to compute Glicko-2 ratings.
     * @param rating The current player rating (r)
     * @param rd The Rating Deviation (RD <-> φ)
     * @param volatility The volatility (σ)
     * @param lastUpdate The last time the {@code rating} was updated. UNIX Timestamp ms
     */
    public PlayerRating(double rating, double rd, double volatility, long lastUpdate) {
        this.rating = rating;
        this.rd = rd;
        this.volatility = volatility;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Get the player ranking
     * @return The player ranking
     */
    public double getRating() { return rating; }

    /**
     * Get the player volatility
     * @return The player volatility
     */
    public double getVolatility() { return volatility; }

    /**
     * Get the player Rating Deviation
     * @return The player Rating Deviation
     */
    public double getRatingDeviation() { return rd; }


    /**
     * Get the Last update in millis of the player ranking
     * @return the last update
     */
    public long getLastUpdate() { return lastUpdate; }
}

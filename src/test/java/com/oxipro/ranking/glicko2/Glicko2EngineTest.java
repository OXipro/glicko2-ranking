package com.oxipro.ranking.glicko2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.fail;

public class Glicko2EngineTest {

    // Glickman, 22 Mar 2022, worked example
    private static final double RATING = 1500;
    private static final double RD = 200;
    private static final double SIGMA = 0.06;

    private static final double RATING_PRIME = 1464.06;
    private static final double RD_PRIME = 151.52;
    private static final double SIGMA_PRIME = 0.05999;

    // Step 6 only (no games): phi* from phi=1.1513, sigma=0.06 -> RD on Glicko scale
    private static final double RD_AFTER_IDLE = 200.2714;

    private final List<String> failures = new ArrayList<>();

    @BeforeEach
    void setUp() {
        failures.clear();
    }

    @Test
    void updateMatchesPaperExample() {
        printHeader("update (paper example)");
        Glicko2Engine engine = new Glicko2Engine();
        PlayerRating player = new PlayerRating(RATING, RD, SIGMA, 0);

        List<MatchResult> results = List.of(
                new MatchResult(1400, 30, 1),
                new MatchResult(1550, 100, 0),
                new MatchResult(1700, 300, 0)
        );

        engine.update(player, results);

        check("rating'", RATING_PRIME, player.getRating(), 0.02);
        check("RD'", RD_PRIME, player.getRatingDeviation(), 0.02);
        check("sigma'", SIGMA_PRIME, player.getVolatility(), 0.0001);
        finish();
    }

    @Test
    void updateWithNoGamesOnlyIncreasesRd() {
        printHeader("update (no games)");
        Glicko2Engine engine = new Glicko2Engine();
        PlayerRating player = new PlayerRating(RATING, RD, SIGMA, 0);

        engine.update(player, Collections.emptyList());

        check("rating", RATING, player.getRating(), 0.001);
        check("sigma", SIGMA, player.getVolatility(), 0.000001);
        check("RD", RD_AFTER_IDLE, player.getRatingDeviation(), 0.001);
        finish();
    }

    @Test
    void ageIncreasesRdWithElapsedPeriods() {
        printHeader("age (1 period)");
        Glicko2Engine engine = new Glicko2Engine();
        PlayerRating player = new PlayerRating(RATING, RD, SIGMA, 0);

        engine.age(player, (long) engine.PERIOD);

        check("RD", RD_AFTER_IDLE, player.getRatingDeviation(), 0.001);
        check("rating", RATING, player.getRating(), 0.001);
        check("sigma", SIGMA, player.getVolatility(), 0.000001);
        finish();
    }

    @Test
    void enginePassesTauEpsilonScaleToMath() {
        printHeader("engine -> math config");
        Glicko2Engine engine = new Glicko2Engine(0.4, 0.00001, 200.0, 1000);
        check("t", 0.4, engine.math.t, 0);
        check("epsilon", 0.00001, engine.math.epsilon, 0);
        check("scale", 200.0, engine.math.scale, 0);
        finish();
    }

    private void printHeader(String title) {
        System.out.println(title);
        System.out.printf(Locale.US, "%-14s %14s %14s %14s %s%n", "value", "doc", "script", "delta", "status");
    }

    private void check(String name, double expected, double actual, double tolerance) {
        double gap = Math.abs(expected - actual);
        boolean ok = gap <= tolerance;
        System.out.printf(Locale.US, "%-14s %14.6f %14.6f %14.6f %s%n",
                name, expected, actual, gap, ok ? "PASS" : "FAIL");
        if (!ok) {
            failures.add(name + " doc=" + expected + " script=" + actual + " delta=" + gap);
        }
    }

    private void finish() {
        if (!failures.isEmpty()) {
            fail("Delta with expected values:\n" + String.join("\n", failures));
        }
    }
}

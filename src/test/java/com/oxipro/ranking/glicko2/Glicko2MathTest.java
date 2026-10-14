package com.oxipro.ranking.glicko2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.fail;

public class Glicko2MathTest {

    // Glickman, 22 Mar 2022, worked example
    private static final double TAU = 0.5;
    private static final double EPSILON = 0.000001;
    private static final double SCALE = 173.7178;

    private static final double MU = 0.0;
    private static final double PHI = 1.1513;
    private static final double SIGMA = 0.06;

    private static final double MU1 = -0.5756;
    private static final double MU2 = 0.2878;
    private static final double MU3 = 1.1513;
    private static final double PHI1 = 0.1727;
    private static final double PHI2 = 0.5756;
    private static final double PHI3 = 1.7269;

    private static final double G1 = 0.9955;
    private static final double G2 = 0.9531;
    private static final double G3 = 0.7242;
    private static final double E1 = 0.639;
    private static final double E2 = 0.432;
    private static final double E3 = 0.303;

    private static final double V = 1.7785;
    private static final double DELTA = -0.4834;

    private static final double A = -5.62682;
    private static final double B = -6.12682;
    private static final double FA = -0.00053567;
    private static final double FB = 1.999675;

    private static final double SIGMA_PRIME = 0.05999;
    private static final double PHI_STAR = 1.152862;
    private static final double PHI_PRIME = 0.8722;
    private static final double MU_PRIME = -0.2069;

    private final List<String> failures = new ArrayList<>();
    private Glicko2Math math;

    @BeforeEach
    void setUp() {
        failures.clear();
        math = new Glicko2Math(TAU, EPSILON, SCALE);
    }

    @Test
    void convertRanking() {
        printHeader("convertRanking");
        check("mu", MU, math.convertRanking(1500), 0.001);
        check("mu1", MU1, math.convertRanking(1400), 0.001);
        check("mu2", MU2, math.convertRanking(1550), 0.001);
        check("mu3", MU3, math.convertRanking(1700), 0.001);
        finish();
    }

    @Test
    void convertDeviation() {
        printHeader("convertDeviation");
        check("phi", PHI, math.convertDeviation(200), 0.001);
        check("phi1", PHI1, math.convertDeviation(30), 0.001);
        check("phi2", PHI2, math.convertDeviation(100), 0.001);
        check("phi3", PHI3, math.convertDeviation(300), 0.001);
        finish();
    }

    @Test
    void computeG() {
        printHeader("computeG");
        check("g1", G1, math.computeG(PHI1), 0.001);
        check("g2", G2, math.computeG(PHI2), 0.001);
        check("g3", G3, math.computeG(PHI3), 0.001);
        finish();
    }

    @Test
    void computeE() {
        printHeader("computeE");
        check("E1", E1, math.computeE(MU, MU1, G1), 0.001);
        check("E2", E2, math.computeE(MU, MU2, G2), 0.001);
        check("E3", E3, math.computeE(MU, MU3, G3), 0.001);
        finish();
    }

    @Test
    void computeF() {
        printHeader("computeF");
        check("f(A)", FA, math.computeF(A, 0.23367556, V, 1.32549169, SIGMA), 0.00001);
        check("f(B)", FB, math.computeF(B, 0.23367556, V, 1.32549169, SIGMA), 0.001);
        finish();
    }

    @Test
    void newVol() {
        printHeader("newVol");
        check("sigma'", SIGMA_PRIME, math.newVol(SIGMA, DELTA, V, PHI), 0.0001);
        finish();
    }

    @Test
    void preRatingGrd() {
        printHeader("preRatingGrd");
        check("phi*", PHI_STAR, math.preRatingGrd(PHI, SIGMA_PRIME), 0.001);
        finish();
    }

    @Test
    void newGrd() {
        printHeader("newGrd");
        check("phi'", PHI_PRIME, math.newGrd(PHI, SIGMA_PRIME, V), 0.001);
        finish();
    }

    @Test
    void newMu() {
        printHeader("newMu");
        check("mu'", MU_PRIME, math.newMu(MU, PHI_PRIME, DELTA, V), 0.001);
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
            fail("Delta with the Glicko-2 example:\n" + String.join("\n", failures));
        }
    }
}

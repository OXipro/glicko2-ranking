package com.oxipro.ranking.glicko2;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.fail;

public class Glicko2ExampleTest {

    private final List<String> failures = new ArrayList<>();

    @Test
    void exampleFromThePaper() {
        System.out.println("Test Glicko-2 (Glickman, 22 march 2022)");
        System.out.println("player 1500 RD 200 vol 0.06 vs 1400, 1550 et 1700 (scores 1, 0, 0).");
        System.out.printf(Locale.US, "%-14s %14s %14s %14s %s%n", "value", "doc", "script", "delta", "status");

        double mu = Main.convertRanking(1500f);
        double phi = Main.convertDeviation(200f);
        check("mu", 0, mu, 0.001);
        check("phi", 1.1513, phi, 0.001);

        double[] muJ = {
                Main.convertRanking(1400f),
                Main.convertRanking(1550f),
                Main.convertRanking(1700f)
        };
        double[] phiJ = {
                Main.convertDeviation(30f),
                Main.convertDeviation(100f),
                Main.convertDeviation(300f)
        };
        double[] scores = {1, 0, 0};

        double[] expectedMu = {-0.5756, 0.2878, 1.1513};
        double[] expectedPhi = {0.1727, 0.5756, 1.7269};
        double[] expectedG = {0.9955, 0.9531, 0.7242};
        double[] expectedE = {0.639, 0.432, 0.303};

        for (int j = 0; j < 3; j++) {
            int n = j + 1;
            check("mu" + n, expectedMu[j], muJ[j], 0.001);
            check("phi" + n, expectedPhi[j], phiJ[j], 0.001);
            check("g" + n, expectedG[j], Main.computeG(phiJ[j]), 0.001);
            check("E" + n, expectedE[j], Main.computeE(mu, muJ[j], phiJ[j]), 0.001);
        }

        double v = Main.computeV(mu, muJ, phiJ);
        double delta = Main.computeQ(scores, mu, muJ, phiJ);
        check("v", 1.7785, v, 0.001);
        check("delta", -0.4834, delta, 0.001);

        double sigma = Main.newVol(0.06, scores, mu, muJ, phiJ, phi);
        check("sigma'", 0.05999, sigma, 0.0001);

        double phiStar = Main.newGrd(phi, sigma);
        check("phi*", 1.152862, phiStar, 0.001);

        // Steps 7 and 8 are not separate methods. They are applied here to the program's outputs.
        double phiPrime = 1.0 / Math.sqrt(1.0 / (phiStar * phiStar) + 1.0 / v);
        double muPrime = mu + (phiPrime * phiPrime) * (delta / v);
        check("phi'", 0.8722, phiPrime, 0.001);
        check("mu'", -0.2069, muPrime, 0.001);
        check("rating'", 1464.06, 173.7178 * muPrime + 1500, 0.02);
        check("RD'", 151.52, 173.7178 * phiPrime, 0.02);

        if (!failures.isEmpty()) {
            fail("Delta with the Glicko-2 example:\n" + String.join("\n", failures));
        }
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
}

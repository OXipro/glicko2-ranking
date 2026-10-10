package com.oxipro.ranking.glicko2;

public class Main {

    public static void main(String[] args) {

        System.out.println(10*5/10*5);

    }

    public static double computeG(double grd) {
        double g = 1/(Math.sqrt(1+  3*Math.pow(grd,2)/Math.pow(Math.PI,2))  );
        return g;
    }

    public static double computeE(double micro, double microI, double grdI) {
        double e = 1/(1+ Math.exp(-computeG(grdI)*(micro-microI)));
        return e;
    }

    public static double computeV(double mu, double[] muMicro, double[] muGrd) {

        double total = 0;

        for (int j = 0; j < muMicro.length; j++) {

            double g = computeG(muGrd[j]);
            double e = computeE(mu, muMicro[j], muGrd[j]);

            double vI = Math.pow(g, 2) * e * (1 - e);

            total = total + vI;
        }

        return 1/total;
    }

    public static double computeQ(double s[], double mu, double[] muMicro, double[] muGrd) {

        double total = 0;

        for (int j = 0; j < muMicro.length; j++) {

            double g = computeG(muGrd[j]);
            double e = computeE(mu, muMicro[j], muGrd[j]);

            total = total + g * (s[j] - e);

        }

        return total * computeV(mu, muMicro, muGrd);

    }





    public static double convertRanking(float rating) {
        double gr = (rating-1500)/173.7178;
        return gr;
    }

    public static double convertDeviation(float deviation) {
        double gd = deviation/173.7178;
        return gd;
    }
}
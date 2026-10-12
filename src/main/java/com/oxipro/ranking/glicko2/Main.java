package com.oxipro.ranking.glicko2;

public class Main {

    public static final double t = 0.5;
    public static final double epsilon = 0.000001;

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

    public static double computeF(double x, double s[], double grd, double mu, double[] muMicro, double[] muGrd, double vol) {
        double v = computeV(mu, muMicro, muGrd);
        double q = computeQ(s, mu, muMicro, muGrd);

        double EsqX = Math.pow(Math.E, x);
        double GRDsq2 = Math.pow(grd, 2);

        double FTop = EsqX *(Math.pow(q, 2) - GRDsq2 - v - EsqX);
        double FBottom = 2* Math.pow((GRDsq2 + v + EsqX), 2);

        double L = FTop/FBottom;

        double a = Math.log(Math.pow(vol, 2));

        double RTop = (x - a);
        double RBottom = Math.pow(t, 2);

        double R = RTop/RBottom;

        double f = L - R;

        return f;
    }


    public static double newVol(double vol, double s[], double mu, double[] muMicro, double[] muGrd, double grd) {

        double a = Math.log(Math.pow(vol, 2));

        double qSQ = Math.pow(computeQ(s, mu, muMicro, muGrd), 2);
        double grdSQ = Math.pow(grd, 2);
        double v = computeV(mu, muMicro, muGrd);

        double b;

        if (qSQ > (grdSQ + v)) {
            b = Math.log(qSQ - grdSQ - v);
        } else {
            double k = 1;
            while (computeF(a - k * t, s, grd, mu, muMicro, muGrd, vol) < 0) {
                k = k + 1;
            }
            b = a - k * t;
        }

        double fa = computeF(a, s, grd, mu, muMicro, muGrd, vol);
        double fb = computeF(b, s, grd, mu, muMicro, muGrd, vol);

        while (Math.abs(b - a) > epsilon) {
            double c = a +
                    ((a - b)*fa)
                    /
                    (fb - fa );

            double fc = computeF(c, s, grd, mu, muMicro, muGrd, vol);
            if ((fc * fb) <= 0) {
               a = b;
               fa = fb;
            } else {
                fa = fa/2;
            }
            b = c;
            fb = fc;
            if (Math.abs(b - a) <= epsilon) {
                break;
            }
        }

        return Math.exp(a / 2);
    }

    public static double newGrd(double old, double newVol) {
        double oldSQ = Math.pow(old, 2);
        double volSQ = Math.pow(newVol, 2);

        return Math.sqrt(oldSQ + volSQ);
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

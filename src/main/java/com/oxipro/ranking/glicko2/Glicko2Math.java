package com.oxipro.ranking.glicko2;

public class Glicko2Math {

    public final double t;
    public final double epsilon;
    public final double scale;
    public final double ratingBase;

    public Glicko2Math(double t, double epsilon, double scale, double ratingBase) {
        this.t = t;
        this.epsilon = epsilon;
        this.scale = scale;
        this.ratingBase = ratingBase;
    }

    public double computeG(double grd) {
        double g = 1/(Math.sqrt(1+  3*Math.pow(grd,2)/Math.pow(Math.PI,2))  );
        return g;
    }

    public double computeE(double micro, double microI, double g) {
        double e = 1/(1+ Math.exp(-g*(micro-microI)));
        return e;
    }

    public double computeF(double x, double qSQ, double v, double grdSQ, double vol) {
        double EsqX = Math.pow(Math.E, x);

        double FTop = EsqX *(qSQ - grdSQ - v - EsqX);
        double FBottom = 2* Math.pow((grdSQ + v + EsqX), 2);

        double L = FTop/FBottom;

        double a = Math.log(Math.pow(vol, 2));

        double RTop = (x - a);
        double RBottom = Math.pow(t, 2);

        double R = RTop/RBottom;

        double f = L - R;

        return f;
    }

    public double newVol(double vol, double delta, double v, double grd) {

        double a = Math.log(Math.pow(vol, 2));

        double qSQ = Math.pow(delta, 2);
        double grdSQ = Math.pow(grd, 2);

        double b;

        if (qSQ > (grdSQ + v)) {
            b = Math.log(qSQ - grdSQ - v);
        } else {
            double k = 1;
            while (computeF(a - k * t, qSQ, v, grdSQ, vol) < 0) {
                k = k + 1;
            }
            b = a - k * t;
        }

        double fa = computeF(a, qSQ, v, grdSQ, vol);
        double fb = computeF(b, qSQ, v, grdSQ, vol);

        while (Math.abs(b - a) > epsilon) {
            double c = a +
                    ((a - b)*fa)
                    /
                    (fb - fa );

            double fc = computeF(c, qSQ, v, grdSQ, vol);
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

    public double preRatingGrd(double old, double newVol) {
        double oldSQ = Math.pow(old, 2);
        double volSQ = Math.pow(newVol, 2);

        return Math.sqrt(oldSQ + volSQ);
    }

    public double newGrd(double old, double newVol, double v) {

        double preRatingGrd = preRatingGrd(old, newVol);
        double l = 1/(Math.pow(preRatingGrd, 2));
        double r = 1/v;

        double grd = 1/(Math.sqrt(l + r));

        return grd;
    }

    public double newMu(double mu, double newGrd, double delta, double v) {
        return mu + Math.pow(newGrd, 2) * (delta / v);
    }

    public double convertRanking(double rating) {
        double gr = (rating - ratingBase) / scale;
        return gr;
    }

    public double convertMu(double mu) {
        return scale * mu + ratingBase;
    }

    public double convertDeviation(double deviation) {
        double gd = deviation/scale;
        return gd;
    }
}

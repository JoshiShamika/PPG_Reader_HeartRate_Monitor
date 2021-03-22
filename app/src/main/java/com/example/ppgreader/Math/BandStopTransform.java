
package com.example.ppgreader.Math;
import org.apache.commons.math3.complex.Complex;


public class BandStopTransform {


  private double wc;
  private double wc2;
  private double a;
  private double b;
  private double a2;
  private double b2;


  public BandStopTransform(double fc,
                           double fw,
                           LayoutBase digital,
                           LayoutBase analog) {
        digital.reset();

        double ww = 2 * Math.PI * fw;

        wc2 = 2 * Math.PI * fc - (ww / 2);
        wc = wc2 + ww;

        // this is crap
        if (wc2 < 1e-8)
            wc2 = 1e-8;
        if (wc > Math.PI - 1e-8)
            wc = Math.PI - 1e-8;

        a = Math.cos((wc + wc2) * .5) /
                Math.cos((wc - wc2) * .5);
        b = Math.tan((wc - wc2) * .5);
        a2 = a * a;
        b2 = b * b;

        int numPoles = analog.getNumPoles();
        int pairs = numPoles / 2;
        for (int i = 0; i < pairs; i++) {
            PoleZeroPair pair = analog.getPair(i);
            ComplexPair p = transform(pair.poles.first);
            ComplexPair z = transform(pair.zeros.first);
            digital.addPoleZeroConjugatePairs(p.first, z.first);
            digital.addPoleZeroConjugatePairs(p.second, z.second);
        }

        if ((numPoles & 1) == 1) {
            ComplexPair poles = transform(analog.getPair(pairs).poles.first);
            ComplexPair zeros = transform(analog.getPair(pairs).zeros.first);

            digital.add(poles, zeros);
        }

        if (fc < 0.25)
            digital.setNormal(Math.PI, analog.getNormalGain());
        else
            digital.setNormal(0, analog.getNormalGain());
    }

    private ComplexPair transform(Complex c) {
        if (c.isInfinite())
            c = new Complex(-1);
        else
            c = ((new Complex(1)).add(c)).divide((new Complex(1)).subtract(c)); // bilinear

        Complex u = new Complex(0);
        u = MathSupplement.addmul(u, 4 * (b2 + a2 - 1), c);
        u = u.add(8 * (b2 - a2 + 1));
        u = u.multiply(c);
        u = u.add(4 * (a2 + b2 - 1));
        u = u.sqrt();

        Complex v = u.multiply(-.5);
        v = v.add(a);
        v = MathSupplement.addmul(v, -a, c);

        u = u.multiply(.5);
        u = u.add(a);
        u = MathSupplement.addmul(u, -a, c);

        Complex d = new Complex(b + 1);
        d = MathSupplement.addmul(d, b - 1, c);

        return new ComplexPair(u.divide(d), v.divide(d));
    }

}

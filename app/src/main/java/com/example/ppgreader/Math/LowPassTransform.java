package com.example.ppgreader.Math;

import org.apache.commons.math3.complex.Complex;


public class LowPassTransform {

	private double f;

	private Complex transform(Complex c) {
		if (c.isInfinite())
			return new Complex(-1, 0);


		c = c.multiply(f);

		Complex one = new Complex(1, 0);


		return (one.add(c)).divide(one.subtract(c));
	}

	public LowPassTransform(double fc, LayoutBase digital, LayoutBase analog) {
		digital.reset();


		f = Math.tan(Math.PI * fc);

		int numPoles = analog.getNumPoles();
		int pairs = numPoles / 2;
		for (int i = 0; i < pairs; ++i) {
			PoleZeroPair pair = analog.getPair(i);
			digital.addPoleZeroConjugatePairs(transform(pair.poles.first),
					transform(pair.zeros.first));
		}

		if ((numPoles & 1) == 1) {
			PoleZeroPair pair = analog.getPair(pairs);
			digital.add(transform(pair.poles.first),
					transform(pair.zeros.first));
		}

		digital.setNormal(analog.getNormalW(), analog.getNormalGain());
	}

}

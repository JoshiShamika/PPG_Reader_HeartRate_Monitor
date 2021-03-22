package com.example.ppgreader.Math;

import org.apache.commons.math3.complex.Complex;


public class MathSupplement {
	
	public static double doubleLn10 =2.3025850929940456840179914546844;

	public static Complex solve_quadratic_1(double a, double b, double c) {
		return (new Complex(-b).add(new Complex(b * b - 4 * a * c, 0)).sqrt())
				.divide(2. * a);
	}

	public static Complex solve_quadratic_2(double a, double b, double c) {
		return (new Complex(-b).subtract(new Complex(b * b - 4 * a * c, 0))
				.sqrt()).divide(2. * a);
	}

	public static Complex adjust_imag(Complex c) {
		if (Math.abs(c.getImaginary()) < 1e-30)
			return new Complex(c.getReal(), 0);
		else
			return c;
	}

	public static Complex addmul(Complex c, double v, Complex c1) {
		return new Complex(c.getReal() + v * c1.getReal(), c.getImaginary() + v
				* c1.getImaginary());
	}

	public static Complex recip(Complex c) {
		double n = 1.0 / (c.abs() * c.abs());

		return new Complex(n * c.getReal(), n * c.getImaginary());
	}

	public static double asinh(double x) {
		return Math.log(x + Math.sqrt(x * x + 1));
	}

	public static double acosh(double x) {
		return Math.log(x + Math.sqrt(x * x - 1));
	}

}



package com.example.ppgreader.Math;


import org.apache.commons.math3.complex.Complex;


public class BiquadPoleState extends PoleZeroPair {


	public BiquadPoleState(Complex p, Complex z) {
        super(p, z);
    }

	public BiquadPoleState(Complex p1, Complex z1,
                           Complex p2, Complex z2) {
        super(p1, z1, p2, z2);
    }

    double gain;

}

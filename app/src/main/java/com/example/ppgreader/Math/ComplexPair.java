

package com.example.ppgreader.Math;

import org.apache.commons.math3.complex.Complex;


public class ComplexPair {

        public Complex first;
        public Complex second;

        ComplexPair(Complex c1,
                    Complex c2) {
            first = c1;
            second = c2;
        }

        ComplexPair(Complex c1) {
            first = c1;
            second = new Complex(0,0);
        }

        boolean isConjugate () {
            return second.equals(first.conjugate());
        }

        boolean isReal () {
            return first.getImaginary() == 0 && second.getImaginary() == 0;
        }


        boolean isMatchedPair () {
            if (first.getImaginary() != 0)
                return second.equals(first.conjugate());
            else
            return second.getImaginary() == 0 &&
                    second.getReal() != 0 &&
                    first.getReal() != 0;
        }

        boolean is_nan()
        {
            return first.isNaN() || second.isNaN();
        }
    };

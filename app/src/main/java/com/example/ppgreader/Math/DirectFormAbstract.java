

package com.example.ppgreader.Math;


public abstract class DirectFormAbstract {

    public DirectFormAbstract () {
        reset();
    }

    public abstract void reset();

    public abstract double process1 (double in, Biquad s);

    public static final int DIRECT_FORM_I = 0;
    public static final int DIRECT_FORM_II = 1;

};

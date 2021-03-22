package com.example.ppgreader.Math;

import org.apache.commons.math3.complex.Complex;

public class LayoutBase {

	private int m_numPoles;
	private PoleZeroPair[] m_pair;
	private double m_normalW;
	private double m_normalGain;

	public LayoutBase(PoleZeroPair[] pairs) {
		m_numPoles = pairs.length * 2;
		m_pair = pairs;
	}

	public LayoutBase(int numPoles) {
		m_numPoles = 0;
		if ((numPoles % 2) == 1) {
			m_pair = new PoleZeroPair[numPoles / 2 + 1];
		} else {
			m_pair = new PoleZeroPair[numPoles / 2];
		}
	}

	public void reset() {
		m_numPoles = 0;
	}

	public int getNumPoles() {
		return m_numPoles;
	}

	public void add(Complex pole, Complex zero) {
		m_pair[m_numPoles / 2] = new PoleZeroPair(pole, zero);
		++m_numPoles;
	}

	public void addPoleZeroConjugatePairs(Complex pole, Complex zero) {
		if (pole == null) System.out.println("LayoutBase addConj() pole == null");
		if (zero == null) System.out.println("LayoutBase addConj() zero == null");
		if (m_pair == null) System.out.println("LayoutBase addConj() m_pair == null");
		m_pair[m_numPoles / 2] = new PoleZeroPair(pole, zero, pole.conjugate(),
				zero.conjugate());
		m_numPoles += 2;
	}

	public void add(ComplexPair poles, ComplexPair zeros) {
		System.out.println("LayoutBase add() numPoles="+m_numPoles);
		m_pair[m_numPoles / 2] = new PoleZeroPair(poles.first, zeros.first,
				poles.second, zeros.second);
		m_numPoles += 2;
	}

	public PoleZeroPair getPair(int pairIndex) {
		return m_pair[pairIndex];
	}

	public double getNormalW() {
		return m_normalW;
	}

	public double getNormalGain() {
		return m_normalGain;
	}

	public void setNormal(double w, double g) {
		m_normalW = w;
		m_normalGain = g;
	}
};

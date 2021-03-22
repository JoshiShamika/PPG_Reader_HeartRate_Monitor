

package com.example.ppgreader.Math;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

public class Cascade {


	private Biquad[] m_biquads;

	private DirectFormAbstract[] m_states;


	private int m_numBiquads;

	private int numPoles;

	public int getNumBiquads() {
		return m_numBiquads;
	}

	public Biquad getBiquad(int index) {
		return m_biquads[index];
	}

	public Cascade() {
		m_numBiquads = 0;
		m_biquads = null;
		m_states = null;
	}

	public void reset() {
		for (int i = 0; i < m_numBiquads; i++)
			m_states[i].reset();
	}

	public double filter(double in) {
		double out = in;
		for (int i = 0; i < m_numBiquads; i++) {
			if (m_states[i] != null) {
			out = m_states[i].process1(out, m_biquads[i]);
			}
		}
		return out;
	}

	public Complex response(double normalizedFrequency) {
		double w = 2 * Math.PI * normalizedFrequency;
		Complex czn1 = ComplexUtils.polar2Complex(1., -w);
		Complex czn2 = ComplexUtils.polar2Complex(1., -2 * w);
		Complex ch = new Complex(1);
		Complex cbot = new Complex(1);

		for (int i = 0; i < m_numBiquads; i++) {
			Biquad stage = m_biquads[i];
			Complex cb = new Complex(1);
			Complex ct = new Complex(stage.getB0() / stage.getA0());
			ct = MathSupplement.addmul(ct, stage.getB1() / stage.getA0(), czn1);
			ct = MathSupplement.addmul(ct, stage.getB2() / stage.getA0(), czn2);
			cb = MathSupplement.addmul(cb, stage.getA1() / stage.getA0(), czn1);
			cb = MathSupplement.addmul(cb, stage.getA2() / stage.getA0(), czn2);
			ch = ch.multiply(ct);
			cbot = cbot.multiply(cb);
		}

		return ch.divide(cbot);
	}

	public void applyScale(double scale) {

		if (m_biquads.length>0) {
			m_biquads[0].applyScale(scale);
		}
	}

	public void setLayout(LayoutBase proto, int filterTypes) {
		numPoles = proto.getNumPoles();
		m_numBiquads = (numPoles + 1) / 2;
		m_biquads = new Biquad[m_numBiquads];
		switch (filterTypes) {
		case DirectFormAbstract.DIRECT_FORM_I:
			m_states = new DirectFormI[m_numBiquads];
			for (int i = 0; i < m_numBiquads; i++) {
				m_states[i] = new DirectFormI();
			}
			break;
		case DirectFormAbstract.DIRECT_FORM_II:
		default:
			m_states = new DirectFormII[m_numBiquads];
			for (int i = 0; i < m_numBiquads; i++) {
				m_states[i] = new DirectFormII();
			}
			break;
		}
		for (int i = 0; i < m_numBiquads; ++i) {
			PoleZeroPair p = proto.getPair(i);
			m_biquads[i] = new Biquad();
			m_biquads[i].setPoleZeroPair(p);
		}
		applyScale(proto.getNormalGain()
				/ ((response(proto.getNormalW() / (2 * Math.PI)))).abs());
	}

};

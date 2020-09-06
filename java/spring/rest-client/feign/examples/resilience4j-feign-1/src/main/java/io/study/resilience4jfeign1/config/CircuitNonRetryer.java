package io.study.resilience4jfeign1.config;

import feign.RetryableException;
import feign.Retryer;

public class CircuitNonRetryer implements Retryer {

	@Override
	public void continueOrPropagate(RetryableException e) {
		throw e;
	}

	@Override
	public Retryer clone() {
		return new CircuitNonRetryer();
	}
}

package com.infy.RewardPointCalculator.exception;

public class UnsupportedJwtTokenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedJwtTokenException(String message) {
        super(message);
    }
}
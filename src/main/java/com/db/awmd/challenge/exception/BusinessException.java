package com.db.awmd.challenge.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 255721644032051440L;

	public BusinessException(String message) {
		super(message);
	}
}
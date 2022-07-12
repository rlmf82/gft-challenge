package com.db.awmd.challenge.exception;

public class InvalidValueException extends BusinessException {

	private static final long serialVersionUID = 255721644032051440L;

	public InvalidValueException() {
		super("The informed value is invalid. The operation will not be performed.");
	}
}
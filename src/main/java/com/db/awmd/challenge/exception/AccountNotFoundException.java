package com.db.awmd.challenge.exception;

public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 255721644032051440L;

	public AccountNotFoundException(String accountId) {
		super(String.format("The account %s does not exist", accountId));
	}
}
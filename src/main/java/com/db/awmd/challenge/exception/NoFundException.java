package com.db.awmd.challenge.exception;

public class NoFundException extends BusinessException {

	private static final long serialVersionUID = 255721644032051440L;

	public NoFundException(String accountId) {
		super(String.format("The account %s has insufficient funds. The operation will not be performed.", accountId));
	}
}
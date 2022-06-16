package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferMoney;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.InvalidValueException;
import com.db.awmd.challenge.exception.NoFundException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;
	
	private final NotificationService notificationService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public synchronized void transferValuesAccount(TransferMoney transferData) {
		int negativeValue = -1;
		int positiveValue = 1;

		Account accountFrom = this.accountsRepository.getAccount(transferData.getAccountFrom());  
		
		if(accountFrom == null) {
			throw new AccountNotFoundException(transferData.getAccountFrom());
		}

		Account accountTo = this.accountsRepository.getAccount(transferData.getAccountTo());

		if(accountTo == null) {
			throw new AccountNotFoundException(transferData.getAccountTo());
		}

		if(transferData.getValue().compareTo(BigDecimal.ZERO) != positiveValue) {
			throw new InvalidValueException();
		}

		if(accountFrom.getBalance().subtract(transferData.getValue()).compareTo(BigDecimal.ZERO) == negativeValue) {
			throw new NoFundException(accountFrom.getAccountId());
		}

		accountTo.setBalance(accountTo.getBalance().add(transferData.getValue()));
		accountFrom.setBalance(accountFrom.getBalance().subtract(transferData.getValue()));

		accountsRepository.updateAccount(accountTo);
		accountsRepository.updateAccount(accountFrom);

		notifyTransferenceOperation(transferData.getValue(), accountFrom, accountTo);
	}

	private synchronized void notifyTransferenceOperation(BigDecimal value, Account accountFrom, Account accountTo) {
		notificationService.notifyAboutTransfer(accountTo, String.format("You received %s in your account", value));
		notificationService.notifyAboutTransfer(accountFrom, String.format("You transfered %s from your account to the account %s", value, accountTo.getAccountId()));
	}
}
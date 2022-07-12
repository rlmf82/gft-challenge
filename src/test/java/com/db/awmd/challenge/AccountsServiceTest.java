package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferMoney;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidValueException;
import com.db.awmd.challenge.exception.NoFundException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Mock
	private NotificationService notificationServiceMock;

	@Mock
	private AccountsRepository accountsRepositoryMock;

	@InjectMocks
	private AccountsService accountsServiceInjected;
	
	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}
	}

	@Test
	public void transferFunds() throws Exception {
		String idAccountTo = "2";
		String idAccountFrom = "1";
		
		doNothing().when(notificationServiceMock).notifyAboutTransfer(any(), any());
		when(accountsRepositoryMock.getAccount(idAccountTo)).thenReturn(new Account(idAccountTo, new BigDecimal("1000")));
		when(accountsRepositoryMock.getAccount(idAccountFrom)).thenReturn(new Account(idAccountFrom, new BigDecimal("150")));
		
		try {
			this.accountsServiceInjected
			.transferValuesAccount(
					TransferMoney
						.builder()
						.accountFrom(idAccountFrom)
						.accountTo(idAccountTo)
						.value(new BigDecimal(100))
						.build());
			
		} catch (Exception ex) {
			fail("Should have failed when transfering funds between accounts");
		}
		
		Mockito.verify(accountsRepositoryMock, Mockito.times(2)).getAccount(Mockito.any());
		Mockito.verify(accountsRepositoryMock, Mockito.times(2)).updateAccount(Mockito.any());
		Mockito.verify(notificationServiceMock, Mockito.times(2)).notifyAboutTransfer(Mockito.any(), Mockito.any());
	}

	@Test
	public void transferFunds_NoFundsAvailable() throws Exception {
		String idAccountTo = "2";
		String idAccountFrom = "1";
		
		doNothing().when(notificationServiceMock).notifyAboutTransfer(any(), any());
		when(accountsRepositoryMock.getAccount(idAccountTo)).thenReturn(new Account(idAccountTo, new BigDecimal("1000")));
		when(accountsRepositoryMock.getAccount(idAccountFrom)).thenReturn(new Account(idAccountFrom, new BigDecimal("50")));
		
		NoFundException noFundsException = new NoFundException(idAccountFrom);
		
		try {
			this.accountsServiceInjected
			.transferValuesAccount(
					TransferMoney
						.builder()
						.accountFrom(idAccountFrom)
						.accountTo(idAccountTo)
						.value(new BigDecimal(100))
						.build());
			
			fail("NoFundsException should be thrown");

		} catch (NoFundException ex) {
			assertTrue(ex.getMessage().equals(noFundsException.getMessage()));
		}
		
		Mockito.verify(accountsRepositoryMock, Mockito.times(2)).getAccount(Mockito.any());
		Mockito.verify(accountsRepositoryMock, Mockito.times(0)).updateAccount(Mockito.any());
		Mockito.verify(notificationServiceMock, Mockito.times(0)).notifyAboutTransfer(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void transferFunds_AcountNotFound() throws Exception {
		String idAccountTo = "2";
		String idAccountFrom = "1";
		
		doNothing().when(notificationServiceMock).notifyAboutTransfer(any(), any());
		when(accountsRepositoryMock.getAccount(idAccountTo)).thenReturn(new Account(idAccountFrom, new BigDecimal("50")));
		when(accountsRepositoryMock.getAccount(idAccountFrom)).thenReturn(null);
		
		AccountNotFoundException accountNotFoundException = new AccountNotFoundException(idAccountFrom);
		
		try {
			this.accountsServiceInjected
			.transferValuesAccount(
					TransferMoney
						.builder()
						.accountFrom(idAccountFrom)
						.accountTo(idAccountTo)
						.value(new BigDecimal(100))
						.build());
			
			fail("NoFundsException should be thrown");

		} catch (AccountNotFoundException ex) {
			assertTrue(ex.getMessage().equals(accountNotFoundException.getMessage()));
		}
		
		Mockito.verify(accountsRepositoryMock, Mockito.times(1)).getAccount(Mockito.any());
		Mockito.verify(accountsRepositoryMock, Mockito.times(0)).updateAccount(Mockito.any());
		Mockito.verify(notificationServiceMock, Mockito.times(0)).notifyAboutTransfer(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void transferFunds_NegativeAmount() throws Exception {
		String idAccountTo = "2";
		String idAccountFrom = "1";
		
		doNothing().when(notificationServiceMock).notifyAboutTransfer(any(), any());
		when(accountsRepositoryMock.getAccount(idAccountTo)).thenReturn(new Account(idAccountFrom, new BigDecimal("50")));
		when(accountsRepositoryMock.getAccount(idAccountFrom)).thenReturn(new Account(idAccountFrom, new BigDecimal("50")));
		
		InvalidValueException invalidValueException = new InvalidValueException();
		
		try {
			this.accountsServiceInjected
			.transferValuesAccount(
					TransferMoney
						.builder()
						.accountFrom(idAccountFrom)
						.accountTo(idAccountTo)
						.value(new BigDecimal(-200))
						.build());
			
			fail("NoFundsException should be thrown");

		} catch (InvalidValueException ex) {
			assertTrue(ex.getMessage().equals(invalidValueException.getMessage()));
		}
		
		Mockito.verify(accountsRepositoryMock, Mockito.times(2)).getAccount(Mockito.any());
		Mockito.verify(accountsRepositoryMock, Mockito.times(0)).updateAccount(Mockito.any());
		Mockito.verify(notificationServiceMock, Mockito.times(0)).notifyAboutTransfer(Mockito.any(), Mockito.any());
	}
	
}
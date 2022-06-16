package com.db.awmd.challenge.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferMoneyDto {

	@NotNull
	@NotEmpty
	private String accountFrom;

	@NotNull
	@NotEmpty
	private String accountTo;

	@NotNull
	private BigDecimal value;

}
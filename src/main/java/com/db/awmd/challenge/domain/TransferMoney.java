package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransferMoney {

  private final String accountFrom;

  private final String accountTo;
  
  private final BigDecimal value;
  
}
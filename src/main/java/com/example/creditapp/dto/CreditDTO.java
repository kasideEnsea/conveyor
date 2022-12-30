package com.example.creditapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class CreditDTO {
     private BigDecimal amount;
     private Integer term;
     private BigDecimal monthlyPayment;
     private BigDecimal rate;
     private BigDecimal psk;
     private Boolean isInsuranceEnabled;
     private Boolean isSalaryClient;
     private List<PaymentScheduleElement> paymentSchedule;
}

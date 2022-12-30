package com.example.creditapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LoanApplicationRequestDTO {
   private BigDecimal amount;
   private Integer term;
   private String firstName;
   private String lastName;
   private String middleName;
   private String email;
   private LocalDate birthdate;
   private String passportSeries;
   private String passportNumber;
}

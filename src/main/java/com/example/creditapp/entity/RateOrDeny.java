package com.example.creditapp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class RateOrDeny {
    private BigDecimal rate;
    private boolean isDenied;
}

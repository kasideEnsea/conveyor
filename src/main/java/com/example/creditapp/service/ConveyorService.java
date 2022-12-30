package com.example.creditapp.service;

import com.example.creditapp.DataUtils;
import com.example.creditapp.dto.LoanApplicationRequestDTO;
import com.example.creditapp.dto.LoanOfferDTO;
import com.example.creditapp.dto.PaymentScheduleElement;
import com.example.creditapp.dto.ScoringDataDTO;
import com.example.creditapp.entity.RateOrDeny;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ConveyorService {

    @Value("$insurance_cost")
    private final BigDecimal INSURANCE_COST;

    @Value("$base_rate")
    private final BigDecimal BASE_RATE;

    @Value("$insurance_rate")
    private final BigDecimal INSURANCE_RATE_DECLINE;

    @Value("$sc_rate")
    private final BigDecimal SALARY_CLIENT_RATE_DECLINE;


    public boolean prescoring(LoanApplicationRequestDTO dto) {
        log.info(dto.toString());
        return (DataUtils.checkLengthBetween(dto.getFirstName(), 2, 30) &&
                DataUtils.checkLengthBetween(dto.getLastName(), 2, 30) &&
                (DataUtils.isEmptyOrNull(dto.getMiddleName()) ||
                        DataUtils.checkLengthBetween(dto.getMiddleName(), 2, 30)) &&
                dto.getAmount().compareTo(BigDecimal.valueOf(10000)) >= 0 &&
                dto.getTerm() >= 6 &&
                DataUtils.isOlderThan(dto.getBirthdate(), 18) &&
                DataUtils.isEmailValid(dto.getEmail()) &&
                DataUtils.checkLengthEquals(dto.getPassportNumber(), 6) &&
                DataUtils.checkLengthEquals(dto.getPassportSeries(), 4)
        );
    }


    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info(loanApplicationRequestDTO.toString());
        List<LoanOfferDTO> offers = new ArrayList<>();
        offers.add(createOffer(loanApplicationRequestDTO, true, true));
        offers.add(createOffer(loanApplicationRequestDTO, true, false));
        offers.add(createOffer(loanApplicationRequestDTO, false, true));
        offers.add(createOffer(loanApplicationRequestDTO, false, false));
        offers.sort((o1, o2) -> o1.getRate().subtract(o2.getRate()).signum());
        offers.forEach(loanOfferDTO -> log.info(loanOfferDTO.toString()));
        return offers;
    }

    private LoanOfferDTO createOffer(LoanApplicationRequestDTO requestDTO, boolean isInsuranceEnabled,
                                     boolean isSalaryClient) {
        log.info(requestDTO.toString() + ", is insurance enable: " + isInsuranceEnabled + ", is salary client: " + isSalaryClient);
        BigDecimal requestedAmount = requestDTO.getAmount();
        BigDecimal totalAmount = isInsuranceEnabled ? requestedAmount.add(INSURANCE_COST) : requestedAmount;
        log.info("Total amount: " + totalAmount);
        Integer term = requestDTO.getTerm();
        BigDecimal rate = generateRate(isInsuranceEnabled, isSalaryClient);
        log.info("Rate: " + rate);
        //Нормальные id предложений будут формироваться при добавлении в БД как primary key, пока что хардкод
        return new LoanOfferDTO(1L, requestedAmount, totalAmount, term, monthlyPayment(totalAmount, rate, term),
                rate, isInsuranceEnabled, isSalaryClient);
    }

    private BigDecimal generateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        log.info("is insurance enable: " + isInsuranceEnabled + ", is salary client: " + isSalaryClient);
        BigDecimal insuranceDecline = isInsuranceEnabled ? INSURANCE_RATE_DECLINE : BigDecimal.valueOf(0);
        BigDecimal scDecline = isSalaryClient ? SALARY_CLIENT_RATE_DECLINE : BigDecimal.valueOf(0);
        log.info(insuranceDecline.toString() + ", " + scDecline.toString());
        return BASE_RATE.subtract(insuranceDecline).subtract(scDecline);
    }

    public BigDecimal monthlyPayment(BigDecimal amount, BigDecimal rate, Integer term) {
        return amount.multiply(rate
                .add(rate.divide(BigDecimal.valueOf(Math.pow((rate
                        .add(BigDecimal.valueOf(1)))
                        .doubleValue(), term) - 1), 0)));
    }

    public RateOrDeny score(ScoringDataDTO dto) {
        log.info(dto.toString());
        BigDecimal rate = generateRate(dto.getIsInsuranceEnabled(), dto.getIsSalaryClient());
        RateOrDeny rateOrDeny = new RateOrDeny();
        switch (dto.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED:
                rateOrDeny.setDenied(true);
                return rateOrDeny;
            case SELF_EMPLOYED:
                rate = rate.add(BigDecimal.valueOf(0.01));
                break;
            case BUSINESS_OWNER:
                rate = rate.add(BigDecimal.valueOf(0.03));
                break;
        }
        switch (dto.getEmployment().getPosition()) {
            case MIDDLE_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(0.02));
                break;
            case TOP_MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(0.04));
                break;
        }
        rateOrDeny.setRate(rate);
        log.info(rateOrDeny.toString());
        return rateOrDeny;
    }

    public List<PaymentScheduleElement> createPaymentSchedule(BigDecimal amount, BigDecimal rate, Integer term) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        int i = 1;
        LocalDate date = LocalDate.now();
        log.info(date.toString());
        BigDecimal totalPayment = monthlyPayment(amount, rate, term);
        log.info(totalPayment.toString());
        BigDecimal debtPayment = amount.divide(rate, 0);
        log.info(debtPayment.toString());
        while (i <= term) {
            PaymentScheduleElement element = new PaymentScheduleElement();
            element.setNumber(i++);
            date = date.plusMonths(1);
            element.setDate(date);
            element.setTotalPayment(totalPayment);
            element.setDebtPayment(debtPayment);
            element.setInterestPayment(totalPayment.subtract(debtPayment));
            element.setRemainingDebt(amount.subtract(totalPayment.multiply(BigDecimal.valueOf(i))));
            paymentSchedule.add(element);
        }
        return paymentSchedule;
    }
}

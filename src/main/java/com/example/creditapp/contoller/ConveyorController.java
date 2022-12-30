package com.example.creditapp.contoller;

import com.example.creditapp.dto.*;
import com.example.creditapp.service.ConveyorService;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@Data
@RequestMapping("conveyor")
public class ConveyorController {
    private final ConveyorService conveyorService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> getAllGroups(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offers = new ArrayList<>();
        if (conveyorService.prescoring(loanApplicationRequestDTO)){
            offers = conveyorService.getOffers(loanApplicationRequestDTO);
        }
        return offers;
    }

    @PostMapping("/calculation")
    public CreditDTO getAllGroups(ScoringDataDTO scoringDataDTO) {
        CreditDTO creditDTO = new CreditDTO();
        BigDecimal rate = conveyorService.score(scoringDataDTO).getRate();
        boolean isScored = conveyorService.score(scoringDataDTO).isDenied();
        List<PaymentScheduleElement> paymentSchedule = conveyorService.createPaymentSchedule(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm());
        if (isScored && rate != null) {
            creditDTO.setAmount(scoringDataDTO.getAmount());
            creditDTO.setTerm(scoringDataDTO.getTerm());
            creditDTO.setMonthlyPayment(conveyorService.monthlyPayment(scoringDataDTO.getAmount(), rate, scoringDataDTO.getTerm()));
            creditDTO.setRate(rate);
            creditDTO.setPsk(scoringDataDTO.getAmount());
            creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
            creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
            creditDTO.setPaymentSchedule(paymentSchedule);
        }
        return creditDTO;
    }
}

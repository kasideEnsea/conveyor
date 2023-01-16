package com.example.creditapp;

import com.example.creditapp.dto.LoanApplicationRequestDTO;
import com.example.creditapp.dto.LoanOfferDTO;
import com.example.creditapp.service.ConveyorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditappApplicationTests {

    public static final int WANTED_NUMBER_OF_INVOCATIONS = 1;
    @InjectMocks
    ConveyorService conveyorService = mock(ConveyorService.class);

    @Test
    void prescoringValid() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 12, "Иван", "Иванов", "Иванович",
                "ivanov@mail.ru", LocalDate.of(2001, 2, 10), "123456", "1234");
        when(conveyorService.prescoring(dto)).thenReturn(true);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void prescoringWrongAmount() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(5000), 12, "Иван", "Иванов", "Иванович",
                "ivanov@mail.ru", LocalDate.of(2001, 2, 10), "123456", "1234");
        when(conveyorService.prescoring(dto)).thenReturn(false);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void prescoringWrongTerm() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 4, "Иван", "Иванов", "Иванович",
                "ivanov@mail.ru", LocalDate.of(2001, 2, 10), "123456", "1234");
        when(conveyorService.prescoring(dto)).thenReturn(false);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void prescoringEmptyName() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 12, "", "", "",
                "ivanov@mail.ru", LocalDate.of(2001, 2, 10), "123456", "1234");
        when(conveyorService.prescoring(dto)).thenReturn(false);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void prescoringUnder18() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 12, "Иван", "Иванов", "Иванович",
                "ivanov@mail.ru", LocalDate.of(2010, 2, 10), "123456", "1234");
        when(conveyorService.prescoring(dto)).thenReturn(false);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void prescoringWrongPassport() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 12, "Иван", "Иванов", "Иванович",
                "ivanov@mail.ru", LocalDate.of(2001, 2, 10), "", "");
        when(conveyorService.prescoring(dto)).thenReturn(false);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).prescoring(dto);
    }

    @Test
    void getOffers() {
        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO(BigDecimal.valueOf(20000), 12,
                "Иван", "Иванов", "Иванович", "ivanov@mail.ru",
                LocalDate.of(2001, 2, 10), "123456", "1234");
        List<LoanOfferDTO> offerDTOS = new ArrayList<>();
        LoanOfferDTO offer1 = new LoanOfferDTO(BigDecimal.valueOf(20000), BigDecimal.valueOf(120000), 12,
                BigDecimal.valueOf(10108.66), BigDecimal.valueOf(0.02), true, true);
        LoanOfferDTO offer2 = new LoanOfferDTO(BigDecimal.valueOf(20000), BigDecimal.valueOf(20000), 12,
                BigDecimal.valueOf(1721.33), BigDecimal.valueOf(0.06), false, false);
        LoanOfferDTO offer3 = new LoanOfferDTO(BigDecimal.valueOf(20000), BigDecimal.valueOf(120000), 12,
                BigDecimal.valueOf(10163.24), BigDecimal.valueOf(0.03), true, false);
        LoanOfferDTO offer4 = new LoanOfferDTO(BigDecimal.valueOf(20000), BigDecimal.valueOf(20000), 12,
                BigDecimal.valueOf(1712.15), BigDecimal.valueOf(0.05), false, true);
        offerDTOS.add(offer1);
        offerDTOS.add(offer2);
        offerDTOS.add(offer3);
        offerDTOS.add(offer4);
        when(conveyorService.getOffers(dto)).thenReturn(offerDTOS);
        verify(conveyorService, times(WANTED_NUMBER_OF_INVOCATIONS)).getOffers(dto);
    }

}

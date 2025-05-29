package com.kasa.adr.dto;

import com.kasa.adr.model.Address;
import com.kasa.adr.model.Loan;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DefaulterDetails {
    String customerId;
    String name;
    String mobile;
    String email;
    Address address;
    Loan loan1;
    Loan loan2;
    String loanRecallNoticeDate;
    String LRNAmount;
    String loanRecallNoticeNumber;
    String KASAAppointmentDate;
    String appearanceDatePlus1day;
    String invocationRefNo;
}

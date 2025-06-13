package com.kasa.adr.model;

import com.kasa.adr.dto.Documents;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document
public class Case {
    @Id
    String id;

    @DBRef
    User createdBy;
    @DBRef
    User assignedArbitrator;
    @DBRef
    User claimantAdmin;
    //csv
    String customerId;
    String name;
    String mobile;
    String email;
    Address address;
    List<Loan> loans;
    String loanRecallNoticeDate;
    String LRNAmount;
    String loanRecallNoticeNumber;
    String KASAAppointmentDate;
    String product;
    private List<CaseHistory> history = new ArrayList<>();
    String monthYear;
    String status;
    List<Documents> documents;
    String amountRecovered;
    String caseUploadDetails;
    String invocationRefNo;
    String appearanceDatePlus1day;

}

package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Comprehensive loan customer data object containing customer information,
 * loan details, vehicle information, and legal proceedings data.
 */
@Data
@Builder
@Document
public class CaseDetails {
    @Id
    String id;

    @DBRef
    User createdBy;
    @DBRef
    User assignedArbitrator;
    @DBRef
    User claimantAdmin;

    String monthYear;
    String caseUploadDetails;
    // Customer Information
    private String customerId;
    private String customerName;
    private String customerAddress;
    private String customerEmailAddress;
    private String customerContactNumber;

    // Co-applicant Information
    private String coapplicantName;
    private String coapplicantAddress;
    private String coapplicantEmail;
    private String coapplicantMobile;

    // Product Information
    private String product;
    private String assetDescription1;
    private String make;
    private String carRegNumber1;
    private String engineNumber1;
    private String chasisNumber1;

    // Loan 1 Information
    private Integer tenure1;
    private String loanNumber1;
    private LocalDate disbursalDate1;
    private BigDecimal amountFinanceLan1;
    private BigDecimal emiLan1;
    private BigDecimal penaltyCharges1;
    private BigDecimal bounceCharges1;
    private BigDecimal futurePrincipal1;

    // Loan 2 Information
    private String loanNumber2;
    private LocalDate disbursalDate2;
    private BigDecimal amountFinanceLan2;
    private BigDecimal emiLan2;
    private BigDecimal loan2PenaltyCharges;
    private BigDecimal loan2BounceCharges;
    private BigDecimal loan2FuturePrincipal;

    // Loan Recall and Legal Information
    private LocalDate loanRecallNoticeDate;
    private BigDecimal lrnAmount;
    private BigDecimal fcAmount;
    private BigDecimal installmentOverdue;
    private Integer pendingEmis;
    private BigDecimal totalClaimAmount;
    private String claimAmountInWord;
    private String loanRecallNoticeNumber;

    // Legal Proceedings Dates
    private LocalDate kasaAppointmentDate;
    private LocalDate commencementLetterDate;
    private String invocationRefNo;
    private LocalDate arbitrationNoticeDispatchDate;
    private LocalDate appearanceDate;
    private LocalDate statementOfAccountDate;
    private LocalDate interimOrderDate;
    private LocalDate evidenceDate;
    private LocalDate awardDate;


}
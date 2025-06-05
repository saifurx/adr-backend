package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Loan {
    private String loanNumber;
    private String amount;
    private String type;
    private String emi;
    private String tenure;
    private String disbursalDate;
    private String additionalDetails;
    private String carRegNumber;
    private String engineNumber;
    private String assetDescription;
    private String make;
    private String chassisNumber;
    private String penaltyCharges;
    private String bounceCharges;
    private String futurePrincipal;

}

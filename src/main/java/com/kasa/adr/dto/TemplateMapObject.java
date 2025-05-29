package com.kasa.adr.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TemplateMapObject {


    DefaulterDetails defaulter;
    ArbitratorCreateRequest arbitrator;
    ClaimantCreateRequest claimant;


}

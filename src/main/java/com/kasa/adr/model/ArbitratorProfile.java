package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@Document
public class ArbitratorProfile {
    //Arbitrator

    private Address correspondenceAddress;
    private String qualification;
    private String experience;
    private int noOfContestedArbitration;
    private String discloses;
    private String limitation;
    private String category;
    private String specialization;
    private String zuid;
    private String sigImageUrl;

}

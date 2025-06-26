package com.kasa.adr.dto;

import com.kasa.adr.model.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArbitratorCreateRequest {
    private String name;
    private String email;
    private String mobile;
    private Address correspondenceAddress;
    private String qualification;
    private String experience;
    private int noOfContestedArbitration;
    private String discloses;
    private String limitation;
    private String profileImageUrl;
    private String sigImageUrl;
    private String specialization;
    private boolean status;
    private String zuid;
}

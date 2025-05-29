package com.kasa.adr.dto;


import com.kasa.adr.model.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@Document
public class ClaimantCreateRequest {
    // @Id
    //private String id;
    private String name;
    private String institutionType;
    private String branch;
    private String authorizedPersonName;
    private String designation;
    private String email;
    private String mobile;
    private Address address;
    private boolean status;
    private String profileImageUrl;

//    NBFC/Bank Name
//    Name of NBFC/Bank
//            Branch
//    Name of authorized person
//    Designation
//    Email ID
//    Contact No.
//    Address

}

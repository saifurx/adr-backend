package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {


    String line1;
    String line2;
    String pin;
    // Double[] coordinates;
    String type;
    String district;
    String state;
    String country;
    String location;
}
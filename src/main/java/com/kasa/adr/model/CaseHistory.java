package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;


@Data
@Builder
public class CaseHistory {

    Instant date;
    String descriptions;

}

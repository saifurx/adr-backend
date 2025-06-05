package com.kasa.adr.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
public class Holiday {

    @Id
    String id;

    String name;
    Instant date;
    String year;

}

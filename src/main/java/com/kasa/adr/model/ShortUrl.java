package com.kasa.adr.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ShortUrl {
    @Id
    String id;
    @Indexed(unique = true)
    String shortCode;
    String longURL;
    Instant createdAt;
    Instant expireAt;
}

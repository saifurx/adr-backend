package com.kasa.adr;

import com.kasa.adr.dto.RequestShortUrl;
import com.kasa.adr.model.ShortUrl;
import com.kasa.adr.repo.UrlShortenerRepo;
import com.kasa.adr.service.XShortService;
import com.kasa.adr.service.xshort.ShortCodeGenerator;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XShortServiceTest {


}
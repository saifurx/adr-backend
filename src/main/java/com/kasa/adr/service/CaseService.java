package com.kasa.adr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasa.adr.dto.ArbitratorAssign;
import com.kasa.adr.dto.CallDetails;
import com.kasa.adr.model.CaseDetails;
import com.kasa.adr.model.CaseDocuments;
import com.kasa.adr.model.CaseHistoryDetails;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.*;
import com.kasa.adr.service.external.MeetingService;
import com.kasa.adr.service.external.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CaseService {
    Logger logger = LoggerFactory.getLogger(CaseService.class);


    @Autowired
    CaseRepository caseRepository;
    @Autowired
    CaseDocumentsRepo caseDocumentsRepo;
    @Autowired
    CaseHistoryDetailsRepo caseHistoryDetailsRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MeetingService meetingService;

    @Autowired
    TemplateRepo templateRepo;
    @Autowired
    S3Service s3Service;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Map<String, Object> findAllByPage(Pageable pageable, String monthYear, String arbitratorId, String claimantId) {
        logger.info("Received pagination request: {}", pageable);

        Query query = new Query();

        // Adding criteria only if the values are non-empty
        Optional.ofNullable(arbitratorId)
                .filter(StringUtils::hasText)
                .ifPresent(id -> query.addCriteria(Criteria.where("assignedArbitrator.id").is(id)));

        Optional.ofNullable(claimantId)
                .filter(StringUtils::hasText)
                .ifPresent(id -> query.addCriteria(Criteria.where("claimantAdmin.id").is(id)));


        Optional.ofNullable(monthYear)
                .filter(StringUtils::hasText)
                .ifPresent(my -> query.addCriteria(Criteria.where("monthYear").is(my)));

        long totalItems = mongoTemplate.count(query, CaseDetails.class);
        query.with(Sort.by(Sort.Direction.ASC, "customerName"));

        // Applying pagination AFTER counting
        query.with(pageable);
        logger.info("Constructed query: {}", query);

        List<CaseDetails> cases = mongoTemplate.find(query, CaseDetails.class);

        // Preparing response map
        Map<String, Object> result = new HashMap<>();
        result.put("totalElements", totalItems);
        result.put("currentPage", pageable.getPageNumber());
        result.put("pageSize", pageable.getPageSize());
        result.put("totalPages", (int) Math.ceil((double) totalItems / pageable.getPageSize()));
        result.put("content", cases);

        logger.info("Total cases found: {}, Returned cases: {}", totalItems, cases.size());

        return result;
    }

    public Map<String, Object> findAllByPage(Pageable pageable, String srcStr) {

        Query query = new Query();

        query.addCriteria(Criteria.where("customerId").is(srcStr));

        long totalItems = mongoTemplate.count(query, CaseDetails.class);
        // query.with(pageable);

        List<CaseDetails> cases = mongoTemplate.find(query, CaseDetails.class);
        // Prepare the result
        logger.info("totalItems : {}", totalItems);
        Map<String, Object> result = new HashMap<>();
        result.put("totalElements", totalItems);
        result.put("currentPage", pageable.getPageNumber());
        result.put("pageSize", pageable.getPageSize());
        result.put("totalPages", (int) Math.ceil((double) totalItems / pageable.getPageSize()));
        result.put("content", cases);
        return result;
    }

    public Optional<CaseDetails> findById(String caseId) {
        return caseRepository.findById(caseId);
    }

    @Async
    public void scheduleCall(CallDetails callDetails) {
        User updatedBy = userRepository.findById(callDetails.getUserId()).get();
        for (String caseId : callDetails.getCaseIds()) {
            CaseDetails aCase = caseRepository.findById(caseId).get();

            ZonedDateTime zonedDateTime = callDetails.getScheduledTime().atZone(ZoneId.of("Asia/Calcutta"));
            logger.info("Meeting time :" + zonedDateTime);
            // Define the formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a", Locale.US);

            // Format the ZonedDateTime to the desired string
            String formattedDate = zonedDateTime.format(formatter);
            logger.info("Meeting formattedDate :" + formattedDate);
            HttpResponse<String> response = meetingService.scheduleMeeting(formattedDate, aCase.getCustomerEmailAddress(), updatedBy.getArbitratorProfile().getZuid());
            if (response.statusCode() == 200) {
                String body = response.body();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = null;
                try {
                    rootNode = mapper.readTree(body);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                String joinLink = rootNode.path("session").path("joinLink").asText();
                //TODO update case history
            } else {
                logger.info("Unable to schedule Meeting" + aCase.getCustomerId());
            }

        }


    }

    public void assignArbitrator(ArbitratorAssign arbitratorAssign) {
        User arbitrator = userRepository.findById(arbitratorAssign.getAssignedArbitratorId()).get();
        User updatedBy = userRepository.findById(arbitratorAssign.getUserId()).get();

        arbitratorAssign.getCaseIds().forEach(s -> {
            CaseDetails aCase = caseRepository.findById(s).get();
            aCase.setAssignedArbitrator(arbitrator);
            //TODO update case history
            caseRepository.save(aCase);
        });

    }

    public List<String> findCaseIds(String monthYear, String arbitratorId, String claimantId, String status) {
        Query query = new Query();
        if (!arbitratorId.isEmpty()) query.addCriteria(Criteria.where("assignedArbitrator.id").is(arbitratorId));
        if (!claimantId.isEmpty()) query.addCriteria(Criteria.where("claimantAdmin.id").is(claimantId));

        query.addCriteria(Criteria.where("status").is(status));
        query.addCriteria(Criteria.where("monthYear").is(monthYear));
        // Execute the query to fetch only the IDs
        return mongoTemplate.findDistinct(query, "_id", CaseDetails.class, String.class);
    }


    public CaseDetails getOneCase(String caseId) {
        return caseRepository.findById(caseId).orElseThrow();

    }

    public List<CaseDetails> findCaseByCustomerId(String customerId) {
        return caseRepository.findCaseByCustomerId(customerId);
    }


    public List<CaseDetails> getCaseByMobile(String mobile) {
        return caseRepository.findCaseByCustomerContactNumber(mobile);
    }

    public void save(CaseDetails aCase) {
        caseRepository.save(aCase);
    }

    public void updateDocuments(String caseId, MultipartFile multipartFile, String description, String userName) {
        CaseHistoryDetails historyDetails=CaseHistoryDetails.builder().build();
        historyDetails.setCaseId(caseId);
        historyDetails.setDescription(description);
        historyDetails.setCreatedBy(userName);
        historyDetails.setCreatedAt(Instant.now());
        caseHistoryDetailsRepo.save(historyDetails);
        if(!multipartFile.isEmpty()) {
            logger.info("Uploading files");
            String fileName = s3Service.uploadCaseFile(caseId, multipartFile, "cases");
            logger.info("File uploaded successfully: {}", fileName);
            CaseDocuments caseDocuments = CaseDocuments.builder()
                    .caseId(caseId)
                    .description(description)
                    .fileName(fileName)
                    .createdAt(Instant.now())
                    .build();
            caseDocumentsRepo.save(caseDocuments);
        }
    }

    public void sendNotice(String[] caseIds, MultipartFile multipartFile, String templateId, User user) {


    }
}

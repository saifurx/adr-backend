package com.kasa.adr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasa.adr.dto.*;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.Template;
import com.kasa.adr.model.User;
import com.kasa.adr.repo.CaseRepository;
import com.kasa.adr.repo.TemplateRepo;
import com.kasa.adr.repo.UserRepository;
import com.kasa.adr.service.external.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    UserRepository userRepository;

    @Autowired
    MeetingService meetingService;

    @Autowired
    TemplateRepo templateRepo;

    @Autowired
    private MongoTemplate mongoTemplate;



    public Page<Case> casesByPage(Pageable pageable, String monthYear, String arbitratorId, String claimantId, String status) {

        Page<Case> cases;
        if (arbitratorId.isEmpty()) {
            cases = caseRepository.findByClaimantAdmin_IdAndStatus_AndMonthYear(claimantId, status, monthYear, pageable);
        } else {
            cases = caseRepository.findByAssignedArbitrator_IdAndClaimantAdmin_IdAndStatus_AndMonthYear(arbitratorId, claimantId, status, monthYear, pageable);
        }

        return cases;
    }


    //        public Map<String, Object> findAllByPage(Pageable pageable, String monthYear, String arbitratorId, String claimantId, String status) {
//            logger.info("Pageble {}" , pageable);
//        Query query = new Query();
//        if (!arbitratorId.isEmpty()) query.addCriteria(Criteria.where("assignedArbitrator.id").is(arbitratorId));
//        if (!claimantId.isEmpty()) query.addCriteria(Criteria.where("claimantAdmin.id").is(claimantId));
//
//        query.addCriteria(Criteria.where("status").is(status));
//        query.addCriteria(Criteria.where("monthYear").is(monthYear));
//        long totalItems = mongoTemplate.count(query, Case.class);
//        query.with(pageable);
//
//        logger.info("case search query: " + query.toString());
//        List<Case> cases = mongoTemplate.find(query, Case.class);
//        // Prepare the result
//        logger.info(" Count=" + totalItems + " actual count=" + cases.size());
//        Map<String, Object> result = new HashMap<>();
//        result.put("totalElements", totalItems);
//        result.put("currentPage", pageable.getPageNumber());
//        result.put("pageSize", pageable.getPageSize());
//        result.put("totalPages", (int) Math.ceil((double) totalItems / pageable.getPageSize()));
//        result.put("content", cases);
//        return result;
//    }
    public Map<String, Object> findAllByPage(Pageable pageable, String monthYear, String arbitratorId, String claimantId, String status) {
        logger.info("Received pagination request: {}", pageable);

        Query query = new Query();

        // Adding criteria only if the values are non-empty
        Optional.ofNullable(arbitratorId)
                .filter(StringUtils::hasText)
                .ifPresent(id -> query.addCriteria(Criteria.where("assignedArbitrator.id").is(id)));

        Optional.ofNullable(claimantId)
                .filter(StringUtils::hasText)
                .ifPresent(id -> query.addCriteria(Criteria.where("claimantAdmin.id").is(id)));

        Optional.ofNullable(status)
                .filter(StringUtils::hasText)
                .ifPresent(st -> query.addCriteria(Criteria.where("status").is(st)));

        Optional.ofNullable(monthYear)
                .filter(StringUtils::hasText)
                .ifPresent(my -> query.addCriteria(Criteria.where("monthYear").is(my)));

        long totalItems = mongoTemplate.count(query, Case.class);
        query.with(Sort.by(Sort.Direction.ASC, "name"));

        // Applying pagination AFTER counting
        query.with(pageable);
        logger.info("Constructed query: {}", query);

        List<Case> cases = mongoTemplate.find(query, Case.class);

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

        long totalItems = mongoTemplate.count(query, Case.class);
        // query.with(pageable);

        List<Case> cases = mongoTemplate.find(query, Case.class);
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

    public Optional<Case> findById(String caseId) {
        return caseRepository.findById(caseId);
    }

    @Async
    public void scheduleCall(CallDetails callDetails) {
        User updatedBy = userRepository.findById(callDetails.getUserId()).get();
        for (String caseId : callDetails.getCaseIds()) {
            Case aCase = caseRepository.findById(caseId).get();

            ZonedDateTime zonedDateTime = callDetails.getScheduledTime().atZone(ZoneId.of("Asia/Calcutta"));
            logger.info("Meeting time :" + zonedDateTime);
            // Define the formatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a", Locale.US);

            // Format the ZonedDateTime to the desired string
            String formattedDate = zonedDateTime.format(formatter);
            logger.info("Meeting formattedDate :" + formattedDate);
            HttpResponse<String> response = meetingService.scheduleMeeting(formattedDate, aCase.getEmail(), updatedBy.getArbitratorProfile().getZuid());
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
            Case aCase = caseRepository.findById(s).get();
            aCase.setAssignedArbitrator(arbitrator);
        //TODO update case history
            caseRepository.save(aCase);
        });

    }

    public void updateStatus(UpdateStatus updateStatus) {
        User updatedBy = userRepository.findById(updateStatus.getUserId()).get();
        updateStatus.getCaseIds().forEach(s -> {
            Case aCase = caseRepository.findById(s).get();
            aCase.setStatus(updateStatus.getStatus());
            if (!updateStatus.getAmountRecovered().isEmpty())
                aCase.setAmountRecovered(updateStatus.getAmountRecovered());
            if (!updateStatus.getFile().isEmpty()) {
                List<Documents> documents = aCase.getDocuments();
                if (documents == null) {
                    documents = new ArrayList<>();
                }
                documents.add(Documents.builder().fileName(updateStatus.getFile()).description(updateStatus.getDescriptions()).createdAt(Instant.now()).build());
            }
           //TODO update case history
            caseRepository.save(aCase);

        });

    }

    @Async
    public void sendEMail(EmailDetails emailDetails) {
        User updatedBy = userRepository.findById(emailDetails.getUserId()).get();
        Template template = templateRepo.findById(emailDetails.getEmailTemplateId()).get();
        emailDetails.getCaseIds().forEach(s -> {
          //TODO update case history
        });
    }

    public List<String> findCaseIds(String monthYear, String arbitratorId, String claimantId, String status) {
        Query query = new Query();
        if (!arbitratorId.isEmpty()) query.addCriteria(Criteria.where("assignedArbitrator.id").is(arbitratorId));
        if (!claimantId.isEmpty()) query.addCriteria(Criteria.where("claimantAdmin.id").is(claimantId));

        query.addCriteria(Criteria.where("status").is(status));
        query.addCriteria(Criteria.where("monthYear").is(monthYear));
        // Execute the query to fetch only the IDs
        return mongoTemplate.findDistinct(query, "_id", Case.class, String.class);
    }



    public Case getOneCase(String caseId) {
        return caseRepository.findById(caseId).orElseThrow();

    }

    public List<Case> findCaseByCustomerId(String customerId) {
        return caseRepository.findCaseByCustomerId(customerId);
    }


    public List<Case> getCaseByMobile(String mobile) {
        return caseRepository.getCasesByMobile(mobile);
    }

    public void save(Case aCase) {
        caseRepository.save(aCase);
    }
}

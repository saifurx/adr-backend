package com.kasa.adr.repo;

import com.kasa.adr.model.CaseDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends MongoRepository<CaseDetails, String> {

    @Query("{'customerId' : ?0 }")
    List<CaseDetails> findCaseByCustomerId(String customerId);

    @Query("{'caseUploadDetails' : ?0 }")
    List<CaseDetails> findAllByUploadId(String uploadId);

//    Page<CaseDetails> findByAssignedArbitrator_IdAndClaimantAdmin_IdAndStatus_AndMonthYear(
//            String assignedArbitratorId, String claimantAdminId, String status, String monthYear, Pageable pageable);
//
//    Page<CaseDetails> findByClaimantAdmin_IdAndStatus_AndMonthYear(
//            String claimantAdminId, String status, String monthYear, Pageable pageable);

    @Query("{'customerContactNumber' : ?0 }")
    List<CaseDetails> findCaseByCustomerContactNumber(String mobile);
}

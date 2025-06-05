package com.kasa.adr.repo;

import com.kasa.adr.model.Case;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends MongoRepository<Case, String> {

    @Query("{'customerId' : ?0 }")
    List<Case> findCaseByCustomerId(String customerId);

    @Query("{'caseUploadDetails' : ?0 }")
    List<Case> findAllByUploadId(String uploadId);

    Page<Case> findByAssignedArbitrator_IdAndClaimantAdmin_IdAndStatus_AndMonthYear(
            String assignedArbitratorId, String claimantAdminId, String status, String monthYear, Pageable pageable);

    Page<Case> findByClaimantAdmin_IdAndStatus_AndMonthYear(
            String claimantAdminId, String status, String monthYear, Pageable pageable);

    List<Case> getCasesByMobile(String mobile);
}

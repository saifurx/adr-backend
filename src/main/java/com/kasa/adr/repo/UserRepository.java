package com.kasa.adr.repo;

import com.kasa.adr.model.Role;
import com.kasa.adr.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("{apiKey: { $exists: true } }")
    List<User> findAllUsersWithAPIKey();

    Optional<User> findByApiKey(String apiKey);

    @Query("{'userType' : ?0}")
    List<User> findAllByType(String userType);

    @Query("{'userType' : ?0, 'status' : ?1}")
    List<User> findAllByTypeAndStatus(String type, boolean status);

    long countByUserType(String hotel);

    @Query(value = "{'userType' : ?0, 'status' : ?1}", count = true)
    long countByUserTypeAndStatus(String hotel, boolean status);

    @Query("{'userType' : ?0, 'claimantAdminUserId' : ?1}")
    List<User> findAllByTypeAndClaimantId(String userType, String claimantAdminUserId);

    @Query("{'userType' : ?0, 'role' : ?1}")
    List<User> findAllClaimantAdmins(String userType, Role role);

    @Query("{'userType' : ?0}")
    Page<User> findAllByTypeAndPageable(String userType, Pageable pageable);


}
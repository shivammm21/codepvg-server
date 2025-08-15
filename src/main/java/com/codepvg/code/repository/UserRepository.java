package com.codepvg.code.repository;

import com.codepvg.code.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByStatus(User.UserStatus status);
    
    List<User> findByRole(User.Role role);
    
    @Query(value = "{}", sort = "{ 'totalSolved': -1, 'totalSubmissions': 1 }")
    List<User> findAllOrderByLeaderboard();
    
    @Query("{ 'status': 'APPROVED', 'role': 'STUDENT' }")
    List<User> findApprovedStudents();
}
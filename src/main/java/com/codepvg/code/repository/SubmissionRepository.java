package com.codepvg.code.repository;

import com.codepvg.code.model.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {
    
    List<Submission> findByUserId(String userId);
    
    List<Submission> findByProblemId(String problemId);
    
    List<Submission> findByUserIdAndProblemId(String userId, String problemId);
    
    List<Submission> findByStatus(Submission.SubmissionStatus status);
    
    @Query(value = "{ 'userId': ?0 }", sort = "{ 'submittedAt': -1 }")
    List<Submission> findByUserIdOrderBySubmittedAtDesc(String userId);
    
    @Query(value = "{ 'problemId': ?0 }", sort = "{ 'submittedAt': -1 }")
    List<Submission> findByProblemIdOrderBySubmittedAtDesc(String problemId);
    
    @Query("{ 'userId': ?0, 'status': 'ACCEPTED' }")
    List<Submission> findAcceptedSubmissionsByUserId(String userId);
}
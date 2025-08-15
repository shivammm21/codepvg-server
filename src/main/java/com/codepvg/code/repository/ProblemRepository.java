package com.codepvg.code.repository;

import com.codepvg.code.model.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends MongoRepository<Problem, String> {
    
    List<Problem> findByDifficulty(Problem.Difficulty difficulty);
    
    List<Problem> findByTagsContaining(String tag);
    
    List<Problem> findByCreatedBy(String createdBy);
    
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Problem> findByTitleContainingIgnoreCase(String title);
    
    @Query(value = "{}", sort = "{ 'createdAt': -1 }")
    List<Problem> findAllOrderByCreatedAtDesc();
    
    Optional<Problem> findByTitle(String title);
    
    List<Problem> findByTopicsContaining(String topic);
}
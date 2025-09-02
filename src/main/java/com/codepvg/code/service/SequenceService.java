package com.codepvg.code.service;

import com.codepvg.code.model.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

    @Autowired
    private MongoOperations mongoOperations;

    public long getNextSequence(String key) {
        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        DatabaseSequence seq = mongoOperations.findAndModify(query, update, options, DatabaseSequence.class);
        return seq != null ? seq.getSeq() : 1L;
    }
}

package com.codepvg.code.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
public class DatabaseSequence {
    @Id
    private String id; // sequence key, e.g., "problem_number_seq"

    private long seq;

    public DatabaseSequence() {}

    public DatabaseSequence(String id, long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getSeq() { return seq; }
    public void setSeq(long seq) { this.seq = seq; }
}

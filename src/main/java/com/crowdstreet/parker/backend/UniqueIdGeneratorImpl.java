package com.crowdstreet.parker.backend;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UniqueIdGeneratorImpl implements  IUniqueIdGenerator {

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}

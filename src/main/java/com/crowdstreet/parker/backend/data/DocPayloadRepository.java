package com.crowdstreet.parker.backend.data;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocPayloadRepository  extends CrudRepository<DocPayload, String> {


}

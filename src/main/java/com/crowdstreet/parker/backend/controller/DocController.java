package com.crowdstreet.parker.backend.controller;

import com.crowdstreet.parker.backend.IUniqueIdGenerator;
import com.crowdstreet.parker.backend.UniqueIdGeneratorImpl;
import com.crowdstreet.parker.backend.data.DocPayload;
import com.crowdstreet.parker.backend.data.DocPayloadRepository;
import com.crowdstreet.parker.backend.thirdparty.IThirdPartyService;
import com.crowdstreet.parker.backend.thirdparty.ThirdPartyRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

// Third party exceptions caught in ThirdPartyException controller
// Database exceptions caught in DataStoreException controller
@RestController
public class DocController {

    Logger logger = LoggerFactory.getLogger(DocController.class);

    public static final String CALLBACK_PATH = "callback";

    IThirdPartyService thirdPartyService;
    DocPayloadRepository repository;
    IUniqueIdGenerator idGenerator;

    @Autowired
    public DocController(IThirdPartyService thirdPartyService, DocPayloadRepository repository, IUniqueIdGenerator idGenerator) {
        this.thirdPartyService = thirdPartyService;
        this.repository = repository;
        this.idGenerator = idGenerator;
    }

    protected String generateThirdPartyCallback(String uniqueId) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        return baseUrl + "/" + DocController.CALLBACK_PATH + "/" + uniqueId;
    }


    @PostMapping(path = "/request", consumes = "application/json")
    public String request(@RequestBody DocPayloadRequestDto request) throws ThirdPartyRequestException {

        logger.debug("received payload request: " + request.body);
        String uniqueId = idGenerator.generateUniqueId();
        this.thirdPartyService.requestDocPayload(request.body, this.generateThirdPartyCallback(uniqueId));
        this.repository.save(new DocPayload(uniqueId, request.body));
        return uniqueId;
    }

    @PostMapping(path = DocController.CALLBACK_PATH + "/{id}")
    public void post(@PathVariable String id, @RequestBody String status, HttpServletResponse response) {

        logger.debug("received post callback id: " + id + " status: " + status);

        Optional<DocPayload> payloadStatus = this.repository.findById(id);
        if (payloadStatus.isPresent()) {
            payloadStatus.get().status = status;
            this.repository.save(payloadStatus.get());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @PutMapping(path = DocController.CALLBACK_PATH + "/{id}")
    public void put(@PathVariable String id, @RequestBody DocPayloadPutDto putDto, HttpServletResponse response) {

        logger.debug("received put callback id: " + id + " put: " + putDto);

        Optional<DocPayload> payloadStatus = this.repository.findById(id);
        if (payloadStatus.isPresent()) {
            payloadStatus.get().status = putDto.status;
            payloadStatus.get().detail = putDto.detail;
            this.repository.save(payloadStatus.get());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            logger.error("no status for id: " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping(path = "/status/{id}")
    public DocPayloadStatusResponseDto get(@PathVariable String id, HttpServletResponse response) {

        logger.debug("received request for stats: " + id);

        Optional<DocPayload> payloadStatus = this.repository.findById(id);
        if (payloadStatus.isPresent()) {
            return new DocPayloadStatusResponseDto(payloadStatus.get());
        } else {
            logger.error("no status exists for id: " + id);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }
}

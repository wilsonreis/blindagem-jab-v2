package com.santander.kpv.controllers;


import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.services.sender.SendReplyServiceV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SendReplyController {
    @Autowired
    private SendReplyServiceV1 sendReplyService;
    public SendReplyController(SendReplyServiceV1 sendReplyService) {
        this.sendReplyService = sendReplyService;
    }
    @PostMapping(
            value = "/sendReplyXML",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> blindagem(@RequestBody String xml) {
        try {
            log.info("blindagemFinalV1 - mensagem recebida = [{}]", xml);
            return new ResponseEntity<>(sendReplyService.sendSyncReply( xml), HttpStatus.OK);
        } catch (JmsException ex) {
            log.info("public String blindagem(@RequestBody String message)  [{}]", ex.getMessage());
            throw new MyRuntimeException(ex);
        }
    }
}

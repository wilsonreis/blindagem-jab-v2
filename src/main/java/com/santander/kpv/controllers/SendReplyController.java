package com.santander.kpv.controllers;


import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.services.sender.SendReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;

@RestController
@Slf4j
public class SendReplyController {
    @Autowired
    private SendReplyService sendReplyService;
    public SendReplyController(SendReplyService sendReplyService) {
        this.sendReplyService = sendReplyService;
    }
    @PostMapping(
            value = "/sendReplyXML",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> blindagem(@RequestBody String message) {
        try {
            log.info("blindagemFinalV1 - mensagem recebida = [{}]", message);
            return new ResponseEntity<>(sendReplyService.sendSyncReply(message), HttpStatus.OK);
        } catch (JmsException ex) {
            log.info("public String blindagem(@RequestBody String message)  [{}]", ex.getMessage());
            throw new MyRuntimeException(ex);
        }
    }
}

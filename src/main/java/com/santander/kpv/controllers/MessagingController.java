package com.santander.kpv.controllers;

import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.services.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@RestController
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/messagingXML")
    ResponseEntity<String> blindagem(@RequestParam(value = "xml") String xml) throws JMSException {
        try {
            String s = messagingService.sendAndReceiveMessage(xml);
            return new ResponseEntity<>(s, HttpStatus.OK);
        } catch (JmsException | jakarta.jms.JMSException ex) {
            throw new MyRuntimeException(ex);
        }
    }
}

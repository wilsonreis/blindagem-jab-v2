package com.santander.kpv.controllers;

import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.services.MessagingService;
import com.santander.kpv.services.MessagingV2Service;
import com.santander.kpv.services.MessagingV3Service;
import com.santander.kpv.utils.IBMQueueBrowserUtils;
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
    @Autowired
    private MessagingV2Service messagingV2Service;
    @Autowired
    private MessagingV3Service messagingV3Service;
    public MessagingController(MessagingService messagingService) {
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
    @GetMapping("/messagingV2XML")
    public ResponseEntity<String> sendAndReceive(@RequestParam(value = "xml") String xml) throws jakarta.jms.JMSException {
        String response = messagingV2Service.sendAndReceiveMessage(xml);
        if (response != null) {
            return ResponseEntity.ok("Received response: " + response);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("No response received");
        }
    }
    @GetMapping("/messagingV3XML")
    public ResponseEntity<String> sendAndReceiveV3(@RequestParam(value = "xml") String xml) throws jakarta.jms.JMSException {
        IBMQueueBrowserUtils.rastreiaFila2("DEV.QUEUE.2");
        String response = messagingV3Service.sendAndReceiveMessage(xml);
        if (response != null) {
            return ResponseEntity.ok("Received response: " + response);
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("No response received");
        }
    }
}

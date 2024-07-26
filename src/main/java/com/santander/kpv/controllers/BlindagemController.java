package com.santander.kpv.controllers;
import com.santander.kpv.exceptions.MyRuntimeException;
import com.santander.kpv.services.sender.JabService;
import com.santander.kpv.utils.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Generated
public class BlindagemController {

    @Autowired
    private JabService jabService;

    public BlindagemController(JabService jabService) {
        this.jabService = jabService;
    }

    @PostMapping(
            value = "/blindagemFinalV1",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> blindagem(@RequestBody String message) {
        try {
            log.info("blindagemFinalV1 - mensagem recebida = [{}]", message);
            return new ResponseEntity<>(jabService.enviaRecebeMensagens(message), HttpStatus.OK);
        } catch (JmsException ex) {
            log.info("public String blindagem(@RequestBody String message)  [{}]", ex.getMessage());
            throw new MyRuntimeException(ex);
        }
    }
}


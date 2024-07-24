package com.santander.kpv.controllers;

import com.santander.kpv.services.sender.JabService;
import com.santander.kpv.services.sender.JabServiceRefatorado;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@RestController
public class JabController {

    private JabService jabService;
    private JabServiceRefatorado jabServiceRefatorado;

    public JabController(JabService enviaMensagem, JabServiceRefatorado jabServiceRefatorado) {
        this.jabService = enviaMensagem;
        this.jabServiceRefatorado = jabServiceRefatorado;
    }

    @GetMapping("/jabXML")
    String blindagem(@RequestParam(value = "xml") String xml) throws JMSException {
        try {
            return jabService.enviaRecebeMensagens(xml);
        } catch (JmsException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @GetMapping("/jabRefatoradoXML")
    String blindagemRefatorada(@RequestParam(value = "xml") String xml) throws JMSException {
        try {
            return jabServiceRefatorado.enviaRecebeMensagens(xml);
        } catch (JmsException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }
}

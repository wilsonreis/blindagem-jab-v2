package com.santander.kpv.controllers;

import com.santander.kpv.services.sender.JabService;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@RestController
public class JabController {

    private JabService enviaMensagem;

    public JabController(JabService enviaMensagem) {
        this.enviaMensagem = enviaMensagem;
    }

    @GetMapping("/jabaXML")
    String blindagem(@RequestParam(value = "xml") String xml) throws JMSException {
        try {
            return enviaMensagem.enviaRecebeMensagens(xml);
        } catch (JmsException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }
}

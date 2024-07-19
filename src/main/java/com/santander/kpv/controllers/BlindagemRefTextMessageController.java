package com.santander.kpv.controllers;

import com.santander.kpv.services.sender.SendReceiverTextMessageService;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;

@RestController
public class BlindagemRefTextMessageController {

    private SendReceiverTextMessageService enviaMensagem;

    public BlindagemRefTextMessageController(SendReceiverTextMessageService enviaMensagem) {
        this.enviaMensagem = enviaMensagem;
    }

    @GetMapping("/blindagemTextMessage")
    String blindagem(@RequestParam(value = "cpf") String cpf) throws JMSException {
        try {
            return enviaMensagem.enviaRecebeMensagens(cpf);
        } catch (JmsException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }

    @GetMapping("/blindagemTextMessageSfh")
    String blindagemSFH1(@RequestParam(value = "cpf") String cpf, @RequestParam(value = "sfh") String sfh) {
        try {
            return enviaMensagem.enviaRecebeMensagensSFH(cpf, sfh);
        } catch (JmsException | JMSException ex) {
            ex.printStackTrace();
            return "FAIL";
        }
    }
}

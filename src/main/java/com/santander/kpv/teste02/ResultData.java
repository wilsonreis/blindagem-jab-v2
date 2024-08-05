package com.santander.kpv.teste02;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultData {
    private KColl kColl;

    @XmlElement(name = "kColl")
    public KColl getKColl() {
        return kColl;
    }

    public void setKColl(KColl kColl) {
        this.kColl = kColl;
    }
}

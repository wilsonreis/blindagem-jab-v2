package com.santander.kpv.teste02;

import jakarta.xml.bind.annotation.XmlElement;

public class ResultDataV1 {
    private KColl kColl;

    @XmlElement(name = "kColl")
    public KColl getKColl() {
        return kColl;
    }

    public void setKColl(KColl kColl) {
        this.kColl = kColl;
    }
}

package com.santander.kpv.teste02;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class KCollV0 {
    private String id;
    private KCollV0[] kColls;
    private IColl iColl;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "kColl")
    public KCollV0[] getKColls() {
        return kColls;
    }

    public void setKColls(KCollV0[] kColls) {
        this.kColls = kColls;
    }

    @XmlElement(name = "iColl")
    public IColl getIColl() {
        return iColl;
    }

    public void setIColl(IColl iColl) {
        this.iColl = iColl;
    }
}

package com.santander.kpv.teste02;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IColl {
    private String id;
    private KColl kColl;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "kColl")
    public KColl getKColl() {
        return kColl;
    }

    public void setKColl(KColl kColl) {
        this.kColl = kColl;
    }
}

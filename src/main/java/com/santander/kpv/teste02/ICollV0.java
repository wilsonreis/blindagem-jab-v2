package com.santander.kpv.teste02;

import jakarta.xml.bind.annotation.XmlAttribute;

public class ICollV0 {
    private String id;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

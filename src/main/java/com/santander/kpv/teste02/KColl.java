package com.santander.kpv.teste02;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KColl {
    private String id;
    private KColl[] kColls;
    private IColl iColl;
    private List<Field> fields;

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "kColl")
    public KColl[] getKColls() {
        return kColls;
    }

    public void setKColls(KColl[] kColls) {
        this.kColls = kColls;
    }

    @XmlElement(name = "iColl")
    public IColl getIColl() {
        return iColl;
    }

    public void setIColl(IColl iColl) {
        this.iColl = iColl;
    }

    @XmlElement(name = "field")
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}

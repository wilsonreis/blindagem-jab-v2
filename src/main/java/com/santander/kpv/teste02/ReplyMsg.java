package com.santander.kpv.teste02;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "replyMsg")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplyMsg {
    private String dseStatus;
    private ResultData resultData;

    @XmlElement(name = "dse_status")
    public String getDseStatus() {
        return dseStatus;
    }

    public void setDseStatus(String dseStatus) {
        this.dseStatus = dseStatus;
    }

    @XmlElement(name = "dse_resultData")
    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }
}

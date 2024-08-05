package com.santander.kpv.teste02;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "replyMsg")
public class ReplyMsgV1 {
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

package com.santander.kpv.teste02;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;

public class MainV4 {
    public static void main(String[] args) throws JAXBException {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<replyMsg>\n" +
                "    <dse_status>OK</dse_status>\n" +
                "    <dse_resultData>\n" +
                "        <kColl id=\"outputData\">\n" +
                "            <kColl id=\"errores\"/>\n" +
                "            <kColl id=\"avisos\"/>\n" +
                "            <iColl id=\"listDataSHF8\">\n" +
                "                <kColl>\n" +
                "                    <field id=\"DC_FFORMATO\" value=\"SHMPF8\"/>\n" +
                "                    <field id=\"NRCPFCN\" value=\"05425330812\"/>\n" +
                "                    <field id=\"PEOFFBSE2\" value=\"\"/>\n" +
                "                    <field id=\"CODERRO\" value=\"00\"/>\n" +
                "                    <field id=\"MSGERRO\" value=\"\"/>\n" +
                "                </kColl>\n" +
                "            </iColl>\n" +
                "        </kColl>\n" +
                "    </dse_resultData>\n" +
                "</replyMsg>";

        JAXBContext jaxbContext = JAXBContext.newInstance(ReplyMsg.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        ReplyMsg replyMsg = (ReplyMsg) unmarshaller.unmarshal(reader);

        System.out.println("DSE Status: " + replyMsg.getDseStatus());

        ResultData resultData = replyMsg.getResultData();
        KColl kColl = resultData.getKColl();
        System.out.println("Output Data ID: " + kColl.getId());

        for (KColl subKColl : kColl.getKColls()) {
            System.out.println("Sub KColl ID: " + subKColl.getId());
        }

        IColl iColl = kColl.getIColl();
        System.out.println("IColl ID: " + iColl.getId());

        KColl innerKColl = iColl.getKColl();
        for (Field field : innerKColl.getFields()) {
            System.out.println("Field ID: " + field.getId() + ", Value: " + field.getValue());
        }

        // Convert the ReplyMsg object to JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(replyMsg);
            System.out.println("JSON Output: ");
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

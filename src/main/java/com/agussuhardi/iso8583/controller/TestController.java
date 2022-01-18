package com.agussuhardi.iso8583.controller;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.q2.iso.QMUX;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private final QMUX qmux;

    public TestController(QMUX qmux) {
        this.qmux = qmux;
    }

    @GetMapping
    public Map<String, String> signOn() {
        Map<String, String> hasil = new HashMap<>();

        try {
            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setMTI("0800");
            isoMsg.set(7, new SimpleDateFormat("MMddhhmmss").format(new Date())); //Transmission Date  and Time MMDDhhmmss
            isoMsg.set(11, "000007"); //System Trace Audit Number
            isoMsg.set(70, "001"); //Network Management Information Code

            ISOMsg isoResponse = qmux.request(isoMsg, 20 * 1000);

            if (isoResponse == null) {
                // buat message reversal
                // kirim reversal
                // kalau masih timeout, ulangi 2x lagi reversal

                hasil.put("success", "false");
                hasil.put("error", "timeout");
                return hasil;
            }

            String response = new String(isoResponse.pack());

            hasil.put("success", "true");
            hasil.put("response_code", isoResponse.getString(39));
            hasil.put("raw_message", response);
        } catch (ISOException e) {
            e.printStackTrace();
        }

        return hasil;
    }
}

package com.agussuhardi.iso8583.iso;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.BaseChannel;

import java.io.IOException;

public class MyChannel extends BaseChannel {
    @Override
    protected void sendMessageLength(int len) throws IOException {
        // TODO Auto-generated method stub
        System.out.println("Length of outgoing message [int] = " + len);
        String hex = Integer.toHexString(len);
        String lengthMsg = StringUtils.leftPad(hex, 4, "0");
        System.out.println("Length of outgoing message [hex] = " + lengthMsg);
        serverOut.write(getBytes(lengthMsg));
    }

    private byte[] getBytes(String hexStr) {
        char[] chars = new char[2];
        byte[] b = new byte[2];
        int j = 0;

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);

            int decimal = Integer.parseInt(str, 16);
            chars[j] = (char) decimal;

            b[j] = (byte) chars[j];
            j++;
        }
        return b;
    }

    @Override
    protected int getMessageLength() throws IOException {
        // TODO Auto-generated method stub
        int l = 0;
        byte[] b = new byte[2];
        while (l == 0) {
            serverIn.readFully(b, 0, 2);
            l = getLength(b);
            System.out.println(
                    "Length of incoming message [hex] = " + StringUtils.leftPad(Integer.toHexString(l), 4, "0"));
            System.out.println("Length of incoming message [int] = " + l);

            if (l == 0) {
                serverOut.write(b);
                serverOut.flush();
            }
        }
        return l;
    }

    private int getLength(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return Integer.parseInt(builder.toString(), 16);
    }
}

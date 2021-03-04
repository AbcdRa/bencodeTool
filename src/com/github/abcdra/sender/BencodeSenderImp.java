package com.github.abcdra.sender;

import com.github.abcdra.types.BencodeType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class BencodeSenderImp implements BencodeSender{
    private String host;
    private int port;
    private String src;

    public BencodeSenderImp(String src) {
        this.src = src;
        divideSrc(src);
    }

    private void divideSrc(String src) {
        String[] splits = src.split(":",2);
        host = splits[0];
        port = Integer.parseInt(splits[1]);
    }

    @Override
    public void send(BencodeType content) {
        try {
            Socket socket = new Socket(host,port);
            socket.setSoTimeout(10000);
            try (PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true)) {
                // Отправка данных на сервер
                toServer.println(content.value());
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void setSource(String source) {
        src = source;
        divideSrc(src);
    }
}

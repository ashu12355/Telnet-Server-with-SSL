package com.telnetServer.telnetServer.telnet;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoHandlerAdapter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelnetServerHandler extends IoHandlerAdapter {

    private final ExecutorService serverMessageExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.write("Welcome to the Secure Telnet Server! Type 'exit' to quit.");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String messageFromClient = (String) message;
        System.out.println("Client: " + messageFromClient);

        if ("exit".equalsIgnoreCase(messageFromClient)) {
            session.write("Goodbye!");
            session.closeNow();
        } else {
            session.write("Client: " + messageFromClient);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}

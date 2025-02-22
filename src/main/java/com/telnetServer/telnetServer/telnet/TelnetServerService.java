package com.telnetServer.telnetServer.telnet;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

@Service
public class TelnetServerService {

    private static final int TELNET_PORT = 8081;
    private IoAcceptor acceptor;
    private final ExecutorService inputExecutor = Executors.newSingleThreadExecutor();

    public void startTelnetServer() {
        try {
            acceptor = new NioSocketAcceptor();
            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

            // Load SSL Context
            SSLContext sslContext = createSSLContext();
            if (sslContext != null) {
                SslFilter sslFilter = new SslFilter(sslContext);
                sslFilter.setUseClientMode(false);
                chain.addFirst("sslFilter", sslFilter);
            }

            // Add protocol codec for Telnet
            ProtocolCodecFactory codecFactory = new TextLineCodecFactory();
            chain.addLast("codec", new ProtocolCodecFilter(codecFactory));

            acceptor.setHandler(new TelnetServerHandler());
            acceptor.bind(new InetSocketAddress("0.0.0.0", TELNET_PORT));

            System.out.println("Telnet server started on port " + TELNET_PORT);
            startServerInputThread();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Properly handle server input for clean messaging
    private void startServerInputThread() {
        inputExecutor.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    System.out.print("> ");
                    System.out.flush();

                    String serverMessage = reader.readLine().trim();
                    if (!serverMessage.isEmpty()) {
                        sendMessageToAllClients(serverMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Ensure every connected client gets the message reliably
    private synchronized void sendMessageToAllClients(String message) {
        Collection<IoSession> sessions = acceptor.getManagedSessions().values();

        if (sessions.isEmpty()) {
            return;
        }

        for (IoSession session : sessions) {
            if (session != null && session.isConnected()) {
                session.write("Server: " + message);
            }
        }
    }

    private SSLContext createSSLContext() {
        try {
            String keystoreFile = "src/main/resources/keystore.jks";
            String keystorePassword = "changeit";
            String keyPassword = "changeit";

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, keyPassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopTelnetServer() {
        if (acceptor != null) {
            acceptor.unbind();
            acceptor.dispose();
        }
        if (inputExecutor != null) {
            inputExecutor.shutdown();
        }
    }
}

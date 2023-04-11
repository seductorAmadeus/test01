package org.example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final int SERVER_PORT = 9000;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramChannel serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(SERVER_PORT));

        ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            receiveBuffer.clear();
            InetSocketAddress clientAddress = (InetSocketAddress) serverChannel.receive(receiveBuffer);
            if (clientAddress != null) {
                receiveBuffer.flip();
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new ByteArrayInputStream(receiveBuffer.array()));
                System.out.println("Received object from client: " + objectInputStream.readObject());
            }

            // Read commands from the server console
            // clientAddress != null && serverChannel.send(sendBuffer, clientAddress) --  not necessary, just for demonstration purposes
            while (System.in.available() > 0 && clientAddress != null) {
                String serverCommand = scanner.nextLine();
                System.out.println("Received command from server: " + serverCommand);
                String response = "Command received: " + serverCommand;
                sendBuffer.clear();
                sendBuffer.put(response.getBytes());
                sendBuffer.flip();
                serverChannel.send(sendBuffer, clientAddress);
            }

            if (clientAddress != null) {
                String response = "MSG from server";
                sendBuffer.clear();
                sendBuffer.put(response.getBytes());
                sendBuffer.flip();
                serverChannel.send(sendBuffer, clientAddress);
            }
        }
    }
}

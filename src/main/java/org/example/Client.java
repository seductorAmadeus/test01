package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    private static final int BUFFER_SIZE = 1024;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9000;

    public static void main(String[] args) throws IOException {
        DatagramChannel clientChannel = DatagramChannel.open();
        clientChannel.configureBlocking(false);
        InetSocketAddress serverAddress = new InetSocketAddress(SERVER_HOST, SERVER_PORT);

        TimerTask sendTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Person person = new Person("Alice", 25);
                    ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                    ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream);
                    objOutStream.writeObject(person);
                    byte[] requestData = byteOutStream.toByteArray();
                    ByteBuffer sendBuffer = ByteBuffer.wrap(requestData);
                    clientChannel.send(sendBuffer, serverAddress);
                    System.out.println("Sent request to server: " + person);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(sendTask, 0, 2000);

        ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (true) {
            receiveBuffer.clear();
            InetSocketAddress serverAddressFromResponse = (InetSocketAddress) clientChannel.receive(receiveBuffer);
            if (serverAddressFromResponse != null) {
                receiveBuffer.flip();
                byte[] responseData = new byte[receiveBuffer.limit()];
                receiveBuffer.get(responseData);
                String response = new String(responseData);
                System.out.println("Received response from server: " + response);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Person implements Serializable {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

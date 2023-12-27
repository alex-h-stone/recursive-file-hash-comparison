package dev.alexhstone.storage;

import dev.alexhstone.config.ApplicationConfiguration;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigInteger;

public class ActiveMQPublisher {

    private final String brokerURL;
    private ActiveMQConnectionFactory connectionFactory;

    public ActiveMQPublisher() {
        brokerURL = ApplicationConfiguration.getActiveMQBrokerURL();
    }

    public void initialise(){
        connectionFactory = new ActiveMQConnectionFactory(brokerURL);

    }

    public static void main(String[] args){
        ActiveMQPublisher activeMQPublisher = new ActiveMQPublisher();
        activeMQPublisher.initialise();
        activeMQPublisher.doStuff();
    }

    public void doStuff() {
        try {
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession( Session.CLIENT_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("testQueue");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);

            // Create a messages
            String text = "Hello from ActiveMQ!";
            TextMessage message = session.createTextMessage(text);

            // Tell the producer to send the message
            System.out.println("Sent message: " + text);
            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public boolean publish(String workItemAsJson) {
        return false;
    }

    public String poll() {
        return null;
    }

    public BigInteger getQueueSize() {
        return BigInteger.ZERO;
    }
}


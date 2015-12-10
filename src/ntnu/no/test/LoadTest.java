package ntnu.no.test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

public class LoadTest {

	private static final String EXCHANGE_NAME = "trainlogs";
	private static final String TOPIC ="sync";
	private final static String HOST = "118.138.244.250";

	
	public static void main(String[] args){
		
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("ntnutrains");
        factory.setUsername("trainloger");
        Connection connection;
        Gson g = new Gson();
        TrainInfo ti = new TrainInfo(1, 10);
        ti.setDirection(TrainDirection.COUNTERCLOCKWISE);
        ti.setPosition(10);
        ti.setSpeed(11);
        TrainStatus ts = new TrainStatus(TrainState.RUNNING);
        ts.setFromStation("Lade");
        ts.setToStation("Solsiden");
        ti.setStatus(ts);
		try {
			connection = factory.newConnection();
		
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        for(int j = 0; j < 100; j++){
	        for (int i = 0; i < 10*j; i++) {
	        	ti.setTimeStamp(System.currentTimeMillis());
	            String msg = g.toJson(ti);
	            channel.basicPublish(EXCHANGE_NAME, TOPIC, null, msg.getBytes() );
			}
        }
        channel.close();
        connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}

package ntnu.no.trainlogger.delta;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ntnu.no.trainlogger.enums.SleeperType;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainEventType;
import ntnu.no.trainlogger.enums.TrainState;

public class SleeperInfo {
	
	private int id;
	private SleeperType type;
	private int frontSleeperId;
	private int backSleeperId;
	
	public SleeperInfo(int id, SleeperType type, int frontSleeperId, int backSleeperID) {
		this.id = id;
		this.type = type;
		this.frontSleeperId = frontSleeperId;
		this.backSleeperId = backSleeperID;
	}

	public int getId() {
		return id;
	}

	public SleeperType getType() {
		return type;
	}

	public int getFrontSleeperId() {
		return frontSleeperId;
	}

	public int getBackSleeperId() {
		return backSleeperId;
	}
	
	public static void main(String[] args) {
		Gson g = new Gson();
		TrainInfo ti = new TrainInfo(1, 10);
		TrainStatus ts = new TrainStatus(TrainState.RUNNING);
		ts.setFromStation("Lade");
		ts.setToStation("Solsiden");
		ti.setStatus(ts);
		ti.setSpeed(5);
		ti.setDirection(TrainDirection.CLOCKWISE);
		ti.setPosition(10);
		String s = g.toJson(ti);
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		Gson g1 = gb.create();
		TrainInfoUpdate tu = new TrainInfoUpdate(2, System.currentTimeMillis(), 0, TrainEventType.DIRECTION);
		tu.setEvent(TrainEventType.SPEED);
		tu.setSpeed(10);
		String s1 = g1.toJson(tu);
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.100");
        factory.setPassword("broker123");
        factory.setUsername("alexander");
        Connection connection;
		try {
			connection = factory.newConnection();
		
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("trains", "topic");
        
        	channel.basicPublish("trains", "size", null, s.getBytes() );			
        channel.close();
        connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

}

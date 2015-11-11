package ntnu.no.trainlogger.trainsimulator;

import java.util.HashMap;
import java.util.Random;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.rabbitamqp.util.Message;
import ntnu.no.trainlogger.delta.TrainInfo;

public class TrainSimulator extends Block {
	
	private TrainInfo traininfo;
	private Random r;
	private String topic;
	private float speed;
	private boolean running;
	
	public void init(int i){
		r = new Random();
		traininfo = new TrainInfo(i, r.nextInt(19) + 1);
		running = true;
	}

	public void generateMovment(){
		int i = r.nextInt(2);
		int f = traininfo.getPosition();
		switch(i){
			case 0: 
				traininfo.setPosition((int) Math.floor(f + (speed/2)));
				break;
			case 1: 
				speed = r.nextFloat() * 10;
				traininfo.setSpeed(speed);
				traininfo.setPosition((int) Math.floor(f + (speed/2)));
				break;
			case 2:
				speed = 0;
				traininfo.setSpeed(speed);
				break;
			default:
				traininfo.setPosition((int) Math.floor(f + (speed/2)));
				break;
		}
	}

	public TrainInfo getTrainInfo() {
		return running ? traininfo : null;
	}

	public void startStop() {
		running = !running;
	}
	
	public boolean running(){
		return running;
	}
}

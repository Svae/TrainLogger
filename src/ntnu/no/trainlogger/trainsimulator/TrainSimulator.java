package ntnu.no.trainlogger.trainsimulator;

import java.util.Random;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

public class TrainSimulator extends Block {
	private TrainInfo traininfo;
	private Random r;
	private float speed;
	private boolean running;
	private String[] stations = { "Munkegata", "Solsiden", "Gløshaugen", "Lade" };
	public int timeBetweenNewTrainInfo;
	public int timeBetweenChangeProperty;
	
	public static String getAlias(int i){
		return String.valueOf(i);
	}
	
	
	
	public void init(int i) {
		r = new Random();
		traininfo = new TrainInfo(i, r.nextInt(19) + 1);
		TrainStatus ts = new TrainStatus(TrainState.RUNNING);
		ts.setFromStation(stations[r.nextInt(4)]);
		ts.setToStation(stations[r.nextInt(4)]);
		traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
		traininfo.setStatus(ts);
		traininfo.setPosition(0);
		running = true;
		speed = r.nextFloat() * 10;
		traininfo.setSpeed(speed);
	}

	public void changeProperty() {
		int i = (int) Math.round(r.nextGaussian() + 2);
		switch (i) {
		case 0:
			traininfo.getStatus().setToStation(stations[r.nextInt(4)]);
			break;
		case 4:
			traininfo.getStatus().setFromStation(stations[r.nextInt(4)]);
			break;
		case 3:
			traininfo.getStatus().setState(TrainState.RUNNING);
			speed = r.nextFloat() * 10;
			traininfo.setSpeed(speed);
			break;
		case 2: 
			break;
		case 5:
			speed = 0;
			traininfo.setSpeed(speed);
			traininfo.getStatus().setState(TrainState.STOPPED);
			break;
		case 1:
			if(traininfo.getDirection() == TrainDirection.CLOCKWISE) traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
			else traininfo.setDirection(TrainDirection.CLOCKWISE);
		default:
			break;
		}
	}

	public void generateMovement() {
		int pos = traininfo.getPosition();
		traininfo.setPosition(traininfo.getDirection() == TrainDirection.COUNTERCLOCKWISE ? pos + (int) (speed/4) : pos - (int) (speed/4));
		traininfo.setTimeStamp(System.currentTimeMillis());
		traininfo.incrementSequenceNumber();
	}

	public TrainInfo getTrainInfo() {
		return running ? traininfo : null;
	}

	public void startStop() {
		running = !running;
	}

	public boolean running() {
		return running;
	}



	// Do not edit this constructor.
	public TrainSimulator(int timeBetweenNewTrainInfo, int timeBetweenChangeProperty) {
	    this.timeBetweenNewTrainInfo = timeBetweenNewTrainInfo;
	    this.timeBetweenChangeProperty = timeBetweenChangeProperty;
	}
}

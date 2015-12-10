package ntnu.no.trainlogger.eventbasedtrainsimulator;

import java.util.Random;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainStatus;
import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainEventType;
import ntnu.no.trainlogger.enums.TrainState;

public class EventBasedTrainSimulator extends Block {
	private TrainInfo traininfo;
	private Random r;
	private float currentspeed;
	private float setspeed;
	private boolean running;
	private int deltaSequenceNumber = 0;
	private int position = 0;
	private String[] stations = { "Munkegata", "Solsiden", "Gløshaugen", "Lade" };
	// Instance parameter. Edit only in overview page.
	public final int minTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int maxTimeBetweenPropertyChange;
	// Instance parameter. Edit only in overview page.
	public final int acceleration;
	private double accel;
	public int timeBetweenNewTrainInfo;
	public int tracklength = 900;
	public int timeBetweenSyncronization;

	public static String getAlias(int i) {
		return String.valueOf(i);
	}
	
	public void init(int i) {
		accel = acceleration/10.0;
		r = new Random();
		running = true;
		initTrain(i);
	}
	
	private void initTrain(int i){
		traininfo = new TrainInfo(i, r.nextInt(19) + 1);
		TrainStatus ts = new TrainStatus(TrainState.RUNNING);
		ts.setFromStation(stations[r.nextInt(4)]);
		ts.setToStation(stations[r.nextInt(4)]);
		traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
		traininfo.setStatus(ts);
		traininfo.setPosition(position);
		currentspeed = r.nextFloat() * 10 + 5;
		setspeed = currentspeed;
		traininfo.setSpeed(currentspeed);
	}
	

	public void changeProperty() {
		int i = (int) Math.round(r.nextGaussian()*2);
		switch (i) {
			case 0:
				traininfo.getStatus().setState(TrainState.RUNNING);
				setspeed = r.nextFloat() * 10 + 5;
				break;
			case 1:
				traininfo.getStatus().setToStation(stations[r.nextInt(stations.length)]);
				sendToBlock("TOCHANGE");
				break;
			case -1:
				traininfo.getStatus().setFromStation(stations[r.nextInt(stations.length)]);
				sendToBlock("FROMCHANGE");
				break;
			case 2:
				setspeed = 0;
				traininfo.getStatus().setState(TrainState.STOPPING);
				sendToBlock("STATECHANGE");
				break;
			case -2:
				if (traininfo.getDirection() == TrainDirection.CLOCKWISE)
					traininfo.setDirection(TrainDirection.COUNTERCLOCKWISE);
				else
					traininfo.setDirection(TrainDirection.CLOCKWISE);
				sendToBlock("DIRECTIONCHANGE");
				break;
			case 3:
				traininfo.setTrainLength(r.nextInt(20));
				sendToBlock("LENGTHCHANGE");
				break;
			default:
				break;
		}
	}
	

	public void generateMovement() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				while(running){
					if(currentspeed == 0 && traininfo.getStatus().getState() == TrainState.STOPPED && setspeed > 0){
						traininfo.getStatus().setState(TrainState.RUNNING);
						sendToBlock("STATECHANGE");
					}
					
					if(currentspeed != setspeed){
						if(currentspeed < setspeed){
							if((setspeed - currentspeed) <= accel) currentspeed = setspeed;
							else currentspeed += accel;
						} else {
							if((currentspeed-setspeed) <= accel) currentspeed = setspeed;
							else currentspeed -= accel;
						}
						traininfo.setSpeed(currentspeed);
						sendToBlock("SPEEDCHANGE");
					}
					if(currentspeed == 0 && traininfo.getStatus().getState() == TrainState.STOPPING){
						traininfo.getStatus().setState(TrainState.STOPPED);
						sendToBlock("STATECHANGE");
					}
					
					
					position = traininfo.getDirection() == TrainDirection.COUNTERCLOCKWISE ? Math.round(position + (currentspeed/10))
							: Math.round(position - (currentspeed/10));
					if(position < 0) position += tracklength;
					if(position > tracklength) position -= tracklength;					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
			}
		};
		runAsync(r);
	}

	public TrainInfo getTrainInfo() {
		traininfo.setPosition(position);
		traininfo.incrementSequenceNumber();
		traininfo.setTimeStamp(System.currentTimeMillis());
		return running ? traininfo : null;
	}
	
	public void startStop() {
		running = !running;
	}

	public boolean running() {
		return running;
	}

	public int getNewTimeout() {
		return minTimeBetweenPropertyChange + r.nextInt(maxTimeBetweenPropertyChange - minTimeBetweenPropertyChange + 1);
	}

	// Do not edit this constructor.
	public EventBasedTrainSimulator(int minTimeBetweenPropertyChange, int maxTimeBetweenPropertyChange, int timeBetweenSyncronization, int acceleration) {
	    this.minTimeBetweenPropertyChange = minTimeBetweenPropertyChange;
	    this.maxTimeBetweenPropertyChange = maxTimeBetweenPropertyChange;
	    this.timeBetweenSyncronization = timeBetweenSyncronization;
	    this.acceleration = acceleration;
	}

	public void stop() {
		running = false;
	}

	public TrainInfoUpdate speedChange() {
		TrainInfoUpdate td= new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.SPEED);
		td.setSpeed(currentspeed);
		return td;
	}
	
	public TrainInfoUpdate stateChange() {
		TrainInfoUpdate td= new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.STATE);
		td.setState(traininfo.getStatus().getState());
		return td;
	}
	
	public TrainInfoUpdate directionChange(){
		TrainInfoUpdate td= new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.DIRECTION);
		td.setDirection(traininfo.getDirection());
		return td;
	}
	
	public TrainInfoUpdate toStationChange(){
		TrainInfoUpdate td= new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.TO);
		td.setToStation(traininfo.getStatus().getToStation());
		return td;
	}
	
	public TrainInfoUpdate fromStationChange(){
		TrainInfoUpdate td = new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.FROM);
		td.setFromStation(traininfo.getStatus().getFromStation());
		return td;
	}
	
	public TrainInfoUpdate trainLengthChange(){
		TrainInfoUpdate td= new TrainInfoUpdate(traininfo.getId(), System.currentTimeMillis(), deltaSequenceNumber++, TrainEventType.LENGTH);
		td.setTrainLength(traininfo.getTrainLength());
		return td;
	}
	
	
	
}

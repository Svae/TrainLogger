package ntnu.no.trainlogger.processlogdata;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class ProcessLogData extends Block {
	
	private TrainDelta previousDelta;
	private HashMap<Integer, TrainInfo> currentTrainInfos;
	private HashMap<Integer, TrainInfo> sentTrainInfos;
	private TrainDelta newDelta;
	private boolean isStopping = false;
	private int timeBetweenDeltas = 2000;
	
	public void updateDelta(TrainInfo trainInfo) {
		addNewTrainInfo(trainInfo);
	}
	private synchronized void addNewTrainInfo(TrainInfo trainInfo) {
		currentTrainInfos.put(trainInfo.getId(), trainInfo);	
	}
	
	public void init() {
		currentTrainInfos = new HashMap<Integer, TrainInfo>();
		sentTrainInfos = new HashMap<Integer, TrainInfo>();

		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				TrainDelta delta = null;
				while(!isStopping){
					delta = generateDelta();
					sendToBlock("DELTA", delta);
					try {
						Thread.sleep(timeBetweenDeltas);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
					}
				}
			}
		};
		runAsync(r);
	}
	

	public void setTimeBetweenDeltas(int delay){
		timeBetweenDeltas = delay;
	}
	
	protected TrainDelta generateDelta() {
		TrainDelta delta = new TrainDelta();
		for (TrainInfo currentTrainInfo : currentTrainInfos.values()) {
			if(sentTrainInfos.containsKey(currentTrainInfo.getId()) && currentTrainInfo.hasChanges(sentTrainInfos.get(currentTrainInfo.getId()))){
				delta.addTrainInfo(currentTrainInfo.getChanges(sentTrainInfos.get(currentTrainInfo.getId())));
				sentTrainInfos.put(currentTrainInfo.getId(), currentTrainInfo);
				
			} else {
				sentTrainInfos.put(currentTrainInfo.getId(), currentTrainInfo);
				delta.addTrainInfo(currentTrainInfo);
			}
		}
		delta.setTimeStamp(new Date());
		return delta;
	}
	
	public void stop(){
		isStopping = true;
	}
}

package ntnu.no.trainlogger.processlogdata;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainDelta;
import ntnu.no.trainlogger.delta.TrainInfo;

public class ProcessLogData extends Block {
	
	private HashMap<Integer, TrainInfo> currentTrainInfos;
	private HashMap<Integer, TrainInfo> sentTrainInfos;
	private boolean isStopping = false;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenDeltas;
	// Instance parameter. Edit only in overview page.
	public final int timeBetweenSynchronization;
	public void updateDelta(TrainInfo trainInfo) {
		addNewTrainInfo(trainInfo);
	}
	private synchronized void addNewTrainInfo(TrainInfo trainInfo) {
		currentTrainInfos.put(trainInfo.getId(), trainInfo);	
	}
	
	public void init() {
		currentTrainInfos = new HashMap<Integer, TrainInfo>();
		sentTrainInfos = new HashMap<Integer, TrainInfo>();
		makeDeltaProcessThread();
		makeDeltaSyncThread();

	}
	
	private void makeDeltaProcessThread(){
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				TrainDelta delta = null;
				while(!isStopping){
					if(!currentTrainInfos.isEmpty()){
						delta = generateDelta();
						sendToBlock("DELTA", delta);
					}
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
	
	private void makeDeltaSyncThread(){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while(!isStopping){
					if(!currentTrainInfos.isEmpty()){
						TrainDelta delta = new TrainDelta();
						for (TrainInfo ti : currentTrainInfos.values()) {
							delta.addTrainInfo(ti);
						}
						delta.setTimeStamp(System.currentTimeMillis());
						sendToBlock("SYNC", delta);
					}
					try{
						Thread.sleep(timeBetweenSynchronization);
					} catch(InterruptedException e){
						logger.error(e.getMessage());
					}
				}
				
			}
		};
		runAsync(r);
	}
	
	protected TrainDelta generateDelta() {
		TrainDelta delta = new TrainDelta();
		for (TrainInfo currentTrainInfo : currentTrainInfos.values()) {
			if(!sentTrainInfos.containsKey(currentTrainInfo.getId())){
				sentTrainInfos.put(currentTrainInfo.getId(), currentTrainInfo);
				delta.addTrainInfo(currentTrainInfo);
			}
			else if(currentTrainInfo.hasChanges(sentTrainInfos.get(currentTrainInfo.getId()))){		
				delta.addTrainInfo(currentTrainInfo.getChanges(sentTrainInfos.get(currentTrainInfo.getId())));
				sentTrainInfos.put(currentTrainInfo.getId(), currentTrainInfo);
			} 
				
		}
		delta.setTimeStamp(System.currentTimeMillis());	
		return delta;
	}
	
	
	public void stop(){
		isStopping = true;
	}
	// Do not edit this constructor.
	public ProcessLogData(int timeBetweenDeltas, int timeBetweenSynchronization) {
	    this.timeBetweenDeltas = timeBetweenDeltas;
	    this.timeBetweenSynchronization = timeBetweenSynchronization;
	}
}

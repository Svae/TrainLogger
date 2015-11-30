package ntnu.no.trainlogger.eventsimulatortest.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.ntnu.item.arctis.runtime.Block;
import ntnu.no.trainlogger.delta.TrainInfo;
import ntnu.no.trainlogger.delta.TrainInfoUpdate;
import ntnu.no.trainlogger.delta.TrainInfoUpdateSerializer;

public class Component extends Block {

	private GsonBuilder gb;
	private Gson g;

	public int init(){
		gb = new GsonBuilder();
		gb.registerTypeAdapter(TrainInfoUpdate.class, new TrainInfoUpdateSerializer());
		g = gb.create();
		return 1;
	}

	public void printSync(TrainInfo ti){
		System.out.println("------------------------------------");
		logger.info(ti.toString());
		System.out.println("------------------------------------");
	}
	
	public void printUpdate(TrainInfoUpdate tu){
		String s = g.toJson(tu);
		System.out.println(s);
		System.out.println("------------------------------------");
	}

}

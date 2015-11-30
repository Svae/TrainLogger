package ntnu.no.trainlogger.delta;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ntnu.no.trainlogger.enums.TrainDirection;
import ntnu.no.trainlogger.enums.TrainState;

public class TrainInfoUpdateSerializer implements JsonSerializer<TrainInfoUpdate>{

	@Override
	public JsonElement serialize(TrainInfoUpdate ti, Type arg1, JsonSerializationContext arg2) {
		final JsonObject  jsonObject = new JsonObject();
		jsonObject.addProperty("id", ti.getId());
		jsonObject.addProperty("timeStamp", ti.getTimeStamp());
		jsonObject.addProperty("sequenceNumber", ti.getSequenceNumber());
		if(ti.getSpeed() != 0) jsonObject.addProperty("speed", ti.getSpeed());
		if(ti.getDirection() != null) jsonObject.addProperty("direction", ti.getDirection().toString());
		if(!ti.getFromStation().isEmpty()) jsonObject.addProperty("fromStation", ti.getFromStation());
		if(!ti.getToStation().isEmpty()) jsonObject.addProperty("toStation", ti.getToStation());
		if(ti.getState() != null) jsonObject.addProperty("state", ti.getState().toString());
		if(ti.getTrainLength() != 0) jsonObject.addProperty("trainLength", ti.getTrainLength());
		
		return jsonObject;
	}

}

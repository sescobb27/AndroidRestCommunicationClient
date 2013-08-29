package simon.proyecto1.restcommunicationclient.controllers;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ServiceSuscriptor {
	public void responseObtained(JSONArray response) throws Exception;
	public void responseObtained(JSONObject response) throws Exception;
}

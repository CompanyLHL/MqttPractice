package com.meter.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 这里是一个得到服务传过来关于电表的信息 {"collected":true,"shared":true,"time":140000000.00000}
 * */
public class GetMeterInfo extends BaseResponse {
	String TAG = "GetMeterInfo";
	public boolean isCollected = false;
	public boolean isShared = false;
	public double time = 0.00;

	@Override
	public void paseRespones(String js) {
		try {
			JSONObject json = new JSONObject(js);
			isCollected = json.getBoolean("collected");
			isShared = json.getBoolean("shared");
			time = json.getDouble("time");
			System.out.println("接收到的发布的订阅的信息是：collected==" + isCollected
					+ "==shareed===" + isShared + "===time===" + time);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}

/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.example.demo.util;

import org.eclipse.paho.android.service.Constants;
import org.eclipse.paho.android.service.SharePreferenceUtil;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.demo.R;
import com.meter.response.GetDisplayInfo;
import com.meter.response.GetMeterInfo;

/**
 * Handles call backs from the MQTT Client
 * 
 */
public class MqttCallbackHandler implements MqttCallback {

	/**
	 * {@link Context} for the application used to format and import external
	 * strings
	 **/
	private Context context;
	/**
	 * Client handle to reference the connection that this handler is attached
	 * to
	 **/
	private String clientHandle;

	/**
	 * Creates an <code>MqttCallbackHandler</code> object
	 * 
	 * @param context
	 *            The application's context
	 * @param clientHandle
	 *            The handle to a {@link Connection} object
	 */
	public MqttCallbackHandler(Context context, String clientHandle) {
		this.context = context;
		this.clientHandle = clientHandle;
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	@Override
	public void connectionLost(Throwable cause) {

		if (cause != null) {
			Connection c = Connections.getInstance(context).getConnection(
					clientHandle);
			c.addAction("Connection Lost");
			c.changeConnectionStatus(ConnectionStatus.DISCONNECTED);

			// format string to use a notification text
			Object[] args = new Object[2];
			args[0] = c.getId();
			args[1] = c.getHostName();

			String message = context.getString(R.string.connection_lost, args);

			// build intent
			Intent intent = new Intent();
			intent.setClassName(context, "com.example.studentdemo.MainActivity");
			intent.putExtra("handle", clientHandle);

			// notify the user
			Notify.notifcation(context, message, intent,
					R.string.notifyTitle_connectionLost);

			// reconnect
			SharePreferenceUtil preference = new SharePreferenceUtil(context);
			if (!preference.isIs_net_broken()) {
				Notify.notifcation(context, "reconnect", intent,
						R.string.reconnect);
				MQTTClientUtil.getInstance(context).setSubscribe(false);
				MQTTClientUtil.getInstance(context).reconnect();
			}
		}
	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
	 *      org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message)
			throws Exception {

		// Get connection object associated with this object
		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);

		// if(!isNativeMessage(clientHandle)){
		// create arguments to format message arrived notifcation string
		String[] args = new String[2];
		args[0] = new String(message.getPayload());
		args[1] = topic;

		// get the string from strings.xml and format
		String messageString = context.getString(R.string.messageRecieved,
				(Object[]) args);

		// create intent to start activity
		Intent intent = new Intent();
		intent.setClassName(context, "com.example.demo.MainActivity");
		intent.putExtra("handle", clientHandle);

		// format string args
		Object[] notifyArgs = new String[3];
		notifyArgs[0] = c.getId();
		notifyArgs[1] = new String(message.getPayload());
		notifyArgs[2] = topic;

		// notify the user
		Notify.notifcation(context,
				context.getString(R.string.notification, notifyArgs), intent,
				R.string.notifyTitle);
		// 可以在这里对消息进行处理

		// update client history
		c.addAction(messageString);

		// Notify.toast(context, messageString, Toast.LENGTH_LONG);
		String getMessage = messageString.substring(
				messageString.indexOf("{") - 1, messageString.indexOf("}") + 1);

		if (getMessage.contains("meter-ep_ra")) {
			// 这是电表的显示信息
			UpdateUI(getMessage);
		} else {
			UpdataUI(getMessage);

		}
		// }

	}

	/**
	 * 得到publish的数据，然后进行解析 定义两个变量来的得到的信息
	 * */
	boolean isCollected = false;
	boolean isShared = false;
	double time = 0.00;

	public void UpdataUI(String messString) {
		System.out.println("UpdataUI");
		GetMeterInfo getMeterInfo = new GetMeterInfo();
		getMeterInfo.paseRespones(messString.toString());
		isCollected = getMeterInfo.isCollected;
		isShared = getMeterInfo.isShared;
		time = getMeterInfo.time;
		Constants.Collected = isCollected;
		Constants.Shared = isShared;

		// Notify.toast(context, "--" + isCollected + "--" + isShared + "--"
		// + time, Toast.LENGTH_LONG);
	}

	/**
	 * 得到publish 的数据，接受电表电流的一些情况
	 * 
	 * */

	String Ep_pa; /* 正向有功总电量 @ */
	String Ep_ra; /* 反向有功总电量 @ */
	String Ua; /* A向电压 @ */
	String Ia; /* A向电l流 @ */
	String pf; /* 总功率因数 Power Factorv @ */

	public void UpdateUI(String message) {
		System.out.println("UpdateUI =====接受电表电流的一些情况");
		GetDisplayInfo displayInfo = new GetDisplayInfo();
		displayInfo.paseRespones(message.toString());
		Ep_pa = displayInfo.Ep_pa;
		Ep_ra = displayInfo.Ep_ra;
		Ua = displayInfo.Ua;
		Ia = displayInfo.Ia;
		pf = displayInfo.pf;
		Constants.Display_dl_one = "正向有功总电量:" + Ep_pa;
		Constants.Display_dl_two = "反向有功总电量 :" + Ep_ra;
		Constants.Display_dy_one = "A向电压:" + Ua;
		Constants.Display_dy_two = "A向电l流 :" + Ia;
		Constants.Display_zgl = "总功率因数 Power Factory:" + pf;
	}

	/**
	 * 判断是否是自己发的消息
	 * 
	 * @param clientHandle2
	 */
	private boolean isNativeMessage(String clientHandle2) {
		String s = "tcp://" + Constants.MQTT_SERVER + ":" + Constants.MQTT_PORT;
		String ss[] = clientHandle2.split(s);
		SharePreferenceUtil spu = new SharePreferenceUtil(context);
		if (spu.getDeviceId().equals(ss[1])) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Do nothing
		Notify.toast(context, "deliveryComplete", Toast.LENGTH_SHORT);
	}

}

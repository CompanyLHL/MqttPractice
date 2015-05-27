package com.example.demo.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.paho.android.service.Constants;
import org.eclipse.paho.android.service.SharePreferenceUtil;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.util.Connection;
import com.example.demo.util.Connections;
import com.example.demo.util.MQTTClientUtil;
import com.example.demo.util.MqttCallbackHandler;
import com.example.demo.util.Notify;

/**
 * 开启后台服务 然后绑定服务，然后打开连接，发送订阅
 * 
 * */
public class BackgroundService extends Service {

	private MQTTClientUtil mcu;
	private String clientHandle;// 这是拼接的连接服务的地址
	private SharePreferenceUtil preferens;
	private ChangeListener changeListener = new ChangeListener();
	protected static final String TAG = "hef";

	@Override
	public void onCreate() {
		super.onCreate();
		bindService();
		preferens = new SharePreferenceUtil(getBaseContext());
		mcu = MQTTClientUtil.getInstance(getBaseContext());
		clientHandle = "tcp://" + Constants.MQTT_SERVER + ":"
				+ Constants.MQTT_PORT + "" + preferens.getDeviceId();
		if (Connections.getInstance(getBaseContext()).getConnection(
				clientHandle) != null) {
			if (!Connections.getInstance(getBaseContext())
					.getConnection(clientHandle).isConnected()) {
				System.out.println("当前服务未连接");
				mcu.setSubscribe(false);
				mcu.reconnect();
			}
		} else {

			mcu.connectAction();
		}
	}

//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// Recover connections.
//		Map<String, Connection> connections = Connections.getInstance(this)
//				.getConnections();
//
//		// Register receivers again
//		for (Connection connection : connections.values()) {
//			connection.getClient().registerResources(this);
//			connection.getClient().setCallback(
//					new MqttCallbackHandler(this, connection.getClient()
//							.getServerURI()
//							+ connection.getClient().getClientId()));
//		}
//		return super.onStartCommand(intent, flags, startId);
//	}

	/**
	 * 持久化service
	 */
	private void bindService() {
		Intent i = new Intent();
		// 注意Intent的flag设置：FLAG_ACTIVITY_CLEAR_TOP:
		// 如果activity已在当前任务中运行，在它前端的activity都会被关闭，它就成了最前端的activity。FLAG_ACTIVITY_SINGLE_TOP:
		// 如果activity已经在最前端运行，则不需要再加载。设置这两个flag，就是让一个且唯一的一个activity（服务界面）运行在最前端。
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
		Notification myNotify = new Notification.Builder(this)
				.setSmallIcon(R.drawable.mqtt)
				.setTicker("mqtt backgroud service")
				.setContentTitle("Background Service For Start MQTT Service")
				.setContentText("Ummmm, clear").setContentIntent(pi)
				.getNotification();
		// 设置notification的flag，表明在点击通知后，通知并不会消失，也在最右图上仍在通知栏显示图标。这是确保在activity中退出后，状态栏仍有图标可提下拉、点击，再次进入activity。
		myNotify.flags |= Notification.FLAG_NO_CLEAR;

		// 步骤 2：startForeground( int,
		// Notification)将服务设置为foreground状态，使系统知道该服务是用户关注，低内存情况下不会killed，并提供通知向用户表明处于foreground状态。
		startForeground(12345, myNotify);
	}

	@Override
	public void onDestroy() {
		Map<String, Connection> connections = Connections.getInstance(this)
				.getConnections();

		for (Connection connection : connections.values()) {
			connection.registerChangeListener(changeListener);
			connection.getClient().unregisterResources();
		}

		MQTTClientUtil mcu = MQTTClientUtil.getInstance(getBaseContext());
		mcu.disconnect();
		// Notify.toast(getBaseContext(), "失去连接", Toast.LENGTH_LONG);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class ChangeListener implements PropertyChangeListener {

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {

			if (!event.getPropertyName().equals(
					Constants.ConnectionStatusProperty)) {
				return;
			}

		}

	}

}

package com.example.demo.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.paho.android.service.Constants;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.android.service.SharePreferenceUtil;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.demo.R;
import com.example.demo.util.ActionListener.Action;

public class MQTTClientUtil {

	private boolean isSubscribe = false;

	private SharePreferenceUtil preferences;

	private static MQTTClientUtil mqttClient;

	private static Context context;

	/**
	 * 连接的服务器的地址
	 * */
	private String clientHandle;

	// private ConnectionLog mLog;
	private ChangeListener changeListener = new ChangeListener();

	public static MQTTClientUtil getInstance(Context con) {
		context = con;
		if (mqttClient == null) {
			mqttClient = new MQTTClientUtil();
		}
		return mqttClient;
	}

	public MQTTClientUtil() {
		preferences = new SharePreferenceUtil(context);
		// try {
		// mLog = new ConnectionLog();
		clientHandle = "tcp://" + Constants.MQTT_SERVER + ":"
				+ Constants.MQTT_PORT + "" + preferences.getDeviceId();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * 连接 broker 服务器
	 */
	public void connectAction() {
		MqttConnectOptions conOpt = new MqttConnectOptions();
		/*
		 * Mutal Auth connections could do something like this
		 * 
		 * 
		 * SSLContext context = SSLContext.getDefault(); context.init({new
		 * CustomX509KeyManager()},null,null); //where CustomX509KeyManager
		 * proxies calls to keychain api SSLSocketFactory factory =
		 * context.getSSLSocketFactory();
		 * 
		 * MqttConnectOptions options = new MqttConnectOptions();
		 * options.setSocketFactory(factory);
		 * 
		 * client.connect(options);
		 */

		// The basic client information
		String clientId = preferences.getDeviceId();// 客户端设备id

		String uri = "tcp://";
		uri = uri + Constants.MQTT_SERVER + ":" + Constants.MQTT_PORT;

		MqttAndroidClient client;
		client = Connections.getInstance(context).createClient(context, uri,
				clientId);

		// last will message
		String message = "";// 发送的消息
		String topic = "";// 主题
		Boolean retained = true;

		// connection options

		// String username = (String) data.get(ActivityConstants.username);
		//
		// String password = (String) data.get(ActivityConstants.password);

		Connection connection = new Connection(clientHandle, clientId,
				Constants.MQTT_SERVER, Constants.MQTT_PORT, context, client,
				Constants.ssl);
		// arrayAdapter.add(connection);

		connection.registerChangeListener(changeListener);
		// connect client

		String[] actionArgs = new String[1];
		actionArgs[0] = clientId;
		connection.changeConnectionStatus(ConnectionStatus.CONNECTING);

		conOpt.setCleanSession(Constants.cleanSession);// 设置清除会话信息 TRUE 清除 fasle
														// 不清除 可以接收到先前发送的信息
		conOpt.setConnectionTimeout(Constants.TIMEOUT);// 设置超时时间
		conOpt.setKeepAliveInterval(Constants.KEEP_ALIVE_INTERVAL);// 设置会话心跳时间
																	// 单位为秒
																	// 服务器会每隔1.5*20000秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
		// if (!username.equals(ActivityConstants.empty)) {
		// conOpt.setUserName(username);
		// }
		// if (!password.equals(ActivityConstants.empty)) {
		// conOpt.setPassword(password.toCharArray());
		// }

		final ActionListener callback = new ActionListener(context,
				ActionListener.Action.CONNECT, clientHandle, actionArgs);

		boolean doConnect = true;

		if ((!message.equals(Constants.empty))
				|| (!topic.equals(Constants.empty))) {
			// need to make a message since last will is set
			try {
				// 设置最终端口的通知消息 需要知道客户端是否掉线
				conOpt.setWill(topic, message.getBytes(), Constants.QOS,
						retained.booleanValue());
			} catch (Exception e) {
				Log.e(this.getClass().getCanonicalName(), "Exception Occured",
						e);
				doConnect = false;
				callback.onFailure(null, e);
			}
		}
		// 设置MQTT回调
		client.setCallback(new MqttCallbackHandler(context, clientHandle));
		connection.addConnectionOptions(conOpt);
		Connections.getInstance(context).addConnection(connection);
		if (doConnect) {
			try {
				// 连接broker
				client.connect(conOpt, null, callback);
			} catch (MqttException e) {
				Log.e(context.getClass().getCanonicalName(),
						"MqttException Occured", e);
			}
		}

	}

	/**
	 * This class ensures that the user interface is updated as the Connection
	 * objects change their states
	 * 
	 * 
	 */
	private class ChangeListener implements PropertyChangeListener {

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			Intent intent = new Intent();
			intent.setClassName(context, "com.example.demo.MainActivity");
			intent.putExtra("handle", clientHandle);
			if (!isSubscribe
					&& replaceBlank(event.getSource().toString()).equals(
							preferences.getDeviceId() + "Connectedto"
									+ Constants.MQTT_SERVER)) {
				isSubscribe = true;
				subscribe();
			}
		}
	}

	/**
	 * 去掉字符串中的空 例如 空格 回车 等
	 * 
	 * @param str
	 * @return
	 */
	public String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public boolean isSubscribe() {
		return isSubscribe;
	}

	public void setSubscribe(boolean isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	/**
	 * Subscribe to a topic that the user has specified
	 */
	public boolean subscribe() {
		try {
			Notify.toast(context, "订阅成功===" + Constants.subscrbieTopics,
					Toast.LENGTH_SHORT);
			Connections
					.getInstance(context)
					.getConnection(clientHandle)
					.getClient()
					.subscribe(
							Constants.subscrbieTopics,
							Constants.qos,
							null,
							new ActionListener(context, Action.SUBSCRIBE,
									clientHandle, Constants.subscrbieTopics));
		} catch (MqttSecurityException e) {
			Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to"
					+ Constants.subscrbieTopics
					+ " the client with the handle " + clientHandle, e);
			return false;
		} catch (MqttException e) {
			Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to"
					+ Constants.subscrbieTopics
					+ " the client with the handle " + clientHandle, e);
			return false;
		}
		return true;
	}

	/**
	 * Publish the message the user has specified
	 */
	public void publish(String topic, String message, int qos) {
		boolean retained = true;
		String[] args = new String[2];
		args[0] = message;
		args[1] = topic;
		try {
			Connections
					.getInstance(context)
					.getConnection(clientHandle)
					.getClient()
					.publish(
							topic,
							message.getBytes(),
							qos,
							retained,
							null,
							new ActionListener(context, Action.PUBLISH,
									clientHandle, args));
		} catch (MqttSecurityException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Failed to publish a messged from the client with the handle "
							+ clientHandle, e);
		} catch (MqttException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Failed to publish a messged from the client with the handle "
							+ clientHandle, e);
		}

	}

	/**
	 * Reconnect the selected client
	 */
	public void reconnect() {
		System.out.println("reconnect");
		Connections.getInstance(context).getConnection(clientHandle)
				.changeConnectionStatus(ConnectionStatus.CONNECTING);

		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		try {
			c.getClient().connect(
					c.getConnectionOptions(),
					null,
					new ActionListener(context, Action.CONNECT, clientHandle,
							null));
		} catch (MqttSecurityException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Failed to reconnect the client with the handle "
							+ clientHandle, e);
			c.addAction("Client failed to connect");
		} catch (MqttException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Failed to reconnect the client with the handle "
							+ clientHandle, e);
			c.addAction("Client failed to connect");
		}

	}

	/**
	 * Disconnect the client
	 */
	public void disconnect() {

		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);

		// if the client is not connected, process the disconnect
		if (!c.isConnected()) {
			return;
		}

		try {
			c.getClient().disconnect(
					null,
					new ActionListener(context, Action.DISCONNECT,
							clientHandle, null));
			c.changeConnectionStatus(ConnectionStatus.DISCONNECTING);
		} catch (MqttException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Failed to disconnect the client with the handle "
							+ clientHandle, e);
			c.addAction("Client failed to disconnect");
		}

	}

}

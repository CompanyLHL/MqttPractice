package org.eclipse.paho.android.service;

public class Constants {

	public static final String TAG = "hef";
	public static final String SERVER = "COM.EXAMPLE.STUDENTDEMO";
	// 控制服务启动
	public static final String ACTION_START = SERVER + ".START";
	// 控制服务停止
	public static final String ACTION_STOP = SERVER + ".STOP";
	// 服务保持链接
	public static final String ACTION_KEEPALIVE = SERVER + ".KEEP_ALIVE";
	// 重新链接
	public static final String ACTION_RECONNECT = SERVER + ".RECONNECT";

	// We store in the preferences, whether or not the service has been started
	public static final String PREF_STARTED = "isStarted";
	// We also store the deviceID (target)
	public static final String PREF_DEVICE_ID = "deviceID";

	// This the application level keep-alive interval, that is used by the
	// AlarmManager
	// to keep the connection active, even when the device goes to sleep.
	// public static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 60*24;
	public static final int KEEP_ALIVE_INTERVAL = 1000 * 60;
	// Retry intervals, when the connection is lost. 重试间隔
	// public static final long INITIAL_RETRY_INTERVAL = 1000 * 60;
	// public static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;
	/**
	 * Property name for the history field in {@link Connection} object for use
	 * with {@link java.beans.PropertyChangeEvent}55633964
	 **/
	public static final String historyProperty = "history";

	/**
	 * Property name for the connection status field in {@link Connection}
	 * object for use with {@link java.beans.PropertyChangeEvent}
	 **/
	public static final String ConnectionStatusProperty = "connectionStatus";
	public static final String empty = new String();
	/** Show History Request Code **/
	public static final int showHistory = 3;

	public static final int TIMEOUT = 5000;
	/**
	 * MQTT 服务器所在的ip 地址
	 */
	public static String MQTT_SERVER = "192.168.0.187";
	/**
	 * MQTT 服务器所在的端口号
	 */
	public static int MQTT_PORT = 1883;
	// 订阅消息的主题
	public static String[] subscrbieTopics = { "status/meter/138000121397",
			"status/meter-value/138000121397" };
	// 订阅主题的消息级别
	public static int[] qos = { 0, 0 };
	public static int QOS = 0;
	public static boolean cleanSession = false;// 是否清除会话 false 持久链接 TRUE 该会话内有效
	public static boolean ssl = false;// 用ssl 协议
	/**
	 * 定义两个用来显示是否打开的变量，默认为没有打开
	 * */
	public static boolean Collected = false;
	public static boolean Shared = false;
	/**
	 * 定义一个字符串变量用来表示接受到的数据
	 * */
	public static String Display_dl_one, Display_dl_two, Display_dy_one,
			Display_dy_two, Display_zgl;
}

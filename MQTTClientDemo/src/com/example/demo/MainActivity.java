package com.example.demo;

import java.util.TimerTask;

import org.eclipse.paho.android.service.Constants;
import org.eclipse.paho.android.service.SharePreferenceUtil;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.demo.service.BackgroundService;
import com.example.demo.util.MQTTClientUtil;
import com.example.demo.util.Notify;

/**
 * 点击按钮，然后触发事件
 * 
 * 
 * 这里曾出现过一个问题，那就是点击按钮，会触发listerner，所以，这里对按钮事件添加一个布尔值
 * 
 * */
public class MainActivity extends Activity implements OnClickListener {
	Button btn_state, btn_state_two;
	RadioGroup radio_G, radio_G_two;
	RadioButton btn_on, btn_off, btn_on_two, btn_off_two;
	TextView text_status, text_status_two;
	private SharePreferenceUtil preferens;
	private String topicName = "ctrl/meter/138000121397";
	EditText edit_ip;
	// 定义一个布尔值用来确定是否点击了按钮
	private boolean isDot = false;
	// 这个布尔值是用来是否需要触发点击事件
	private boolean isChecked = false;
	SharedPreferences sp = null;
	LinearLayout layout_switch_one, layout_switch_two;

	Button btn_Connect, btn_Sub;
	TextView text_display_dl_one, text_display_dl_two, text_display_dy_one,
			text_display_dy_two, text_display_zgl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		preferens = new SharePreferenceUtil(this);
		preferens.setDeviceId();

		initView();

	}

	java.util.Timer timer;
	String getIp = "192.168.0.187";

	@Override
	protected void onResume() {
		// 获取数据
		getIp = sp.getString("ip", Constants.MQTT_SERVER);
		super.onResume();
	}

	/**
	 * 初始化
	 * */
	public void initView() {
		text_status = (TextView) findViewById(R.id.text_status);
		text_status_two = (TextView) findViewById(R.id.text_status_two);
		btn_state = (Button) findViewById(R.id.btn_state);
		btn_on = (RadioButton) findViewById(R.id.btn_on);

		btn_off = (RadioButton) findViewById(R.id.btn_off);
		radio_G = (RadioGroup) findViewById(R.id.radioGroup);
		btn_state_two = (Button) findViewById(R.id.btn_state_two);
		btn_on_two = (RadioButton) findViewById(R.id.btn_on_two);
		btn_off_two = (RadioButton) findViewById(R.id.btn_off_two);
		text_display_dl_one = (TextView) findViewById(R.id.text_display_dl_one);
		text_display_dl_two = (TextView) findViewById(R.id.text_display_dl_two);
		text_display_dy_one = (TextView) findViewById(R.id.text_display_dy_one);
		text_display_dy_two = (TextView) findViewById(R.id.text_display_dy_two);
		text_display_zgl = (TextView) findViewById(R.id.text_display_agl);
		btn_on.setOnClickListener(this);
		btn_on_two.setOnClickListener(this);
		btn_off.setOnClickListener(this);
		btn_off_two.setOnClickListener(this);
		radio_G_two = (RadioGroup) findViewById(R.id.radioGroup_two);
		edit_ip = (EditText) findViewById(R.id.text_ip);
		edit_ip.setText(getIp);
		layout_switch_one = (LinearLayout) findViewById(R.id.layout_switch_one);
		layout_switch_two = (LinearLayout) findViewById(R.id.layout_switch_two);
		sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
		btn_Connect = (Button) findViewById(R.id.btn_connect);
		btn_Sub = (Button) findViewById(R.id.btn_sub);
		btn_Connect.setOnClickListener(this);
		btn_Sub.setOnClickListener(this);
		radio_G.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radio, int position) {
				if (isDot) {
					checkContent(position);
				}
				isDot = false;
				// if (isChecked && !isDot) {
				//
				// } else {
				// checkContent(position);
				// isDot = true;
				//
				// }
				isChecked = false;
			}
		});
		radio_G_two.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radio, int position) {
				// if (isChecked && !isDot) {
				//
				// } else {
				//
				// checkContent(position);
				// isDot = true;
				// }
				if (isDot) {
					checkContent(position);
				}
				// isDot = false;
				isChecked = false;
			}
		});

	}

	// public void publishToSelf() {
	//
	// MQTTClientUtil mcu = MQTTClientUtil.getInstance(getBaseContext());
	// String getjson = GetJson();
	// mcu.publish("status/meter/138000121397", getjson, 2);
	// }

	public void startServer() {
		System.out.println("startServer");
		if (isConnect(getBaseContext())) {
			Intent intent = new Intent(this, BackgroundService.class);
			startService(intent);
		} else {
			Notify.toast(getBaseContext(), "请检查您的网络，无连接 或者 连接不正确！",
					Toast.LENGTH_LONG);
			finish();
		}
	}

	public void StartJob() {
		timer = new java.util.Timer();
		timer.schedule(new Job(), 3000, 2000);
		// isDot = true;

	}

	class Job extends TimerTask {

		@Override
		public void run() {
			postHandler.sendEmptyMessage(CHECK);
		}

	}

	/**
	 * 切换按钮的状态值，然后来改变btn_state
	 * 
	 * */
	MQTTClientUtil mcu = null;

	private void checkContent(int v) {

		System.out.println("isdot====" + isDot);
		switch (v) {
		case R.id.btn_on:
			myHandler.sendEmptyMessage(COLLECTINGON);
			break;
		case R.id.btn_off:
			myHandler.sendEmptyMessage(COLLECTINGOFF);
			break;
		case R.id.btn_on_two:
			myHandler.sendEmptyMessage(SHAREEDON);
			break;
		case R.id.btn_off_two:
			myHandler.sendEmptyMessage(SHAREDOFF);
			break;

		default:
			break;
		}
	}

	/**
	 * 启动一个消息机制，来传送数据
	 * 
	 * 
	 * */
	public final static int COLLECTINGON = 0x000011;
	public final static int COLLECTINGOFF = 0x000022;
	public final static int SHAREEDON = 0x000001;
	public final static int SHAREDOFF = 0x000002;
	public final static int CHECK = 0x000000;
	public final static int ISBACK = 0x000033;
	String getJson = "";
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (null == mcu) {
				mcu = MQTTClientUtil.getInstance(getBaseContext());

			}

			switch (msg.what) {
			case COLLECTINGON:
				getJson = GetJson("shared", "off");
				mcu.publish(topicName, getJson, 2);

				getJson = GetJson("collected", "off");
				mcu.publish(topicName, getJson, 2);
				break;
			case COLLECTINGOFF:

				getJson = GetJson("collected", "on");
				mcu.publish(topicName, getJson, 2);

				break;
			case SHAREEDON:
				getJson = GetJson("shared", "off");
				mcu.publish(topicName, getJson, 2);
				break;
			case SHAREDOFF:
				getJson = GetJson("shared", "on");
				mcu.publish(topicName, getJson, 2);

				break;
			default:
				break;
			}

		}
	};
	/**
	 * 接收改变的消息
	 * */
	Handler postHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (null == mcu) {
				mcu = MQTTClientUtil.getInstance(getBaseContext());

			}

			switch (msg.what) {

			case CHECK:
				if (Constants.Collected) {
					btn_state.setBackgroundResource(R.drawable.green);
					btn_on.setTextColor(0xFF888888);
					btn_off.setTextColor(0xFF888888);
					text_status.setText("运行中");
					btn_on.setChecked(true);

				} else {
					btn_off.setTextColor(0xFF888888);
					btn_on.setTextColor(0xFF888888);
					btn_state.setBackgroundResource(R.drawable.red);
					text_status.setText("未运行");
					btn_off.setChecked(true);
				}
				if (Constants.Shared) {
					btn_on_two.setTextColor(0xFF888888);
					btn_off_two.setTextColor(0xFF888888);
					btn_state_two.setBackgroundResource(R.drawable.green);
					text_status_two.setText("运行中");
					btn_on_two.setChecked(true);
				} else {
					btn_off_two.setTextColor(0xFF888888);
					btn_on_two.setTextColor(0xFF888888);
					btn_state_two.setBackgroundResource(R.drawable.red);
					text_status_two.setText("未运行");
					btn_off_two.setChecked(true);
				}
				isChecked = true;
				isDot = false;
				if (null == Constants.Display_dl_one
						|| Constants.Display_dl_one.length() == 0) {
					text_display_dl_one.setText("暂无数据");
					text_display_dy_one.setVisibility(View.GONE);
					text_display_dl_two.setVisibility(View.GONE);
					text_display_dy_two.setVisibility(View.GONE);
					text_display_zgl.setVisibility(View.GONE);
				} else {
					System.out
							.println("display====" + Constants.Display_dl_one);
					text_display_dy_one.setVisibility(View.VISIBLE);
					text_display_dl_two.setVisibility(View.VISIBLE);
					text_display_dy_two.setVisibility(View.VISIBLE);
					text_display_dl_one.setText(Constants.Display_dl_one);
					text_display_dy_two.setText(Constants.Display_dy_two);
					text_display_dl_two.setText(Constants.Display_dl_two);
					text_display_dy_one.setText(Constants.Display_dy_one);
					text_display_zgl.setText(Constants.Display_zgl);
				}
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onDestroy() {
		if (isConnect(getBaseContext())) {
			Intent intent = new Intent(this, BackgroundService.class);
			stopService(intent);
		}
		super.onDestroy();
	}

	public boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("isconnect error", e.toString());
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_connect:
			String ip = edit_ip.getText().toString();
			if (ip.length() == 0 || null == ip || ip.equals("127.0.0.1")) {
				Toast.makeText(MainActivity.this, "请填写Ip地址", Toast.LENGTH_LONG)
						.show();
			} else {
				// 添加数据，一定要使用edit函数
				Editor editor = sp.edit();
				editor.putString("ip", ip);
				// 保存数据 ，类是于事务
				editor.commit();
				Constants.MQTT_SERVER = ip;
				System.out.println("the server ip is==="
						+ Constants.MQTT_SERVER);
				startServer();
				StartJob();

				layout_switch_one.setVisibility(View.VISIBLE);
				layout_switch_two.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_sub:
			StartJob();
			break;
		case R.id.btn_on:
			System.out.println("dot....");
			isDot = true;
			break;
		case R.id.btn_off:
			System.out.println("dot....");
			isDot = true;
			break;
		case R.id.btn_on_two:
			System.out.println("dot....");
			isDot = true;
			break;
		case R.id.btn_off_two:
			System.out.println("dot....");
			isDot = true;
			break;
		default:
			break;
		}

	}

	/**
	 * 转换成json
	 * 
	 * {"collected":true,"shared":true,"time":140000000.00000}
	 * */
	public String GetJson(String name, String status) {
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("stat", status);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("转化后的json 是===》" + json.toString());
		return json.toString();
	}

	/**
	 * 转换成pubjson
	 * 
	 * {"collected":true,"shared":true,"time":140000000.00000}
	 * */
	public String GetJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("collected", true);
			json.put("shared", true);
			json.put("time", 140000000.00000);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("转化后的json 是===》" + json.toString());
		return json.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			if (isConnect(getBaseContext())) {
				Intent intent = new Intent(this, BackgroundService.class);
				stopService(intent);
			}
			finish();
			return false;
		} else {

			return super.onKeyDown(keyCode, event);
		}
	}

}

package org.eclipse.paho.android.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;

/**
 * 轻量存储 类
 * @author hh
 *
 */
public class SharePreferenceUtil {
	
	private SharedPreferences mPrefs;
	
	/**
	 * 网络断开标志 true 断开 ，false 未断开
	 */
	private boolean is_net_broken;
	
	private Context context;
	
	
	public SharePreferenceUtil(Context context) {
		this.context = context;
		mPrefs = context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE);
	}

	
	/**
	 * 设置设备id
	 */
	public void setDeviceId(){
		String mDeviceID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		mPrefs.edit().putString(Constants.PREF_DEVICE_ID, mDeviceID).commit();
	}
	
	/**
	 * 获取设备id
	 * @return 设备id
	 */
	public String getDeviceId(){
		return mPrefs.getString(Constants.PREF_DEVICE_ID, "");
	}


	/**
	 * 获取网络是否断开
	 * @return true 断开 false 未断开
	 */
	public boolean isIs_net_broken() {
		return is_net_broken;
	}


	/**
	 * 设置网络断开标志
	 * @param is_net_broken
	 */
	public void setIs_net_broken(boolean is_net_broken) {
		this.is_net_broken = is_net_broken;
	}
	
	
	

}

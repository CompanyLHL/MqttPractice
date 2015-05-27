package com.meter.response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 这里是一个得到服务传过来关于电表的信息
 * 
 * 
 * {"meter-ep_ra": "0.00", "status": true, "meter-id": "138000121397",
 * "meter-ep_pj": "0.00", "meter-Ic": "0.0000", "meter-ep_pg": "0.00",
 * "meter-ep_pf": "0.00", "meter-ep_pa": "0.44", "meter-Ia": "0.0460",
 * "meter-pf": "0.000", "time": 1428408606.76514, "meter-Uc": "0.00",
 * "meter-Ub": "0.0000", "meter-Ua": "214.30", "meter-ep_pp": "0.00"}
 * 
 * 
 * */
public class GetDisplayInfo extends BaseResponse {
	public String TAG = "GetDisplayInfo";
	public String Ep_pa; /* 正向有功总电量 @ */
	public String Ep_pj; /* 正向有功尖电量 */
	public String Ep_pf; /* 正向有功峰电量 */
	public String Ep_pp; /* 正向有功平电量 */
	public String Ep_pg; /* 正向有功谷电量 */
	public String Ep_ra; /* 反向有功总电量 @ */
	public String Ua; /* A向电压 @ */
	public String Ub; /* B向电压 */
	public String Uc; /* C向电压 */
	public String Ia; /* A向电l流 @ */
	public String Ib; /* B向电l流 */
	public String Ic; /* C向电l流 */
	public String pf; /* 总功率因数 Power Factorv @ */

	@Override
	public void paseRespones(String js) {
		try {
			JSONObject json = new JSONObject(js);
			Ep_pa = json.getString("meter-ep_pa");// 正向有功总电量
			Ep_pj = json.getString("meter-ep_pj");
			Ep_pf = json.getString("meter-ep_pf");
			Ep_pp = json.getString("meter-ep_pp");
			Ep_pg = json.getString("meter-ep_pg");
			Ep_ra = json.getString("meter-ep_ra");// 反向有功总电量
			Ua = json.getString("meter-Ua");// A向电压
			Ub = json.getString("meter-Ub");
			Uc = json.getString("meter-Uc");
			Ia = json.getString("meter-Ia");// A向电l流
			// Ib = json.getString("meter");
			Ic = json.getString("meter-Ic");
			pf = json.getString("meter-pf");// 总功率因数 Power Factorv
			System.out.println("topic 2接收到的发布的订阅的信息是：正向有功总电量Ep_pa==" + Ep_pa
					+ "==反向有功总电量Ep_ra==" + Ep_ra + "==A向电压Ua==" + Ua
					+ "==A向电l流Ia==" + Ia + "==总功率因数 Power Factorvpf==" + pf);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}

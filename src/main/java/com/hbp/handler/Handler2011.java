package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 实时数据采集
 * <--- QN=20040516010101001;ST=32;CN=2011;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&
 * ---> ST=32;CN=2011;PW=123456;MN=8888888880000001;CP=&&DataTime=20040516010101011;B01-Rtd=100;101-Rtd=1.1,101-Flag=N;102-Rtd=2.2,102-Flag=N...&&
 */
public class Handler2011 implements MinaHandler {

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String cp = getRealTimeData();

		// uploadMsg
		MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
				in.getMN(), cp));
		result.add(uploadMsg);

		return result;
	}

	/**
	 * TODO 获取实时数据
	 */
	private String getRealTimeData() {
		return "DataTime=20040516010101011;B01-Rtd=100;101-Rtd=1.1,101-Flag=N;102-Rtd=2.2,102-Flag=N...";
	}

	public static void main(String[] args) {
		Handler2011 handler = new Handler2011();
		List<MinaMessage> list = handler.handleMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=2011;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 污染治理设施运行状态
 * <--- QN=20040516010101001;ST=32;CN=2021;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&
 * ---> ST=32;CN=2021;PW=123456;MN=8888888880000001;CP=&&DataTime=20040516010101011;SB1-RS=1;SB2-RS=0...&&
 */
public class Handler2021 implements MinaHandler {

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String cp = getSBs();

		// uploadMsg
		MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
				in.getMN(), cp));
		result.add(uploadMsg);

		return result;
	}

	/**
	 * TODO 获取设施运行状态
	 */
	private String getSBs() {
		return "DataTime=20040516010101011;SB1-RS=1;SB2-RS=0...";
	}

	public static void main(String[] args) {
		Handler2021 handler = new Handler2021();
		List<MinaMessage> list = handler.handleMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=2021;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

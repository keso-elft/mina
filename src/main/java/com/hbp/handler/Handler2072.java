package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.message.MinaMessage;

/**
 * 污染物报警记录
 * <--- QN=20040516010101001;ST=32;CN=2072;PW=123456;MN=88888880000001;Flag=0;CP=&&AlarmTime=20040506010101;101-Ala=1.1,AlarmType=1&&
 */
public class Handler2072 implements MinaHandler {

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		alarm(in.getCP());

		return result;
	}

	/**
	 * TODO 报警
	 */
	private void alarm(String content) {
	}

	public static void main(String[] args) {
		Handler2072 handler = new Handler2072();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=2072;PW=123456;MN=88888880000001;Flag=0;CP=&&AlarmTime=20040506010101;101-Ala=1.1,AlarmType=1&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

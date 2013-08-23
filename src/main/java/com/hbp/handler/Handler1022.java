package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 设置污染物报警门限值
 * <--- QN=20040516010101001;ST=32;CN=1022;PW=123456;MN=88888880000001;Flag=3;CP=&&101-LowValue=1.1,101-UpValue=9.9;102-LowValue=1.1,102-UpValue=9.9...&&
 * ---> ST=91;CN=9012;PW=123456;MN=88888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler1022 implements MinaHandler {

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		int exeRtn = setAlarmLimit(in.getCP());

		// exeRtnMsg
		String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(), exeRtn);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
				exeRtnCp));
		result.add(exeRtnMsg);

		return result;
	}

	/**
	 * TODO 获取设施运行时间日数据
	 */
	private int setAlarmLimit(String content) {
		return Constants.EXE_SUCCESS;
	}

	public static void main(String[] args) {
		Handler1022 handler = new Handler1022();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=1022;PW=123456;MN=88888880000001;Flag=3;CP=&&101-LowValue=1.1,101-UpValue=9.9;102-LowValue=1.1,102-UpValue=9.9...&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}
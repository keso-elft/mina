package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 污染物报警记录
 * <--- QN=20040516010101001;ST=32;CN=2071;PW=123456;MN=88888880000001;Flag=3;CP=&&BeginTime=20040506010001,EndTime=20040506150030&&
 * ---> ST=32;CN=2071;PW=123456;MN=88888880000001;CP=&&DataTime=20040506010101;101-Ala=1.1&&
 * ---> ST=91;CN=9012;PW=123456;MN=88888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler2071 implements MinaHandler {

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String cp = getAlarm(in.getCpPara("BeginTime"), in.getCpPara("EndTime"));

		// uploadMsg
		MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
				in.getMN(), cp));
		result.add(uploadMsg);

		// exeRtnMsg
		String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(), Constants.EXE_SUCCESS);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
				exeRtnCp));
		result.add(exeRtnMsg);

		return result;
	}

	/**
	 * TODO 获取设施运行时间日数据
	 */
	private String getAlarm(String beginTime, String endTime) {
		return "DataTime=20040506010101;101-Ala=1.1";
	}

	public static void main(String[] args) {
		Handler2071 handler = new Handler2071();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=2071;PW=123456;MN=88888880000001;Flag=3;CP=&&BeginTime=20040506010001,EndTime=20040506150030&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

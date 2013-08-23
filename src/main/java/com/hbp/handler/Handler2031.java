package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 污染物日数据
 * <--- QN=20040516010101001;ST=32;CN=2031;PW=123456;MN=88888880000001;Flag=3;CP=&&BeginTime=20040506000000,EndTime=20040510000000&&
 * ---> ST=32;CN=2031;PW=123456;MN=88888880000001;CP=&&DataTime=20040506000000;B01-Cou=200;101-Cou=2.5,101-Min=1.1,101-Avg=1.1,101-Max=1.1;102-Cou=2.5,102-Min=2.1,102-Avg=2.1,102-Max=2.1...&&
 * ---> ST=91;CN=9012;PW=123456;MN=88888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler2031 implements MinaHandler {

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String cp = getDaysData(in.getCpPara("BeginTime"), in.getCpPara("EndTime"));

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
	 * TODO 获取污染物日数据
	 */
	private String getDaysData(String beginTime, String endTime) {
		return "DataTime=20040506000000;B01-Cou=200;101-Cou=2.5,101-Min=1.1,101-Avg=1.1,101-Max=1.1;102-Cou=2.5,102-Min=2.1,102-Avg=2.1,102-Max=2.1...";
	}

	public static void main(String[] args) {
		Handler2031 handler = new Handler2031();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=2031;PW=123456;MN=88888880000001;Flag=3;CP=&&BeginTime=20040506000000,EndTime=20040510000000&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}
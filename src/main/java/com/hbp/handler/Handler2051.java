package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 6. 污染物分钟数据
 * <--- QN=20040516010101001;ST=32;CN=2051;PW=123456;MN=8888888880000001;Flag=3;CP=&&BeginTime=20040516010101001,EndTime=20040516010101001&&
 * ---> ST=32;CN=2051;PW=123456;MN=8888888880000001;CP=&&DataTime=20040516010101011;B01-Cou=200;101-Cou=2.5,101-Min=1.1,101-Avg=1.1,101-Max=1.1;102-Cou=2.5,102-Min=2.1,102-Avg=2.1,102-Max=2.1...&&
 * ---> ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler2051 implements MinaHandler {

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String content = getMinsData(in.getCpPara("BeginTime"), in.getCpPara("EndTime"));

		// 根据回复消息的flag, 区分回复方式
		int flag = in.getRtnFlag();

		if (flag == 0) { // 无应答无分包

			// uploadMsg
			MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
					in.getMN(), content));
			result.add(uploadMsg);

			// exeRtnMsg
			String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(), Constants.EXE_SUCCESS);
			MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
					exeRtnCp));
			result.add(exeRtnMsg);

		} else if (flag == 1) {// 有应答无分包模式

			// uploadMsg
			MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
					in.getMN(), content));
			result.add(uploadMsg);

		} else if (flag == 3) { // 有应答有分包模式

			// uploadMsg
			MinaMessage uploadMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
					in.getMN(), content));
			List<MinaMessage> subPackageMsg = uploadMsg.subMessages();

			result.addAll(subPackageMsg);

		}

		return result;
	}

	/**
	 * TODO 污染物分钟数据
	 */
	private String getMinsData(String beginTime, String endTime) {
		return "DataTime=20040516010101011;B01-Cou=200;101-Cou=2.5,101-Min=1.1,101-Avg=1.1,101-Max=1.1;102-Cou=2.5,102-Min=2.1,102-Avg=2.1,102-Max=2.1...";
	}

	public static void main(String[] args) {
		Handler2051 handler = new Handler2051();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=2051;PW=123456;MN=8888888880000001;Flag=3;CP=&&BeginTime=20040516010101001,EndTime=20040516010101001&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

package com.hbp.handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 提取现场机系统时间
 * <--- QN=20040516010101001;ST=32;CN=1011;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&
 * ---> ST=32;CN=1011;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;SystemTime=20040516010101002&&
 * ---> ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler1011 implements MinaHandler {

	public final static String UPLOAD_CP_PATTERN = "QN=%s;SystemTime=%s";

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	@Override
	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// answerMsg
		String systemTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String answerCp = String.format(UPLOAD_CP_PATTERN, in.getQN(), systemTime);
		MinaMessage answerMsg = new MinaMessage(String.format(Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
				in.getMN(), answerCp));
		result.add(answerMsg);

		// exeRtnMsg
		String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(), Constants.EXE_SUCCESS);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
				exeRtnCp));
		result.add(exeRtnMsg);

		return result;
	}

	public static void main(String[] args) {
		Handler1011 handler = new Handler1011();
		List<MinaMessage> list = handler.handleMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1011;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

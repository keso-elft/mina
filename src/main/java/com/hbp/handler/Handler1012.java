package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 设置现场机系统时间
 * <--- QN=20040516010101001;ST=32;CN=1012;PW=123456;MN=8888888880000001;Flag=3;CP=&&SystemTime=20040516010101001&&
 * ---> ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler1012 implements MinaHandler {

	protected Logger log = LoggerFactory.getLogger(Handler1012.class);

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		String systemTime = in.getCpPara("SystemTime");
		int exeRtn = setSystemTime(systemTime);

		// exeRtnMsg
		String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(), exeRtn);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
				exeRtnCp));
		result.add(exeRtnMsg);

		return result;
	}

	private int setSystemTime(String systemTime) {
		int exeRtn = Constants.EXE_SUCCESS;

		try {
			// TODO 设置系统时间

		} catch (Exception e) {
			exeRtn = Constants.EXE_FAIL;
			log.error("change password failure!", e);
		}
		return exeRtn;
	}

	public static void main(String[] args) {
		Handler1012 handler = new Handler1012();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=1012;PW=123456;MN=8888888880000001;Flag=3;CP=&&SystemTime=20040516010101001&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

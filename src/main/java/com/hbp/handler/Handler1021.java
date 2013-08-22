package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 提取污染物报警门限值 <---
 * QN=20040516010101001;ST=32;CN=1021;PW=123456;MN=88888880000001
 * ;Flag=3；CP=&&Polld=101;Polld=102...&& --->
 * ST=91;CN=9011;PW=123456;MN=88888880000001
 * ;Flag=0;CP=&&QN=20040516010101001;QnRtn=1&& --->
 * ST=32;CN=1021;PW=123456;MN=88888880000001
 * ;CP=&&QN=20040516010101001;101-LowValue
 * =1.1,101-UpValue=1.1,102-UpValue=9.9...&& --->
 * ST=91;CN=9012;PW=123456;MN=88888880000001
 * ;CP=&&QN=20040516010101001;ExtRtn=1&&
 */
public class Handler1021 implements MinaHandler {

	protected Logger log = LoggerFactory.getLogger(Handler1021.class);
	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	@Override
	public List<MinaMessage> handleMsg(MinaMessage in) {
		List<MinaMessage> result = new ArrayList<MinaMessage>();
		String[] pollds = in.getCP().split(";");
		String threshold = getThresold(pollds);
		// uploadMsg
		MinaMessage uploadMsg = new MinaMessage(String.format(
				Constants.UPLOAD_RTN_PATTERN, in.getCN(), in.getPW(),
				in.getMN(), threshold));
		result.add(uploadMsg);

		// exeRtnMsg
		String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(),
				Constants.EXE_SUCCESS);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(
				Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(), exeRtnCp));
		result.add(exeRtnMsg);
		return result;
	}

	private String getThresold(String[] pollds) {
		// 待扩充
		return "QN=20040516010101001;101-LowValue=1.1,101-UpValue=1.1,102-UpValue=9.9...";
	}

	public static void main(String[] args) {
		Handler1021 handler = new Handler1021();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=1021;PW=123456;MN=88888880000001;Flag=3;CP=&&Polld=101;Polld=102...&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

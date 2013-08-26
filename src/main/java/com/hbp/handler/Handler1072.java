package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.Constants;
import com.hbp.manager.PasswordManager;
import com.hbp.message.MinaMessage;

/**
 * 设置现场机访问密码
 * <--- QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&
 * ---> ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 */
public class Handler1072 implements MinaHandler {

	protected Logger log = LoggerFactory.getLogger(Handler1072.class);

	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	public List<MinaMessage> handleMsg(MinaMessage in) {

		List<MinaMessage> result = new ArrayList<MinaMessage>();

		// service
		String newPass = in.getCpPara("PW");
		int exeRtn = PasswordManager.setPassword(in.getMN(), newPass);

		// exeRtnMsg
		String outCpContent = String.format(EXE_RTN_CP_PATTERN, in.getQN(), exeRtn);
		MinaMessage exeRtnMsg = new MinaMessage(String.format(Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(),
				outCpContent));
		result.add(exeRtnMsg);

		return result;
	}

	public static void main(String[] args) {
		Handler1072 handler = new Handler1072();
		List<MinaMessage> list = handler.handleMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

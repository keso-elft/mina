package com.hbp.receiver;

import com.hbp.ClientModule;
import com.hbp.Constants;
import com.hbp.message.MinaMessage;

public class MinaReceiver {

	public final static String REQUEST_PATTERN = "ST=91;CN=9011;PW=%s;MN=%s;Flag=0;CP=&&QN=%s;QnRtn=%d&&";

	public final static String RESPONSE_PATTERN = "ST=91;CN=9012;PW=%s;MN=%s;CP=&&QN=%s;ExeRtn=%d&&";

	/**
	 * 前置接收消息函数，返回接收成功或错误
	 */
	public MinaMessage returnMsg(MinaMessage msg) {

		// ST=91;CN=9011;PW=123456;MN=8888888880000001;FLAG=0;CP=&&QN=20040516010101001;QnRtn=1&&
		// QnRtn=1成功2失败3密码错误
		MinaMessage rtnMsg = null;
		if (msg.isRequest()) {
			int qnRtn = Constants.QN_SUCCESS;
			if (!msg.getPW().equals(System.getProperty("system.password"))) {
				qnRtn = Constants.QN_WRONG_PASS;
			}
			// TODO QN_DENY错误
			rtnMsg = new MinaMessage(String.format(REQUEST_PATTERN, msg.getPW(), msg.getMN(), msg.getQN(), qnRtn));
		} else {
			int exeRtn = Constants.EXE_SUCCESS;
			// TODO 其他错误
			// TODO 少PW,MN,QN
			rtnMsg = new MinaMessage(String.format(RESPONSE_PATTERN, msg.getPW(), msg.getMN(), msg.getQN(), exeRtn));
		}

		return rtnMsg;
	}

	public static void main(String[] args) {
		MinaReceiver receiver = new MinaReceiver();

		ClientModule module = new ClientModule();
		module.loadProperties();

		System.out.println(receiver.returnMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&")));

		System.out.println(receiver.returnMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1072;PW=111111;MN=8888888880000001;Flag=3;CP=&&PW=654321&&")));

		System.out.println(receiver.returnMsg(new MinaMessage("ST=91;CN=9014;CP=&&QN=20040516010101001;CN=2051&&")));
	}
}

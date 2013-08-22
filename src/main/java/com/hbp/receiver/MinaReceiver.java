package com.hbp.receiver;

import com.hbp.ClientModule;
import com.hbp.Constants;
import com.hbp.message.MinaMessage;

public class MinaReceiver {

	/**
	 * 前置接收消息函数，根据收到消息类型，决定返回消息格式，以及接收成功或错误
	 */
	public MinaMessage replyMsg(MinaMessage msg) {

		MinaMessage rtnMsg = null;

		if (msg.isRequest()) {

			int qnRtn = Constants.QN_SUCCESS;
			
			// TODO 需要在validate判断
			if (!msg.getPW().equals(System.getProperty("system.password"))) {
				qnRtn = Constants.QN_WRONG_PASS;
			}
			// TODO 无处理QN_DENY

			rtnMsg = new MinaMessage(String.format(Constants.REPLY_REQUEST_PATTERN, msg.getPW(), msg.getMN(),
					msg.getQN(), qnRtn));
			rtnMsg.setResultCode(qnRtn);

		} else if (msg.isNotification()) {

			rtnMsg = new MinaMessage(String.format(Constants.REPLY_NOTIFICATION_PATTERN, msg.getPW(), msg.getMN(),
					msg.getQN()));

		} else if (msg.isDataReply()) {

			int exeRtn = Constants.EXE_SUCCESS;
			// TODO 无处理其他错误
			// TODO 少PW,MN,QN,需要从过去的消息中取..
			rtnMsg = new MinaMessage(String.format(Constants.REPLY_DATA_REPLY_PATTERN, msg.getPW(), msg.getMN(),
					msg.getQN(), exeRtn));
		}

		return rtnMsg;
	}

	public static void main(String[] args) {
		MinaReceiver receiver = new MinaReceiver();

		ClientModule module = new ClientModule();
		module.loadProperties();

		// 正确结果
		// ST=91;CN=9011;PW=123456;MN=8888888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=1&&
		// ST=91;CN=9011;PW=111111;MN=8888888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=3&&
		// ST=91;CN=9013;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001&&
		// ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&

		// REQUEST
		System.out.println(receiver.replyMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1072;PW=123456;MN=8888888880000001;Flag=3;CP=&&PW=654321&&")));
		System.out.println(receiver.replyMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=1072;PW=111111;MN=8888888880000001;Flag=3;CP=&&PW=654321&&")));
		// NOTIFICATION
		System.out.println(receiver.replyMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=2012;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&")));
		// DATA_REPLY
		System.out.println(receiver.replyMsg(new MinaMessage("ST=91;CN=9014;CP=&&QN=20040516010101001;CN=2051&&")));
	}
}

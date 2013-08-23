package com.hbp.receiver;

import com.hbp.ClientModule;
import com.hbp.Constants;
import com.hbp.MinaClient;
import com.hbp.handler.Handler1072;
import com.hbp.message.MinaMessage;

public class MinaReceiver {

	MinaClient client;

	public MinaReceiver(MinaClient client) {
		super();
		this.client = client;
	}

	/**
	 * 前置接收消息函数，根据收到消息类型，决定返回消息格式，以及接收成功或错误
	 */
	public MinaMessage replyMsg(MinaMessage in) {

		MinaMessage rtnMsg = null;

		if (in.isRequest()) {

			int qnRtn = Constants.QN_SUCCESS;

			if (!in.getPW().equals(System.getProperty("system.password"))) {
				qnRtn = Constants.QN_WRONG_PASS;
			}
			if (in.getQN() == null || in.getMN() == null || in.getCN() == null || client == null
					|| client.getHandler(in.getCN()) == null)
				qnRtn = Constants.QN_DENY;

			rtnMsg = new MinaMessage(String.format(Constants.REPLY_REQUEST_PATTERN, in.getPW(), in.getMN(), in.getQN(),
					qnRtn));
			rtnMsg.setResultCode(qnRtn);

		} else if (in.isNotification()) {

			// 不判断密码/成功失败
			rtnMsg = new MinaMessage(String.format(Constants.REPLY_NOTIFICATION_PATTERN, in.getPW(), in.getMN(),
					in.getQN()));

		} else if (in.isDataReply()) {

			int exeRtn = Constants.EXE_SUCCESS;

			// client缓存获取之前的源消息
			String QN = in.getCpPara("QN");
			MinaMessage sourceMsg = client != null ? client.getHasReplyMsg(QN) : null;

			if (sourceMsg != null) {
				rtnMsg = new MinaMessage(String.format(Constants.REPLY_DATA_REPLY_PATTERN, sourceMsg.getPW(),
						sourceMsg.getMN(), sourceMsg.getQN(), exeRtn));
				// 删除缓存数据
				client.removeHasReplyMsg(QN);
			} else {
				exeRtn = Constants.EXE_NO_DATA;
				rtnMsg = new MinaMessage(String.format(Constants.REPLY_DATA_REPLY_PATTERN,
						System.getProperty("system.password"), "", QN, exeRtn));
			}
		}

		return rtnMsg;
	}

	public static void main(String[] args) {

		ClientModule module = new ClientModule();
		module.loadProperties();

		MinaClient client = new MinaClient("");
		MinaReceiver receiver = client.getReceiver();

		client.putHasReplyMsg(new MinaMessage(
				"QN=20040516010101001;ST=32;CN=2011;PW=123456;MN=8888888880000001;Flag=3;CP=&&&&"));
		client.addHandler("1072", new Handler1072());

		// 正确结果
		// ST=91;CN=9011;PW=123456;MN=8888888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=1&&
		// ST=91;CN=9011;PW=111111;MN=8888888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=3&&
		// ST=91;CN=9013;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001&&
		// ST=91;CN=9012;PW=123456;MN=8888888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
		// ST=91;CN=9012;PW=123456;CP=&&QN=20040516010101001;ExeRtn=3&&

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
		System.out.println(receiver.replyMsg(new MinaMessage("ST=91;CN=9014;CP=&&QN=20040516010101001;CN=2051&&")));
	}
}

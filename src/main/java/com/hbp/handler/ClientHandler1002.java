package com.hbp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.message.MinaMessage;

public class ClientHandler1002 implements MinaHandler {

	protected static Logger log = LoggerFactory.getLogger(ClientHandler1002.class);

	@Override
	public MinaMessage handleMsg(MinaMessage msg) {
		if (msg != null) {
			log.info("客户端收到返回Mina消息:" + msg);
			// TODO 接收消息并发送到队列
			log.info("客户端将消息加入存储数据库队列:" + msg);
		}
		return null;
	}
}
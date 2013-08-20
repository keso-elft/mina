package com.hbp.handler;

import com.hbp.message.MinaMessage;

public interface MinaHandler {

	/**
	 * 处理消息函数，如果需要写返回消息，放入返回的消息即可, HandlerAdapter类里面会自动进行回写。
	 */
	public MinaMessage handleMsg(MinaMessage msg);
}

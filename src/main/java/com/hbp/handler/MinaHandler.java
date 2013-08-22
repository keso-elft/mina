package com.hbp.handler;

import java.util.List;

import com.hbp.message.MinaMessage;

public interface MinaHandler {
	/**
	 * 后置处理消息并回复结果函数
	 */
	public List<MinaMessage> handleMsg(MinaMessage in);
}

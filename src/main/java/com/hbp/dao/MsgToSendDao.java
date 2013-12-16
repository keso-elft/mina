package com.hbp.dao;

import java.util.List;

import com.hbp.model.MsgToSend;

@SuppressWarnings("rawtypes")
public interface MsgToSendDao {

	public List getMsgToSend();

	public void delete(MsgToSend msg);

}

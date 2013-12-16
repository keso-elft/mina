package com.hbp.dao.impl;

import java.util.List;

import com.hbp.dao.MsgToSendDao;
import com.hbp.model.MsgToSend;

@SuppressWarnings({ "rawtypes" })
public class MsgToSendDaoImpl extends SuperDao implements MsgToSendDao {

	public List getMsgToSend() {
		return getHibernateTemplate().find("from MsgToSend");
	}

	public void delete(MsgToSend msg) {
		getHibernateTemplate().delete(msg);
	}

}

package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * 设置设备采样时间周期 <---
 * QN=20040516010101001;ST=32;CN=3014;PW=123456;MN=88888880000001
 * ;Flag=3;CP=&&PollD=101,CTime=04,CTime=10,CTime=14,CTime=16&& --->
 * ST=91;CN=9011
 * ;PW=123456;MN=88888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=1&& --->
 * ST
 * =91;CN=9012;PW=123456;MN=88888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 * 
 * @author Administrator
 * 
 */
public class Handler3014 implements MinaHandler {

	protected Logger log = LoggerFactory.getLogger(Handler3012.class);
	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";

	@Override
	public List<MinaMessage> handleMsg(MinaMessage in) {
		List<MinaMessage> result = new ArrayList<MinaMessage>();
		String polldAndTimes = in.getCP();

		if (setPolldAndTimes(polldAndTimes)) {
			// exeRtnMsg
			String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(),
					Constants.EXE_SUCCESS);
			MinaMessage exeRtnMsg = new MinaMessage(
					String.format(Constants.EXE_RTN_PATTERN, in.getPW(),
							in.getMN(), exeRtnCp));
			result.add(exeRtnMsg);
		}
		return result;
	}

	private boolean setPolldAndTimes(String polldAndTimes) {
		// TODO Auto-generated method stub
		return true;
	}

	public static void main(String[] args) {
		Handler3014 handler = new Handler3014();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=3014;PW=123456;MN=88888880000001;Flag=3;CP=&&PollD=101,CTime=04,CTime=10,CTime=14,CTime=16&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

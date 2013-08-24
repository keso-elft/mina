package com.hbp.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hbp.Constants;
import com.hbp.message.MinaMessage;

/**
 * <--- QN=20040516010101001;ST=32;CN=1000;PW=123456;MN=88888880000001;Flag=3;CP=&&OverTime=5;Recount=3&&
 * ---> ST=91;CN=9011;PW=123456;MN=88888880000001;Flag=0;CP=&&QN=20040516010101001;QnRtn=1&&
 * ---> ST=91;CN=9012;PW=123456;MN=88888880000001;CP=&&QN=20040516010101001;ExeRtn=1&&
 * @author Administrator
 *
 */
public class Handler1000 implements MinaHandler{
	
	protected Logger log = LoggerFactory.getLogger(Handler1000.class);
	public final static String EXE_RTN_CP_PATTERN = "QN=%s;ExeRtn=%d";
	
	@Override
	public List<MinaMessage> handleMsg(MinaMessage in) {
		
		List<MinaMessage> result = new ArrayList<MinaMessage>();
		String overTimeAndRecount = in.getCP();
		if(setTimeAndRecount(overTimeAndRecount)) {
			// exeRtnMsg
			String exeRtnCp = String.format(EXE_RTN_CP_PATTERN, in.getQN(),
					Constants.EXE_SUCCESS);
			MinaMessage exeRtnMsg = new MinaMessage(String.format(
					Constants.EXE_RTN_PATTERN, in.getPW(), in.getMN(), exeRtnCp));
			result.add(exeRtnMsg);
		}
			
		return result;
	}

	private boolean setTimeAndRecount(String overTimeAndRecount) {
		// TODO Auto-generated method stub
		return true;
	}

	public static void main(String[] args) {
		Handler1000 handler = new Handler1000();
		List<MinaMessage> list = handler
				.handleMsg(new MinaMessage(
						"QN=20040516010101001;ST=32;CN=1000;PW=123456;MN=88888880000001;Flag=3;CP=&&OverTime=5;Recount=3&&"));
		for (MinaMessage msg : list) {
			System.out.println(msg);
		}
	}
}

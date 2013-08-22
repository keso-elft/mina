package com.hbp;

public class Constants {

	public final static int QN_SUCCESS = 1;
	public final static int QN_DENY = 2;
	public final static int QN_WRONG_PASS = 3;

	public final static int EXE_SUCCESS = 1;
	public final static int EXE_FAIL = 2;
	public final static int EXE_NO_DATA = 3;

	public final static String UPLOAD_RTN_PATTERN = "ST=32;CN=%s;PW=%s;MN=%s;CP=&&%s&&";
	public final static String EXE_RTN_PATTERN = "ST=91;CN=9012;PW=%s;MN=%s;CP=&&%s&&";
}

package com.ashesi.cs.mhealth.knowledge;

public class LogD {
	private int id;
	private int log_code;
	private String log_date;
	private String username;
	private String source;
	private String msg;
	
	public LogD(int id, int log_code, String date, String username, String source, String msg){
		this.id = id;
		this.log_code = log_code;
		this.log_date = date;
		this.username = username;
		this.source = source;
		this.msg = msg;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the log_code
	 */
	public int getLog_code() {
		return log_code;
	}

	/**
	 * @param log_code the log_code to set
	 */
	public void setLog_code(int log_code) {
		this.log_code = log_code;
	}

	/**
	 * @return the log_date
	 */
	public String getLog_date() {
		return log_date;
	}

	/**
	 * @param log_date the log_date to set
	 */
	public void setLog_date(String log_date) {
		this.log_date = log_date;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}

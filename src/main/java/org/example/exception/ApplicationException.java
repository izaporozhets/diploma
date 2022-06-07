package org.example.exception;

import org.example.enums.ApplicationError;

public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -6772091593303257397L;

	private Integer code;
	private String data;

	public ApplicationException() {
	}

	public ApplicationException(Integer code, String message) {
		super(message);
		this.code = code;
	}


	public ApplicationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ApplicationException(ApplicationError errorContent, String data) {
		super(errorContent.getMessage());
		this.code = errorContent.getErrorCode();
		this.data = data;
	}

	public ApplicationException(ApplicationError errorContent) {
		super(errorContent.getMessage());
		this.code = errorContent.getErrorCode();
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
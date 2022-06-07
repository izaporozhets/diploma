package org.example.enums;

public enum ApplicationError {

	USER_DOES_NOT_EXIST("Користувача не знайдено", -6001),
	STUDENT_DOES_NOT_EXIST("Студента не знајдено", -6002);

	ApplicationError(String message, int errorCode) {
		this.message = message;
		this.errorCode = errorCode;
	}

	private String message;
	private int errorCode;

	public String getMessage() {
		return message;
	}

	public int getErrorCode() {
		return errorCode;
	}
}

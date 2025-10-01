package com.syncly.syncly.wrapper;

import lombok.Data;

@Data
public class ServiceResponse <T> {
	private ResStatus status = ResStatus.SUCCESS;
	private String message;
	private T data;
	private String errorStackTrace;
	private int statusCode;

	 

	@Override
	public String toString() {
		return String.format("Response [data=%s, message=%s, status=%s]", data, message, status);
	}
	public enum ResStatus {
		SUCCESS, WARNING, ERROR;

		public String toString() {
			return this.name().toLowerCase();
		}
	}
}

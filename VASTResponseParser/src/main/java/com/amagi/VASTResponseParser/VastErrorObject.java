package com.amagi.VASTResponseParser;

/**
 * Created by priyanka. All the errors while executing functions of
 * com.amagi.validateXML.VastXmlUtils are returned as a list of
 * com.amagi.validateXML.VastErrorObject in vastErrorObjectList of
 * com.amagi.validateXML.VastReturnObject
 */
public class VastErrorObject {
	/** The predefined error which occurred while executing function */
	private ErrorEnum error;
	/** Custom message to be added alongwith the error for debugging */
	private String customMessage;
	/** The VAST URL where error occurred */
	private String errorUrl;
	private static final String SPACE_DELIMITER = " ";

	public VastErrorObject(ErrorEnum errorEnum, String errorMessage) {
		error = errorEnum;
		customMessage = errorMessage;
	}

	public VastErrorObject(ErrorEnum errorEnum) {
		error = errorEnum;
	}

	public void setErrorUrl(String url) {
		errorUrl = url;
	}

	public void setCustomMessage(String errorMessage) {
		customMessage = errorMessage;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public ErrorEnum getError() {
		return error;
	}

	@Override
	public String toString() {
		StringBuilder errorMsg = new StringBuilder(error.getErrorMessage());
		if (null != customMessage) {
			errorMsg.append(SPACE_DELIMITER).append(customMessage);
		}
		if (null != errorUrl) {
			errorMsg.append(SPACE_DELIMITER).append(errorUrl);
		}
		return errorMsg.toString();
	}
}
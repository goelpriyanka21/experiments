package com.amagi.VASTResponseParser;

/**
 * Created by priyanka.
 */
public enum ErrorEnum {
	URL_INVALID(1010, "INVALID URL"), URL_CANT_CONNECT(1011, "CANT CONNECT TO URL"), CORS_HEADER_NOT_SET(1012,
			"VAST RESPONSE DOES NOT HAVE CORS HEADER SET"), MANDATORY_HEADER_NOT_SET(1013,
					"MANDATORY HEADERS NOT SET"), URL_INVALID_PROTOCOL(1014, "INVALID URL PROTOCOL"),

	VAST_NOT_VERSION_COMPLIANT(1020, "VAST XML VERSION NOT SUPPORTED"),

	XML_PARSING_EXCEPTION(1030, "XML PARSING EXCEPTION"), XML_NOT_XSD_COMPLIANT(1045,
			"VAST XML SHOULD BE VAST XSD COMPLIANT"),

	WRAPPER_MAX_LIMIT_EXCEEDED(1040, "WRAPPER XMLS EXCEEDED MAX ALLOWED WRAPPER XML COUNT"), NOT_WRAPPER_XML(1049,
			"XML NOT A WRAPPER XML"),

	MULTIPLE_AD_OBJECT(1050, "VAST XML SHOULD CONTAIN ONLY ONE AD OBJECT"), NO_INLINE_OBJECT(1060,
			"VAST XML SHOULD CONTAIN EXACTLY ONE INLINE OBJECT INSIDE AD OBJECT"), MULTIPLE_CREATIVE_LINEAR_OBJECT(1070,
					"VAST XML SHOULD CONTAIN ONLY A SINGLE <CREATIVE><LINEAR></LINEAR></CREATIVE> OBJECT INSIDE INLINE OBJECT"), VIDEO_DURATION_EXCEEDS_ALLOWED_DURATION(
							1080, "VAST VIDEO DURATION EXCEEDS MAX ALLOWED DURATION"), NO_VALID_MEDIA_FILE(1090,
									"NO VALID MEDIA FILE PRESENT INSIDE VAST XML"),

	DEV_SETUP_ERROR(0, "Unexpected error in params passed or dev setup");

	private final int errorCode;
	private final String errorMessage;

	ErrorEnum(int errorCd, String errorMsg) {
		this.errorCode = errorCd;
		this.errorMessage = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}

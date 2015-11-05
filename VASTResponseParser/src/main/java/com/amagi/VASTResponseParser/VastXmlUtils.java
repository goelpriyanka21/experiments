package com.amagi.VASTResponseParser;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * Created by priyanka. This class contains util functions to validate a VAST
 * XML URI
 * 
 */
public class VastXmlUtils {

	/** Max count of wrapper XML's permitted in the VAST URI to be validated */
	public final int WRAPPER_XML_MAX_LIMIT;
	/** Max duration of video in the VAST file */
	public Date VIDEO_DURATION_MAX_LIMIT;
	/** List of Applicable connection protocols for VAST URL */
	public final List<String> VALID_CONNECTION_PROTOCOLS;
	/** VAST tag in VAST XML */
	private static final String VAST_TAG = "VAST";
	/** Tag of Current version in VAST XML */
	private static final String VAST_VERSION_ATTRIBUTE = "version";
	/**
	 * Tag of Second-level element surrounding wrapper ad pointing to Secondary
	 * ad server in VAST XML
	 */
	private static final String WRAPPER_TAG = "Wrapper";
	/**
	 * Tag which has URL of ad tag of downstream Secondary Ad Server in VAST XML
	 */
	private static final String VAST_AD_TAG_URI_TAG = "VASTAdTagURI";
	/** Tag of Top-level element, wraps each ad in the response in VAST XML */
	private static final String AD_TAG = "Ad";
	/**
	 * Tag of Second-level element surrounding complete ad data for a single ad
	 * in VAST XML
	 */
	private static final String INLINE_TAG = "InLine";
	/**
	 * Tag that Wraps each creative element within an InLine or Wrapper Ad in
	 * VAST XML
	 */
	private static final String CREATIVE_TAG = "Creative";
	/** Tag which has in VAST XML */
	private static final String LINEAR_TAG = "Linear";
	/** Tag which has Duration in standard time format, hh:mm:ss in VAST XML */
	private static final String DURATION_TAG = "Duration";
	/** Tag which has Location of linear file in VAST XML */
	private static final String MEDIA_FILE_TAG = "MediaFile";
	/** Tag which has Bitrate of encoded video in Kbps in VAST XML */
	private static final String BITRATE_TAG = "bitrate";
	/** Tag which has Pixel width of video in VAST XML */
	private static final String WIDTH_TAG = "width";
	/** Tag which has Pixel height of video in VAST XML */
	private static final String HEIGHT_TAG = "height";
	private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
	/**
	 * Map of vast Allowed Version and file path of their XML Schema Definitions
	 */
	private static Map<String, String> VAST_ALLOWED_VERSIONS_XSD_MAP;
	private static final String SPACE_DELIMITER = " ";
	private final List<String> MANDATOR_HEADER_KEYS;

	public String vastVersionGlobal;

	/**
	 * Default constructor of com.amagi.validateXML.VastXmlUtils<br>
	 * Sets the following default values<br>
	 * WRAPPER_XML_MAX_LIMIT = 4<br>
	 * VIDEO_DURATION_MAX_LIMIT = 30<br>
	 * VALID_CONNECTION_PROTOCOLS = [http, https]<br>
	 * VAST_ALLOWED_VERSIONS_XSD_MAP = {"2.0" :
	 * "src/main/resources/vast_2.0.1.xsd", "3.0" :
	 * "src/main/resources/vast3_draft.xsd"}
	 */
	public VastXmlUtils() {
		WRAPPER_XML_MAX_LIMIT = 1;
		try {
			VIDEO_DURATION_MAX_LIMIT = new SimpleDateFormat("HH:mm:ss").parse("00:00:30");
		} catch (ParseException exception) {
			//// exception while converting videoDurationMaxLimit to date
		}
		VALID_CONNECTION_PROTOCOLS = new ArrayList<String>();
		VALID_CONNECTION_PROTOCOLS.add("http");
		VALID_CONNECTION_PROTOCOLS.add("https");
		VAST_ALLOWED_VERSIONS_XSD_MAP = new HashMap<String, String>();
		VAST_ALLOWED_VERSIONS_XSD_MAP.put("2.0", "/vast_2.0.1.xsd");
		VAST_ALLOWED_VERSIONS_XSD_MAP.put("3.0", "/vast3_draft.xsd");
		MANDATOR_HEADER_KEYS = new ArrayList<String>();
		MANDATOR_HEADER_KEYS.add(CONTENT_TYPE_HEADER_KEY);
	}

	/**
	 * Constructor of vastXmlUtils
	 *
	 * @param wrapperXmlMaxLimit
	 *            Max count of wrapper XML's permitted in the VAST URI to be
	 *            validated
	 * @param videoDurationMaxLimit
	 *            Max duration of video in the VAST file in HH:mm:ss format
	 * @param validConnectionProtocols
	 *            List of Applicable connection protocols for VAST URL, Eg.
	 *            [http, https]
	 * @param vastAllowedVersionsXsdMap
	 *            Map of vast Allowed Version and file path of their XML Schema
	 *            Definitions
	 */
	public VastXmlUtils(int wrapperXmlMaxLimit, String videoDurationMaxLimit,
			ArrayList<String> validConnectionProtocols, Map<String, String> vastAllowedVersionsXsdMap,
			ArrayList<String> mandatoryHeaderKeyList) {
		WRAPPER_XML_MAX_LIMIT = Math.max(wrapperXmlMaxLimit, 0);
		try {
			VIDEO_DURATION_MAX_LIMIT = new SimpleDateFormat("HH:mm:ss").parse(videoDurationMaxLimit);
		} catch (ParseException exception) {
			// exception while converting videoDurationMaxLimit to date
		}
		VALID_CONNECTION_PROTOCOLS = validConnectionProtocols;
		VAST_ALLOWED_VERSIONS_XSD_MAP = vastAllowedVersionsXsdMap;
		MANDATOR_HEADER_KEYS = mandatoryHeaderKeyList;
	}

//	/**
//	 * Function to validate if the vastUrl is a valid URL
//	 *
//	 * @param vastUrl
//	 *            Vast Url to Validate
//	 * @param validConnectionProtocols
//	 *            List of Applicable connection protocols for VAST URL, Eg.
//	 *            [http, https]
//	 * @return Returns a com.amagi.validateXML.VastReturnObject. <br>
//	 *         ValObject is set to true if valid URL <br>
//	 *         com.amagi.validateXML.VastErrorObject is set with error code 1 if
//	 *         invalid URL
//	 */
//	public VastReturnObject validateVastUrl(String vastUrl, String[] validConnectionProtocols) {
//		
//		VastReturnObject vastReturnObject = new VastReturnObject();
//		Boolean isValidUrl = false;
//		UrlValidator urlValidator = new UrlValidator(validConnectionProtocols);
//		if (urlValidator.isValid(vastUrl)) {
//			System.out.println(vastUrl +" is valid");
//			isValidUrl = true;
//			vastReturnObject.setVastReturnCode(VastReturnCodeEnum.OK);
//		} else {
//			System.out.println(vastUrl +" is invalid");
//			StringBuilder errorMessage = new StringBuilder();
//			errorMessage.append("Url:").append(SPACE_DELIMITER).append(vastUrl).append(SPACE_DELIMITER)
//					.append("Allowed Protocols").append(validConnectionProtocols.toString());
//			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.URL_INVALID, errorMessage.toString());
//			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
//		}
////		vastReturnObject.setValObject(isValidUrl);
//		return vastReturnObject;
//	}

	/**
	 * Function to check if mandatory header params are present with correct
	 * values, will return false even if one header is not present
	 *
	 * @param headers
	 *            Response headers while connecting to the URL
	 * @return Returns a returnObject. ErrorObject will have one error for each
	 *         mandatory header param not present
	 */
	public VastReturnObject checkIfMandatorHeadersPresent(Map<String, List<String>> headers) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		vastReturnObject.setVastReturnCode(VastReturnCodeEnum.OK);
		for (String expectedMandatorHeaderKey : MANDATOR_HEADER_KEYS) {
			List<String> mandatoryHeaderValues = headers.get(expectedMandatorHeaderKey);
			if (mandatoryHeaderValues == null) {
				String errorMessage = "Mandatory header " + expectedMandatorHeaderKey + " not set for URL: ";
				VastErrorObject errorObject = new VastErrorObject(ErrorEnum.MANDATORY_HEADER_NOT_SET, errorMessage);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, errorObject);
			}

		}
		return vastReturnObject;
	}

	public VastReturnObject validateURLScheme(URL vastUrl) {
		VastReturnObject vastReturnObject = new VastReturnObject();
		try {
			URI vastUri = vastUrl.toURI();
			String scheme = vastUri.getScheme().toLowerCase();
			if (!VALID_CONNECTION_PROTOCOLS.contains(scheme)) {
				String errorMessage = "URL Protocol " + scheme
						+ " not supported. Only http/https are supported for URL: ";
				VastErrorObject errorObject = new VastErrorObject(ErrorEnum.URL_INVALID_PROTOCOL, errorMessage);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, errorObject);
			} else {
				vastReturnObject.setValObjectAndCode(scheme, VastReturnCodeEnum.OK);
			}
		} catch (URISyntaxException exception) {
			String errorMessage = "URL not in proper format. Only http/https are supported";
			VastErrorObject errorObject = new VastErrorObject(ErrorEnum.URL_INVALID_PROTOCOL, errorMessage);
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, errorObject);
		}
		return vastReturnObject;
	}

	/**
	 * Function to establish connection with the VAST URL
	 *
	 * @param vastUrlStr
	 *            Vast Url
	 * @return Returns a returnObject.<br>
	 *         ValObject is an InputStream if connection successful<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 1.1
	 *         if connection unsuccessful
	 */
	public VastReturnObject canConnectToUrl(String vastUrlStr) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		try {
			String CORS_HEADER_KEY = "Access-Control-Allow-Origin";
			List<String> CORS_HEADER_EXPECTED_VALUE = Arrays.asList("*", "http://www.amagi.com",
					"https://www.amagi.com", "amagi.com", "www.amagi.com");

			// validateURLScheme
			URL vastUrl = new URL(vastUrlStr);
			VastReturnObject schemeReturnObject = validateURLScheme(vastUrl);
			if (schemeReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR) {
				vastReturnObject.setVastReturnCode(VastReturnCodeEnum.ERROR);
				vastReturnObject.setVastErrorObjectList(schemeReturnObject.getVastErrorObjectList());
				return vastReturnObject;
			}
			
			URLConnection urlConnection = vastUrl.openConnection();
			vastReturnObject.setValObjectAndCode(urlConnection.getInputStream(), VastReturnCodeEnum.OK);
			Map<String, List<String>> headers = urlConnection.getHeaderFields();
			List<String> corsList = headers.get(CORS_HEADER_KEY);
			if (corsList == null || CollectionUtils.intersection(corsList, CORS_HEADER_EXPECTED_VALUE).isEmpty()) {
				String warningMessage = "CORS header not set for URL: " + vastUrlStr;
				VastErrorObject warningObject = new VastErrorObject(ErrorEnum.CORS_HEADER_NOT_SET, warningMessage);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.WARNING, warningObject);
				
				// Check if mandatory header params are present with correct values
				VastReturnObject mandatoryParamsReturnObject = checkIfMandatorHeadersPresent(headers);
				// Add appropriate error codes, if checkIfMandatorHeadersPresent returned an error
				if (mandatoryParamsReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR) {
					for (VastErrorObject vastErrorObject : mandatoryParamsReturnObject.getVastErrorObjectList()) {
						vastErrorObject.setCustomMessage(vastErrorObject.getCustomMessage() + vastUrlStr);
						vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
					}
				}
			}
		} catch (IOException exception) {
			String errorMessage = "URL: " + vastUrlStr;
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.URL_CANT_CONNECT, errorMessage);
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		}
		return vastReturnObject;
	}

	/**
	 * Function to get NodeList of elements by tag Name from the first element
	 * of a nodeList
	 *
	 * @param nodeList
	 *            All child elements of first object of this node are traversed
	 * @param tagName
	 *            All elements of type tagname will be fetched
	 * @return NodeList of objects which are fetched from the first element of
	 *         nodeList and are of type tagname
	 */
	public NodeList getChildTagsFromFirstElement(NodeList nodeList, String tagName) {
		Element firstElement = (Element) nodeList.item(0);
		NodeList elementNodeList = firstElement.getElementsByTagName(tagName);
		return elementNodeList;
	}

	/**
	 * Function to fetch mediaFiles with the following attributes from linear
	 * tag of VAST XML-<br>
	 * 1. Delivery attribute should be "progressive"<br>
	 * 2. Type attribute should be "video/mp4"
	 *
	 * @param linearElement
	 *            The linear DOM element of VAST XML for which the media files
	 *            are to be fetched
	 * @return Returns a com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject contains List<MediaObjects> if applicable media files
	 *         are found<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 9 if
	 *         no applicable media files are found<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 if
	 *         linearElement is null
	 */
	public VastReturnObject fetchMediaFiles(Element linearElement) {
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (null == linearElement) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Linear Element should not be NULL");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} else {
			NodeList mediaFileElementList = linearElement.getElementsByTagName(MEDIA_FILE_TAG);
			List<VastMediaObject> validMediaUrlList = new ArrayList<VastMediaObject>();
			int mediaFileElementListLength = mediaFileElementList.getLength();
			if (mediaFileElementListLength > 0) {
				// Traverse over the MediaFile objects
				for (int i = 0; i < mediaFileElementListLength; i++) {
					VastMediaObject vastMediaObject;
					Element mediaFileElement = (Element) mediaFileElementList.item(i);
					// Check if the MediaFile Object attributes satisfy these
					// constraints
					if ("progressive".equals(mediaFileElement.getAttribute("delivery"))
							&& "video/mp4".equals(mediaFileElement.getAttribute("type"))) {
						int width = Integer.parseInt(mediaFileElement.getAttribute(WIDTH_TAG));
						int height = Integer.parseInt(mediaFileElement.getAttribute(HEIGHT_TAG));
						String mediaUrl = mediaFileElement.getTextContent().trim();

						if (mediaFileElement.hasAttribute(BITRATE_TAG)) {
							int bitrate = Integer.parseInt(mediaFileElement.getAttribute(BITRATE_TAG));
							vastMediaObject = new VastMediaObject(mediaUrl, width, height, bitrate);
						} else {
							vastMediaObject = new VastMediaObject(mediaUrl, width, height);
						}
						validMediaUrlList.add(vastMediaObject);
					}
				}
			}

			// If no valid MediaFile found
			if (0 == validMediaUrlList.size()) {
				String errorMessage = "No Media File with progressive delivery and type video/mp4";
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.NO_VALID_MEDIA_FILE, errorMessage);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			} else {
				vastReturnObject.setValObjectAndCode(validMediaUrlList, VastReturnCodeEnum.OK);
			}
		}
		return vastReturnObject;
	}

	/**
	 * Function to parse VAST XML
	 *
	 * @param vastInputStream
	 *            VAST InputStream to be parsed
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject is set to DOM parent element if successfully parsed<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 3 if
	 *         there is an XML parsing exception<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0
	 *         for errors in parameter passing/setup<br>
	 */
	public VastReturnObject parseXml(InputStream vastInputStream) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (vastInputStream == null) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Input Stream Cannot Be Null");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			return vastReturnObject;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element parentElement;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(vastInputStream);
			parentElement = document.getDocumentElement();
			vastReturnObject.setValObjectAndCode(parentElement, VastReturnCodeEnum.OK);
		} catch (ParserConfigurationException exception) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR, exception.getMessage());
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} catch (SAXException exception) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.XML_PARSING_EXCEPTION, exception.getMessage());
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} catch (IOException exception) {
			String errorMessage = "IO Exception in input stream. " + exception.getMessage();
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR, errorMessage);
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		}
		return vastReturnObject;
	}

	/**
	 * Function to validate if the VAST file VERSION is compliant with the
	 * versions mentioned in VAST_ALLOWED_VERSIONS_XSD_MAP during initialization
	 *
	 * @param parentElement
	 *            Parent DOM element of VAST file
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         valObject is set to vastVersion if vast is Version Compliant<br>
	 *         Errorobject is set with error code 2 if vast is not Version
	 *         Compliant
	 */
	public VastReturnObject validateIfVastVersionCompliant(Element parentElement) {
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (parentElement == null) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Parent XML Element cannot be NULL");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} else {
			// check for VAST VERSION
			if (parentElement.getTagName() != VAST_TAG 
					|| !VAST_ALLOWED_VERSIONS_XSD_MAP.containsKey(parentElement.getAttribute(VAST_VERSION_ATTRIBUTE))) {
				String errorMessage = "Not VAST or version not compliant with "
						+ VAST_ALLOWED_VERSIONS_XSD_MAP.keySet();
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.VAST_NOT_VERSION_COMPLIANT,
						errorMessage);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			} else {
				String vastVersion = parentElement.getAttribute(VAST_VERSION_ATTRIBUTE);
				vastReturnObject.setValObjectAndCode(vastVersion, VastReturnCodeEnum.OK);
			}
		}
		return vastReturnObject;
	}

	/**
	 * Function to validate if the VAST InputStream is XSD compliant
	 *
	 * @param vastInputStream
	 *            vastInputStream of the VAST XML to validate
	 * @param vastXsdFile
	 *            XSD File to validate the VAST XSL inputStream
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject is set to true if VAST InputStream is XSD compliant<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 4.5
	 *         if VAST InputStream is not XSD compliant<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 if
	 *         either XSD file not found or vastInputStream null<br>
	 */
	public VastReturnObject validateIfVastXsdCompliant(InputStream vastInputStream, InputStream xsdInputStream) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (null != xsdInputStream) {
			Source xsdSource = new StreamSource(xsdInputStream);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema;
			Validator validator;
			try {
				schema = factory.newSchema(xsdSource);
				validator = schema.newValidator();
				StreamSource vastInputStreamSource = new StreamSource(vastInputStream);
				validator.validate(vastInputStreamSource);
				vastReturnObject.setValObjectAndCode(true, VastReturnCodeEnum.OK);
			} catch (IOException exception) {
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
						"IO Exception " + exception);
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			} catch (SAXException exception) {
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.XML_NOT_XSD_COMPLIANT, exception.getMessage());
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.WARNING, vastErrorObject);
			}
		} else {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR, "XSD File Not Found");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		}

		return vastReturnObject;
	}

	/**
	 * Function to unwrap wrapper XML and fetch AdTag Url of secondary Ad Server
	 * <br>
	 * The wrapper XML should satify the following constraints<br>
	 * 1. Only one Ad Object should be present in Wrapper XML
	 *
	 * @param parentElement
	 *            The parent DOM element of wrapper VAST XML
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject is the AdTag Url of secondary Ad Server<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 5 if
	 *         VAST XML has more than one Ad Object<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 4.99
	 *         if VAST XML is not a wrapper XML<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 if
	 *         there is no VASTAdTagURI inside Wrapper<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 if
	 *         parent DOM element is NULL
	 */
	public VastReturnObject unWrapWrapperXml(Element parentElement) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (null == parentElement) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Parent XML Element cannot be NULL");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} else {
			NodeList adElementList = parentElement.getElementsByTagName(AD_TAG);
			// Check if Wrapper XML has more than 1 Ad tag
			if (adElementList.getLength() != 1) {
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.MULTIPLE_AD_OBJECT, "Multiple Ad Object");
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			} else {
				NodeList wrapperElementList = getChildTagsFromFirstElement(adElementList, WRAPPER_TAG);
				// Check if VAST XML is a Wrapper XML
				if (wrapperElementList.getLength() > 0) {
					NodeList adTagUriElementList = getChildTagsFromFirstElement(wrapperElementList,
							VAST_AD_TAG_URI_TAG);
					// Check if Wrapper Tag contains VASTAdTagURI tag
					if (adTagUriElementList.getLength() > 0) {
						Element adTagUri = (Element) adTagUriElementList.item(0);
						vastReturnObject.setValObjectAndCode(adTagUri.getTextContent().trim(), VastReturnCodeEnum.OK);
					} else {
						String errorMessage = "No VASTAdTagURI element inside Wrapper, Not valid XML file";
						VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR, errorMessage);
						vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
					}
				} else {
					VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.NOT_WRAPPER_XML, "No Wrapper XML");
					vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
				}
			}
		}
		return vastReturnObject;
	}

	/**
	 * Function to process non-Wrapper XML and fetch media files<br>
	 * Does the following validations against non-Wrapper XML<br>
	 * 1. Parent XML element should not be NULL. ErrorCode set with error code 0
	 * otherwise.<br>
	 * 2. Exactly one Ad Object present in VAST XML. ErrorCode set with error
	 * code 5 otherwise.<br>
	 * 3. Ad Object should contain only one Inline Object in VAST XML. ErrorCode
	 * set with error code 6 otherwise.<br>
	 * 4. Inline Object should contain only one
	 * <Creative><Linear>..</Creative></Linear> tag. ErrorCode set with error
	 * code 7 otherwise.<br>
	 * 5. VAST Video Duration <= VIDEO_DURATION_MAX_LIMIT .
	 * VIDEO_DURATION_MAX_LIMIT specified in constructor. ErrorCode set with
	 * error code 8 otherwise.<br>
	 * 6. VAST file should have at least 1 MediaFile tag which is both
	 * progressive delivery and video/mp4 type. ErrorCode set with error code 9
	 * otherwise.
	 *
	 * 
	 * @param parentElement
	 *            The parent DOM element of non wrapper VAST XML
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject is set to List<MediObjects> which satisfy constraints
	 *         #11<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 in
	 *         case of unexpected internal error<br>
	 *         com.amagi.validateXML.VastErrorObject is set with appropriate
	 *         error code if mentioned constraints are not satisfied
	 */
	public VastReturnObject processAdXml(Element parentElement) {
		VastReturnObject vastReturnObject = new VastReturnObject();

		if (null == parentElement) {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Parent XML Element cannot be NULL");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		} else {
			NodeList adElementList = parentElement.getElementsByTagName(AD_TAG);
			// Check if non-wrapper XML exactly 1 Ad tag
			if (adElementList.getLength() != 1) {
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.MULTIPLE_AD_OBJECT, "Multiple Ad Object");
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			} else {
				Element adElement = (Element) adElementList.item(0);
				NodeList inLineElementList = adElement.getElementsByTagName(INLINE_TAG);
				// Check if Ad Object has an InLine Object
				if (inLineElementList.getLength() != 1) {
					VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.NO_INLINE_OBJECT, "No Inline Object");
					vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
				} else {
					NodeList creativeElementList = getChildTagsFromFirstElement(inLineElementList, CREATIVE_TAG);
					int linearCount = 0;
					// Traverse over all Creative elements
					for (int i = 0; i < creativeElementList.getLength(); i++) {
						Element creativeElement = (Element) creativeElementList.item(i);
						NodeList linearElementList = creativeElement.getElementsByTagName(LINEAR_TAG);
						// If Creative element has no Linear Tag, skip this
						// Creative Element
						if (linearElementList.getLength() != 1) {
							continue;
						}
						// Verify that creative element has only one linear
						// Object
						if (linearCount == 0) {
							linearCount ++;
							Element linearElement = (Element) linearElementList.item(0);
							NodeList durationElementList = linearElement.getElementsByTagName(DURATION_TAG);
							Element durationElement = (Element) durationElementList.item(0);
							try {
								Date videoDurationDate = new SimpleDateFormat("HH:mm:ss")
										.parse(durationElement.getTextContent());
								// Verify that video Duration does not exceed
								// VIDEO_DURATION_MAX_LIMIT
								if (videoDurationDate.after(VIDEO_DURATION_MAX_LIMIT)) {
									VastErrorObject vastErrorObject = new VastErrorObject(
											ErrorEnum.VIDEO_DURATION_EXCEEDS_ALLOWED_DURATION, "Video Duration Exceeds Allowed Duration");
									vastReturnObject.appendVastErrorObjectListAndCode(
											VastReturnCodeEnum.ERROR, vastErrorObject);
									break;
								}
							} catch (ParseException exception) {
								String errorMessage = "Parsing exception while fetching video duration";
								VastErrorObject vastErrorObject = new VastErrorObject(
										ErrorEnum.VIDEO_DURATION_EXCEEDS_ALLOWED_DURATION, errorMessage);
								vastReturnObject.appendVastErrorObjectListAndCode(
										VastReturnCodeEnum.ERROR, vastErrorObject);
								// error code 8
							}
							// Fetch all the mediaFiles from linear element
							vastReturnObject = fetchMediaFiles(linearElement);
						} else {
							
							VastErrorObject vastErrorObject = new VastErrorObject(
									ErrorEnum.MULTIPLE_CREATIVE_LINEAR_OBJECT, "Multiple Creative Linear Object");
							vastReturnObject.appendVastErrorObjectListAndCode(
									VastReturnCodeEnum.ERROR, vastErrorObject);
							// error code 7
						}
					}
					// No <Creative><Linear>..</Linear></Creative> found in
					// Inline Object
					if (linearCount == 0) {
						String errorMessage = "Inline Object should contain atleast one"
								+ " <Creative><Linear>..</Linear></Creative> Object";
						VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.MULTIPLE_CREATIVE_LINEAR_OBJECT,
								errorMessage);
						vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
					}
				}
			}
		}
		return vastReturnObject;
	}

	/**
	 * Function to get byteArray from vastInputStream
	 *
	 * @param vastInputStream
	 *            VAST InputStream to be converted into byteArray
	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
	 *         ValObject is set to byteArray<br>
	 *         com.amagi.validateXML.VastErrorObject is set with error code 0
	 *         for errors in parameter passing/setup
	 */
	public VastReturnObject getBytesFromInputStream(InputStream vastInputStream) {
		
		VastReturnObject vastReturnObject = new VastReturnObject();
		if (vastInputStream != null) {
			ByteArrayOutputStream vastByteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			try {
				while ((n = vastInputStream.read(buf)) > 0) {
					vastByteArrayOutputStream.write(buf, 0, n);
				}
				byte[] vastInputByteArray = vastByteArrayOutputStream.toByteArray();
				vastReturnObject.setValObjectAndCode(vastInputByteArray, VastReturnCodeEnum.OK);
			} catch (IOException exception) {
				VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
						"IO Exception while converting input stream to byte stream");
				vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
			}
		} else {
			VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.DEV_SETUP_ERROR,
					"Input Stream cannot be NULL");
			vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
		}
		return vastReturnObject;
	}

	private void setErrorUrl(VastReturnObject vastReturnObject, String errorUrl) {
		
		if (vastReturnObject!=null && vastReturnObject.getVastErrorObjectList()!=null) {
			List<VastErrorObject> vastErrorObjectList = vastReturnObject.getVastErrorObjectList();
			for (VastErrorObject vastErrorObject : vastErrorObjectList) {
				vastErrorObject.setErrorUrl(errorUrl);
			}
		}
	}
//
//	/**
//	 * Function to validate vastUrl and fetch MediaAssets after unwrapping the
//	 * XML and hopping to secondary Ad Servers <br>
//	 * Does the following validations against vastUrl<br>
//	 * 1. VAST URL is a valid URL, valid connection protocols specified as
//	 * VALID_CONNECTION_PROTOCOLS in constructor. ErrorCode set with error code
//	 * 1 otherwise.<br>
//	 * 2. VAST URL is reachable. ErrorCode set with error code 1.1 otherwise.
//	 * <br>
//	 * 3. VAST URL can be parsed into XML. ErrorCode set with error code 3
//	 * otherwise.<br>
//	 * 4. VAST URL is vast version compliant, version specified as keys of
//	 * VAST_ALLOWED_VERSIONS_XSD_MAP in constructor. ErrorCode set with error
//	 * code 2 otherwise.<br>
//	 * 5. VAST XML is compliant with VAST XSD, XSD path specified as values of
//	 * VAST_ALLOWED_VERSIONS_XSD_MAP against keys in constructor. ErrorCode set
//	 * with error code 4.5 otherwise.<br>
//	 * 6. Number of wrapper XML's are <= WRAPPER_XML_MAX_LIMIT. Value of
//	 * WRAPPER_XML_MAX_LIMIT specified in constructor. ErrorCode set with error
//	 * code 4 otherwise.<br>
//	 * 7. Only one Ad Object present in VAST XML. ErrorCode set with error code
//	 * 5 otherwise.<br>
//	 * 8. Ad Object should contain only one Inline Object in VAST XML. ErrorCode
//	 * set with error code 6 otherwise.<br>
//	 * 9. Inline Object should contain only one
//	 * <Creative><Linear>..</Creative></Linear> tag. ErrorCode set with error
//	 * code 7 otherwise.<br>
//	 * 10. VAST Video Duration <= VIDEO_DURATION_MAX_LIMIT .
//	 * VIDEO_DURATION_MAX_LIMIT specified in constructor. ErrorCode set with
//	 * error code 8 otherwise.<br>
//	 * 11. VAST file should have at least 1 MediaFile tag which is both
//	 * progressive delivery and video/mp4 type. ErrorCode set with error code 9
//	 * otherwise.
//	 *
//	 * @param vastUrl
//	 *            url of the VAST file
//	 * @return Returns com.amagi.validateXML.VastReturnObject.<br>
//	 *         ValObject is set to List<MediObjects> which satisfy constraint
//	 *         #11<br>
//	 *         com.amagi.validateXML.VastErrorObject is set with error code 0 in
//	 *         case of unexpected internal error<br>
//	 *         com.amagi.validateXML.VastErrorObject is set with appropriate
//	 *         error code if mentioned constraints are not satisfied
//	 */
	public VastReturnObject validateVastAndFetchAssets(String vastUrl) {
		System.out.println("\n\n Received new validateVastAndFetchAssets request, vastUrl is " + vastUrl);
		VastReturnObject vastReturnObject = null;
		List<VastErrorObject> returnObjectWarnings = new ArrayList<VastErrorObject>();
		InputStream vastInputStream;
		Element vastParentElement;
		byte[] vastInputByteArray;
		VastReturnObject funcVastReturnObject;
		String customErrorMessage;
		
		for (int i = 1; i <= WRAPPER_XML_MAX_LIMIT + 1; i++) {

			customErrorMessage = "Error occurred while fetching URL " + vastUrl;

			// Try establishing connection with VAST URL
			funcVastReturnObject = canConnectToUrl(vastUrl);
			// Check if return error code from function is ERROR
			if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR) {
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
				System.out.println(vastUrl + "got error in canConnectToUrl()");
				break;
			} else if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.WARNING) {
				// If Return Error Code from function is WARNING, add the warnings in Return Object
				setErrorUrl(funcVastReturnObject, vastUrl);
				returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
			}
			
			// Get the input stream returned from canConnectToUrl
			vastInputStream = (InputStream) funcVastReturnObject.getValObject();
			/*
			 * Convert the input Stream into a Byte Array. This is essential
			 * since same input stream is to be processed multiple times by
			 * different functions
			 */
			funcVastReturnObject = getBytesFromInputStream(vastInputStream);
			// Check if return error code from function is ERROR
			if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR ) {
				// Add custom error message for debugging if not already present
				if (funcVastReturnObject.getVastErrorObjectList().get(0).getCustomMessage() == null) {
					funcVastReturnObject.getVastErrorObjectList().get(0).setCustomMessage(customErrorMessage);
				}
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
				// Add all warnings to the error object
				vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
				System.out.println(vastUrl + "got error in getBytesFromInputStream()");
				break;
			} else if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.WARNING) {
				setErrorUrl(funcVastReturnObject, vastUrl);
				// If Return Error Code from function is WARNING, add the
				// warnings in Return Object
				returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
			}
			vastInputByteArray = (byte[]) funcVastReturnObject.getValObject();

			// Parse the VAST XML stored in ByteArray by passing it as an InputStream to parseXml()
			funcVastReturnObject = parseXml(new ByteArrayInputStream(vastInputByteArray));
			// Check if return error code from function is ERROR
			if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR) {
				// Add custom error message for debugging if not already present
				if (null == funcVastReturnObject.getVastErrorObjectList().get(0).getCustomMessage()) {
					funcVastReturnObject.getVastErrorObjectList().get(0).setCustomMessage(customErrorMessage);
				}
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
				// Add all warnings to the error object
				vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
				System.out.println(vastUrl + "got error in parseXml()");
				break;
			} else if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.WARNING) {
				setErrorUrl(funcVastReturnObject, vastUrl);
				// If Return Error Code from function is WARNING, add the
				// warnings in Return Object
				returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
			}
			// vastParentElement refers to the parent element of VAST XML
			vastParentElement = (Element) funcVastReturnObject.getValObject();

			
			/*
			 * Validate if the VAST URL contains a valid VAST File whose version
			 * -> xsd mapping is stored in VAST_ALLOWED_VERSIONS_XSD_MAP
			 */
			funcVastReturnObject = validateIfVastVersionCompliant(vastParentElement);
			// Check if return error code from function is ERROR
			if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.ERROR) {
				// Add custom error message for debugging if not already present
				if (funcVastReturnObject.getVastErrorObjectList().get(0).getCustomMessage() == null) {
					funcVastReturnObject.getVastErrorObjectList().get(0).setCustomMessage(customErrorMessage);
				}
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
				// Add all warnings to the error object
				vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
				System.out.println(vastUrl + "got error in validateIfVastVersionCompliant()");
				break;
			} else if (funcVastReturnObject.getVastReturnCode() == VastReturnCodeEnum.WARNING) {
				setErrorUrl(funcVastReturnObject, vastUrl);
				// If Return Error Code from function is WARNING, add the
				// warnings in Return Object
				returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
			}

			// Get the VAST VERSION returned from validateIfVastVersionCompliant
			String vastVersion = (String) funcVastReturnObject.getValObject();
			if (i == 1) {
				vastVersionGlobal = vastVersion;
			}
			// Get the VAST XSD corresponding to VAST version
			InputStream xsdInputStream = getClass().getResourceAsStream(VAST_ALLOWED_VERSIONS_XSD_MAP.get(vastVersion));

			// Validate VAST against XSD file
			funcVastReturnObject = validateIfVastXsdCompliant(new ByteArrayInputStream(vastInputByteArray),
					xsdInputStream);
			// Check if return error code from function is ERROR
			if (VastReturnCodeEnum.ERROR == funcVastReturnObject.getVastReturnCode()) {
				// Add custom error message for debugging if not already present
				if (null == funcVastReturnObject.getVastErrorObjectList().get(0).getCustomMessage()) {
					funcVastReturnObject.getVastErrorObjectList().get(0).setCustomMessage(customErrorMessage);
				}
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
				// Add all warnings to the error object
				vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
				System.out.println(vastUrl + "got error in validateIfVastXsdCompliant()");
				break;
			} else if (VastReturnCodeEnum.WARNING == funcVastReturnObject.getVastReturnCode()) {
				setErrorUrl(funcVastReturnObject, vastUrl);
				// If Return Error Code from function is WARNING, add the
				// warnings in Return Object
				returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
			}

			// unwrap wrapper xml
			funcVastReturnObject = unWrapWrapperXml(vastParentElement);
			// Check if return error code from function is ERROR
			if (VastReturnCodeEnum.ERROR == funcVastReturnObject.getVastReturnCode()) {
				System.out.println(vastUrl+"not a wrapper");
				// Add custom error message for debugging if not already present
				if (null == funcVastReturnObject.getVastErrorObjectList().get(0).getCustomMessage()) {
					funcVastReturnObject.getVastErrorObjectList().get(0).setCustomMessage(customErrorMessage);
				}
				VastErrorObject unWrapWrapperXmlVastErrorObject = funcVastReturnObject.getVastErrorObjectList().get(0);
				/*
				 * If XML is not a Wrapper XML, update the loop counter
				 * WRAPPER_XML_MAX_LIMIT. This will fetch Ad XML
				 */
				if (ErrorEnum.NOT_WRAPPER_XML == unWrapWrapperXmlVastErrorObject.getError()) {
					i = WRAPPER_XML_MAX_LIMIT + 1;
				} else {
					setErrorUrl(funcVastReturnObject, vastUrl);
					vastReturnObject = funcVastReturnObject;
					// Add all warnings to the error object
					vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
					break;
				}

			} else {
				System.out.println(vastUrl+ "a wrapper");
				// If Return Error Code from function is WARNING, add the
				// warnings in Return Object
				if (VastReturnCodeEnum.WARNING == funcVastReturnObject.getVastReturnCode()) {
					setErrorUrl(funcVastReturnObject, vastUrl);
					returnObjectWarnings.addAll(funcVastReturnObject.getVastErrorObjectList());
				}

				// VAST wrappers are more than allowed limit
				if (i > WRAPPER_XML_MAX_LIMIT) {
					System.out.println("VAST wrappers are more than allowed limit");
					VastErrorObject vastErrorObject = new VastErrorObject(ErrorEnum.WRAPPER_MAX_LIMIT_EXCEEDED,
							"Allowed Wrapper XML count is " + WRAPPER_XML_MAX_LIMIT);
					vastReturnObject = new VastReturnObject();
					vastReturnObject.appendVastErrorObjectListAndCode(VastReturnCodeEnum.ERROR, vastErrorObject);
					vastReturnObject.addWarningsToErrorList(returnObjectWarnings);
					// WRAPPER XML EXCEEDS MAX COUNT
					System.out.println(vastUrl + "got error in unWrapWrapperXml()");
					break;
				}
			}

			// Fetch the Ad XML
			if (i == WRAPPER_XML_MAX_LIMIT + 1) {
				System.out.println("XML reached WRAPPER_XML_MAX_LIMIT, so processAdXml");
				funcVastReturnObject = processAdXml(vastParentElement);
				// If Warning Status code was returned while retrieving Ad XML
				// from Wrappers, set return code to WARNING
				if (VastReturnCodeEnum.OK == funcVastReturnObject.getVastReturnCode()
						&& !returnObjectWarnings.isEmpty()) {
					System.out.println("XML is in warning state");
					funcVastReturnObject.setVastReturnCode(VastReturnCodeEnum.WARNING);
				}
				
				if (VastReturnCodeEnum.OK == funcVastReturnObject.getVastReturnCode()
						&& returnObjectWarnings.isEmpty()) {
					System.out.println("XML is perfectly ok & List of media files is" +funcVastReturnObject.getValObject());
				}
				// Add all warnings to the error object
				funcVastReturnObject.addWarningsToErrorList(returnObjectWarnings);
				setErrorUrl(funcVastReturnObject, vastUrl);
				vastReturnObject = funcVastReturnObject;
			} 
			else {
				// Set vastUrl fetched by unwrapping XML for next iteration of
				// unwrapping
				System.out.println("vastUrl for next iteration is "+ vastUrl);
				vastUrl = (String) funcVastReturnObject.getValObject();
			}
		}
		return vastReturnObject;
	}
}

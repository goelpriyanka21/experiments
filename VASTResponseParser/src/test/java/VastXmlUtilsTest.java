import com.amagi.VASTResponseParser.*;
import com.github.dreamhead.moco.HttpServer;

import static com.github.dreamhead.moco.Runner.runner;

import com.github.dreamhead.moco.Runner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.powermock.modules.junit4.PowerMockRunner;

import static com.github.dreamhead.moco.Moco.*;

/**
 * Created by priyanka.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(VastXmlUtils.class)
public class VastXmlUtilsTest {

	public VastXmlUtils testVastXmlUtils;
	private static Runner runner;
	HttpServer server;
	public VastReturnObject vastReturnObject;
	File inputXmlFile;
	InputStream inputXmlFileStream;

	@Before
	public void setupDefault() {
		System.out.println("setupDefault() ");
		String videoDurationMaxLimit = "00:00:30";
		ArrayList<String> validConnectionProtocols = new ArrayList<String>();
		validConnectionProtocols.add("http");
		validConnectionProtocols.add("https");
		Map<String, String> vastAllowedVersionsXsdMap = new HashMap<String, String>();
		vastAllowedVersionsXsdMap.put("2.0", "/vast_2.0.1.xsd");
		vastAllowedVersionsXsdMap.put("3.0", "/vast3_draft.xsd");
		ArrayList<String> mandatoryHeaderKeys = new ArrayList<String>();
		mandatoryHeaderKeys.add("Content-Type");
		testVastXmlUtils = new VastXmlUtils(1, videoDurationMaxLimit, validConnectionProtocols,
				vastAllowedVersionsXsdMap, mandatoryHeaderKeys);
		// Start a web server on localhost for testing
		System.out.println("Going to start http server");
		server = httpserver();
		System.out.println("http server started");
	}

	@Test
	public void testRun() {
		System.out.println("\nprocess ran successfully.");
	}

//	@Test
//	public void testValidateVastUrl() {
//		
//		System.out.println("\ntestValidateVastUrl() ");
//		VastReturnObject vastReturnObject = new VastReturnObject();
//		String[] validConnectionProtocols = new String[] { "http", "https" };
//
//		// valid URL
//		vastReturnObject = testVastXmlUtils.validateVastUrl(
//				"https://bs.serving-sys.com/BurstingPipe/adServer.bs?cn=is&c=23&pl=VAST&pli=13374914&PluID=0&pos=7759&ord=1234&cim=1",
//				validConnectionProtocols);
////		Assert.assertEquals(true, vastReturnObject.getValObject());
//		Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//
//		// Invalid protocol
//		vastReturnObject = testVastXmlUtils.validateVastUrl("ftp://random.com", validConnectionProtocols);
//		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//		Assert.assertEquals(ErrorEnum.URL_INVALID, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//		// Invalid URL
//		vastReturnObject = testVastXmlUtils.validateVastUrl("http://randomString", validConnectionProtocols);
//		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//		Assert.assertEquals(ErrorEnum.URL_INVALID, vastReturnObject.getVastErrorObjectList().get(0).getError());
//	}

//	@Test
//	public void testCanConnectToUrl() {
//		System.out.println("\ntestCanConnectToUrl() ");
//		String INPUT_XML_FILE = "src/test/resources/firstWrapper.xml";
//		server.request(by(uri("/headerToAll"))).response(header("Access-Control-Allow-Origin", "*"),
//				attachment("awesome.xml", file(INPUT_XML_FILE)));
//		// file(INPUT_XML_FILE)
//		server.request(by(uri("/headerToAmagi"))).response(
//				header("Access-Control-Allow-Origin", "http://www.amagi.com"),
//				attachment("awesome.xml", file(INPUT_XML_FILE)));
//		server.request(by(uri("/headerToGoogle"))).response(
//				header("Access-Control-Allow-Origin", "http://www.google.com"),
//				attachment("awesome.xml", file(INPUT_XML_FILE)));
//		server.request(by(uri("/noHeader"))).response(file(INPUT_XML_FILE));
//		server.request(by(uri("/urlIsDown"))).response(status(400));
//		runner = runner(server);
//		runner.start();
//		String serverUrl = "http://localhost:" + server.port();
//
//		try {
//			inputXmlFile = new File(INPUT_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			byte[] vastExpectedByteArray = (byte[]) testVastXmlUtils.getBytesFromInputStream(inputXmlFileStream)
//					.getValObject();
//			InputStream vastReturnInputStream;
//			byte[] vastActualByteArray;
//
//			// Can connect to URL, CORS header set to *
//			vastReturnObject = testVastXmlUtils.canConnectToUrl(serverUrl + "/headerToAll");
//			vastReturnInputStream = (InputStream) vastReturnObject.getValObject();
//			vastActualByteArray = (byte[]) testVastXmlUtils.getBytesFromInputStream(vastReturnInputStream)
//					.getValObject();
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertArrayEquals(vastExpectedByteArray, vastActualByteArray);
//
//			// Can connect to URL, CORS header set to http://www.amagi.com
//			vastReturnObject = testVastXmlUtils.canConnectToUrl(serverUrl + "/headerToAmagi");
//			vastReturnInputStream = (InputStream) vastReturnObject.getValObject();
//			vastActualByteArray = (byte[]) testVastXmlUtils.getBytesFromInputStream(vastReturnInputStream)
//					.getValObject();
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertArrayEquals(vastExpectedByteArray, vastActualByteArray);
//
//			// Can connect to URL, CORS header set to http://www.google.com
//			vastReturnObject = testVastXmlUtils.canConnectToUrl(serverUrl + "/headerToGoogle");
//			vastReturnInputStream = (InputStream) vastReturnObject.getValObject();
//			vastActualByteArray = (byte[]) testVastXmlUtils.getBytesFromInputStream(vastReturnInputStream)
//					.getValObject();
//			Assert.assertEquals(VastReturnCodeEnum.WARNING, vastReturnObject.getVastReturnCode());
//			Assert.assertArrayEquals(vastExpectedByteArray, vastActualByteArray);
//
//			// Can connect to URL, CORS header not set
//			vastReturnObject = testVastXmlUtils.canConnectToUrl(serverUrl + "/noHeader");
//			vastReturnInputStream = (InputStream) vastReturnObject.getValObject();
//			vastActualByteArray = (byte[]) testVastXmlUtils.getBytesFromInputStream(vastReturnInputStream)
//					.getValObject();
//			Assert.assertEquals(VastReturnCodeEnum.WARNING, vastReturnObject.getVastReturnCode());
//			Assert.assertArrayEquals(vastExpectedByteArray, vastActualByteArray);
//		} catch (IOException exception) {
//			System.out.println("IO Exception");
//		}
//
//		// Cannot connect to URL
//		vastReturnObject = testVastXmlUtils.canConnectToUrl(serverUrl + "/urlIsDown");
//		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//		Assert.assertEquals(ErrorEnum.URL_CANT_CONNECT, vastReturnObject.getVastErrorObjectList().get(0).getError());
//	}
//
//	@Test
//	public void testParseXml() {
//		System.out.println("\ntestParseXml()");
//		String VALID_XML_FILE = "src/test/resources/firstWrapper.xml";
//		String INVALID_XML_FILE = "src/test/resources/invalid.xml";
//		String INVALID_XML_FILE2 = "src/test/resources/invalid2.xml";
//		try {
//			// test for valid XML
//			inputXmlFile = new File(VALID_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//
//			// test for invalid XML1
//			inputXmlFile = new File(INVALID_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.XML_PARSING_EXCEPTION,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// test for invalid XML2
//			inputXmlFile = new File(INVALID_XML_FILE2);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.XML_PARSING_EXCEPTION,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// test for null inputStream
//			vastReturnObject = testVastXmlUtils.parseXml(null);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//		} catch (IOException exception) {
//
//		}
//	}
//
//	@Test
//	public void testValidateIfVastVersionCompliant() {
//		System.out.println("\ntestValidateIfVastVersionCompliant() ");
//		String VERSION2_XML_FILE = "src/test/resources/firstWrapper.xml";
//		String VERSION3_XML_FILE = "src/test/resources/version3.xml";
//		String VERSION4_XML_FILE = "src/test/resources/version4.xml";
//		String VERSION_NOT_SPECIFIED_XML_FILE = "src/test/resources/noVersion.xml";
//		String NOT_VAST_FILE = "src/test/resources/notVast.xml";
//		Element vastParentElement;
//		try {
//			// test for xml having VAST VERSION = 2
//			inputXmlFile = new File(VERSION2_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals("2.0", vastReturnObject.getValObject());
//
//			// test for xml having VAST VERSION = 3
//			inputXmlFile = new File(VERSION3_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals("3.0", vastReturnObject.getValObject());
//
//			// test for xml having VAST VERSION = 4
//			inputXmlFile = new File(VERSION4_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.VAST_NOT_VERSION_COMPLIANT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// test for xml having VAST VERSION not specified
//			inputXmlFile = new File(VERSION_NOT_SPECIFIED_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.VAST_NOT_VERSION_COMPLIANT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// test for xml which is not a VAST file
//			inputXmlFile = new File(NOT_VAST_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.VAST_NOT_VERSION_COMPLIANT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// parent element is NULL
//			vastReturnObject = testVastXmlUtils.validateIfVastVersionCompliant(null);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//		} catch (IOException exception) {
//
//		}
//	}
//
//	@Test
//	public void testValidateIfVastXsdCompliant() {
//		System.out.println("\ntestValidateIfVastXsdCompliant()");
//		String VERSION2_XML_FILE = "src/test/resources/firstWrapper.xml";
//		String VERSION3_XML_FILE = "src/test/resources/version4.xml";
//		String VERSION_NOT_SPECIFIED_XML_FILE = "src/test/resources/noVersion.xml";
//		String INVALID_VAST_XML_FILE = "src/test/resources/invalidVast.xml";
//
//		String VERSION2_XSD_FILE = "src/main/resources/vast_2.0.1.xsd";
//		String VERSION3_XSD_FILE = "src/main/resources/vast3_draft.xsd";
//		String NOT_EXISTS_XSD_FILE = "src/main/resources/not_exists.xsd";
//		File vastXsdSchema;
//		InputStream xsdXmlFileStream;
//		try {
//			// VAST version2 validated against XSD version2
//			inputXmlFile = new File(VERSION2_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastXsdSchema = new File(VERSION2_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(inputXmlFileStream, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(true, vastReturnObject.getValObject());
//
//			// VAST version3 validated against XSD version3
//			inputXmlFile = new File(VERSION3_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastXsdSchema = new File(VERSION3_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(inputXmlFileStream, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(true, vastReturnObject.getValObject());
//
//			// null inputStream validated against XSD version2
//			vastXsdSchema = new File(VERSION2_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(null, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST version2 validated against non existent xsd file
//			inputXmlFile = new File(VERSION2_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastXsdSchema = new File(NOT_EXISTS_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(inputXmlFileStream, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST with no version attrubute validated against XSD version2
//			inputXmlFile = new File(VERSION_NOT_SPECIFIED_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastXsdSchema = new File(VERSION2_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(inputXmlFileStream, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.WARNING, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.XML_NOT_XSD_COMPLIANT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			inputXmlFile = new File(INVALID_VAST_XML_FILE);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastXsdSchema = new File(VERSION2_XSD_FILE);
//			xsdXmlFileStream = new FileInputStream(vastXsdSchema);
//			vastReturnObject = testVastXmlUtils.validateIfVastXsdCompliant(inputXmlFileStream, xsdXmlFileStream);
//			Assert.assertEquals(VastReturnCodeEnum.WARNING, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.XML_NOT_XSD_COMPLIANT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//		} catch (IOException exception) {
//
//		}
//	}
//
//	@Test
//	public void testUnWrapWrapperXml() {
//		System.out.println("\ntestUnWrapWrapperXml()");
//		String WRAPPER_XML = "src/test/resources/firstWrapper.xml";
//		String NON_WRAPPER_XML = "src/test/resources/nonWrapper.xml";
//		String WRAPPER_WITH_NO_AD_TAG = "src/test/resources/noAdTagWrapper.xml";
//		Element vastParentElement;
//		URI expectedUri;
//		URI retreivedUri;
//		try {
//			// unwrap Wrapper XML
//			inputXmlFile = new File(WRAPPER_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.unWrapWrapperXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			retreivedUri = new URI((String) vastReturnObject.getValObject());
//			expectedUri = new URI(
//					"http://pr.ybp.yahoo.com/ab/secure/false/imp/VLKWKpF-yqYuIxXBgBOcVU-tfralEMxJvgIW8yC_y4fcbC58RypCSCtbray5i9H7NACSD2TwfaPlsWpQtEeNwvQ2PnIQJwFaW2drsKpoHI-YtuXbvSrhJC4LBVOCGBwwaDpTRFa3VwDCWBPOf3X-CGYmSq-SjAhuYHfB1NQx6-Qxhz_zVIWeE1IhoGdnq-fyhlhWpYjEvuPBS33r3YHdVWLNRMHkRAbprj-h5XxuFfm0JgI2RNaj9Hg_dASrxpgK0_grE1fZPfAGKjlTvGSLFbKCKaxLdY7jUeLDjmSvvpwi9-PdBI5DHAGqedOGjoxIyss8zthEVWZXqiQmnIih1eXVDumiPCwdJ85JzgLCGcBYFWScxbSuYLNNX7fmBwQklV8Md91B5x2_UBTBf056MZqSDwROqQQtxFUmZuJumhdhHfnaWl_vQGNirT2imlb7Mbsj_PyD4lTY7gNX8p8I_aQDEoOtx9vYE9OHXdhImjdxnWKUrjO2ciAucRWc43i-FkV0afSGyncS5WvH_l82uw1eoThaCUwJjSiHI6cAwnaeXmQG277ttrcrIur7FhqUSvgyhng1UEge0MWNuxJ3wpwUCKzsA87pYifghlF94Xku5JNDZIFXVbFIkp9TzPnlw-_Wj5RLqV2MnsQuBvJXIT-JzXxDHJklRky-l0upS05z5vbQ6iLz90hJtFV7LyzwJYy3qTAXtYeuX5ZuScqET2zpDpvR3zfS/wp/DA7AFFCA6F021CAB");
//			Assert.assertEquals(expectedUri, retreivedUri);
//
//			// unwrap non Wrapper XML
//			inputXmlFile = new File(NON_WRAPPER_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.unWrapWrapperXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.NOT_WRAPPER_XML, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// wrapper with no AdTagURI
//			inputXmlFile = new File(WRAPPER_WITH_NO_AD_TAG);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.unWrapWrapperXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// null parent Element passed to unwrapWrapperXML
//			vastReturnObject = testVastXmlUtils.unWrapWrapperXml(null);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//		} catch (URISyntaxException exception) {
//
//		} catch (IOException exception) {
//
//		}
//	}
//
//
//	@Test
//	public void testProcessAdXml() {
//		System.out.println("\ntestProcessAdXml()");
//		String NO_AD_XML = "src/test/resources/noAd.xml";
//		String MULTIPLE_AD_XML = "src/test/resources/twoAdTags.xml";
//		String WRAPPER_XML = "src/test/resources/firstWrapper.xml";
//		String NO_LINEAR_AD_XML = "src/test/resources/noLinearAd.xml";
//		String NO_CREATIVE_TAG_XML = "src/test/resources/noCreativeAd.xml";
//		String DURATION_EXCEEDS_XML = "src/test/resources/durationExceedsNonWrapper.xml";
//		String NO_MEDIA_FILES = "src/test/resources/noMediaFiles.xml";
//		String NO_APPLICABLE_MEDIA_FILES = "src/test/resources/noApplicableMediaFiles.xml";
//		String NO_WRAPPER_XML = "src/test/resources/nonWrapper.xml";
//		Element vastParentElement;
//		try {
//			// null parent Element passed to processAdXml
//			vastReturnObject = testVastXmlUtils.processAdXml(null);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.DEV_SETUP_ERROR, vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST XML with noAd TAG
//			inputXmlFile = new File(NO_AD_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.MULTIPLE_AD_OBJECT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST XML with multiple Ad TAGS
//			inputXmlFile = new File(MULTIPLE_AD_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.MULTIPLE_AD_OBJECT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST XML with no inline element
//			inputXmlFile = new File(WRAPPER_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.NO_INLINE_OBJECT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST XML with no linear ads
//			inputXmlFile = new File(NO_LINEAR_AD_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.MULTIPLE_CREATIVE_LINEAR_OBJECT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// VAST XML with no Creative tag
//			inputXmlFile = new File(NO_CREATIVE_TAG_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.MULTIPLE_CREATIVE_LINEAR_OBJECT,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// Video Duration Exceeds Upper Time Limit
//			inputXmlFile = new File(DURATION_EXCEEDS_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.VIDEO_DURATION_EXCEEDS_ALLOWED_DURATION,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// No Media Files
//			inputXmlFile = new File(NO_MEDIA_FILES);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.NO_VALID_MEDIA_FILE,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			// No Applicable Media Files
//			inputXmlFile = new File(NO_APPLICABLE_MEDIA_FILES);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
//			Assert.assertEquals(ErrorEnum.NO_VALID_MEDIA_FILE,
//					vastReturnObject.getVastErrorObjectList().get(0).getError());
//
//			inputXmlFile = new File(NO_WRAPPER_XML);
//			inputXmlFileStream = new FileInputStream(inputXmlFile);
//			vastReturnObject = testVastXmlUtils.parseXml(inputXmlFileStream);
//			vastParentElement = (Element) vastReturnObject.getValObject();
//			vastReturnObject = testVastXmlUtils.processAdXml(vastParentElement);
//			List<VastMediaObject> expectedMediaObjectList = buildMediaObjectList();
//			Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
//			List<VastMediaObject> mediaObjectList = (List<VastMediaObject>) vastReturnObject.getValObject();
//			Assert.assertArrayEquals(expectedMediaObjectList.toArray(), mediaObjectList.toArray());
//
//		} catch (Exception exception) {
//
//		}
//	}
//
	private List<VastMediaObject> buildMediaObjectList() {
		System.out.println("\nbuildMediaObjectList()");
		List<VastMediaObject> mediaObjectList = new ArrayList<VastMediaObject>();
		mediaObjectList.add(new VastMediaObject(
				"http://http.atlas.cdn.yimg.com/yamplus/video_q56ZjD12WJemSb7m7YY7yTvDk-VR4Gse2n-gLwvBUUa4bFAc08PpIsTcFOvpfUH_DcpgrasaGHU-_11.mp4?a=yamplus&mr=0&c=98453",
				640, 360, 699));
		mediaObjectList.add(new VastMediaObject(
				"http://http.atlas.cdn.yimg.com/yamplus/video_q56ZjD12WJemSb7m7YY7yTvDk-VR4Gse2n-gLwvBUUa4bFAc08PpIsTcFOvpfUH_DcpgrasaGHU-_8.mp4?a=yamplus&mr=0&c=98453",
				640, 360));
		mediaObjectList.add(new VastMediaObject(
				"http://http.atlas.cdn.yimg.com/yamplus/video_q56ZjD12WJemSb7m7YY7yTvDk-VR4Gse2n-gLwvBUUa4bFAc08PpIsTcFOvpfUH_DcpgrasaGHU-_9.mp4?a=yamplus&mr=0&c=98453",
				640, 360, 299));
		mediaObjectList.add(new VastMediaObject(
				"http://http.atlas.cdn.yimg.com/yamplus/video_q56ZjD12WJemSb7m7YY7yTvDk-VR4Gse2n-gLwvBUUa4bFAc08PpIsTcFOvpfUH_DcpgrasaGHU-_13.mp4?a=yamplus&mr=0&c=98453",
				640, 360, 1000));
		return mediaObjectList;
	}
	
	@Test
	public void testValidateVastAndFetchAssets() throws Exception {
		System.out.println("\ntestValidateVastAndFetchAssets()");
		URL mockVastUrl = PowerMock.createMock(URL.class);

		String SECOND_WRAPPER_XML = "src/test/resources/secondWrapper.xml";
		String SECOND_WRAPPER_URL_STRING = "http://useast-aws2-user.bidswitch.net/vast/F4k8eGWiBp5X_k2-uL0mRPj8Hd1qP952k2LkhWK3lcB7_qCnwZRHM7i7zDUfs9rhGBdnEwCyz-IgHXtRwFbXcniYoouR5QllrGjcm1RuJN961b-BjbhKBy7ZB08LvbzDo2qxH1aZ7J72fAsHyCs9hHrjZ9dZeG9nH-0KbDJJUz0l1iTbz7kCAC-2e1nnS-PEvmmsvvpQf1T9cFzMydr3tBSxRDoMOZESxghM5PqTDZtyCKGVuB10K7udAPkhVkofG6lenTyHAKC9ofQ7AeMwWJzzrhMCBjJfvObg1WfLKjYHbb3lQ4f9kTfXajFcmGbMs56lN4z9xzqu5UVRVCP_YihgzLrhFtCbR4eRWakH_ofXRqT7X7kGmQ4L4RcpecUIW0toWyU5LXZLRnll1Y2BnnoWsV52XXNqUQGdAzLWfVzE1RUrEnumKcKFgB37WShoQihFKO3_h33mRacxEf052xdBpIDSfCfSMsz9uwRlnYYiNBWjPTOqAeF7H8xvurcYXo0a6YWXPPsGafsEOHlmTEXc7tGKbsw/http%3A%2F%2Ftag.clrstm.com%2Fvast%2FgjS-IaL0qHz2vt3p9pddeQH4_Sknto5N6dzeU0JvexiQODxJD2qqBLoFHGPw89oZHvE-2_rFCWCvteujN2hs-EmS1AXN08OAFI-H6a1-4gSmAE0yczSZoTg54mA2vrrOf4hOB5rIMhwdyw0xgD9pw2A4pUjvy-TWjy0bTqkoRnd8w8d21lJdKzL9pb6C0Q1wkr5iJTV0X6Lrrj6vK0uKThLIe-Nsv2LsJZQ32nYyZmCBjFDTNiwgkHHknXm8_HLhiIpCgVXSoMDHvUkwXQYYoXWNaH4NDi_OyQRuMLWgWKvYwmaCvisttqrpf4V6qLfLFLE56iUjUDop2I0AAKCivpGGeFXnadjG9punW4WDqyUC1dcWxS0Tr-PEAwklBE_iphZQ3g/http%3A%2F%2Ftag.clrstm.com%2Fimp%2Fadi%2FgjS-IfSMO_eNEzZ82IaFMIJUhBJJ4qBcKWK_YuiEbK_7um0GbN3sE2KAIr5kPFxUcOUA1Vfvgo4dHNdtSqFTSQUMX0FLqYTQRUBM7GmkqaSBcdpL6Hjeu3ptLjjRjJbRFXvY5lXK_Za66PerDAHHs9Oj521lN6IRNPKnsiu7nS7Jyd-KBH5AaXcPo3GGIZ0jnKIhbKL94YsaPoqYJhu7dqvzpOEQXmcDRhTr8CbRYwP1uO3w6bEEkkrY36vG9VhJ7BgU8FLJFLOFjExzDvCSgWKEeFtLALiW4jdojhCK2x5KrQlH3OQf5dnmT1q-jQNwzd6qlpyuJy8isdNpzOWoFRZezNFYt3ynzsESQtNWKSr8qLJF3LBJ9w00fVEqZ0NQtgnj1A%2F%24%7BAUCTION_PRICE%7D%2F%24%7BCLICK_URL%3AURLENCODE%7D/95F3D6A156408244";
		String MOCKED_SECOND_WRAPPER_URL_STRING = "/vast/F4k8eGWiBp5X_k2-uL0mRPj8Hd1qP952k2LkhWK3lcB7_qCnwZRHM7i7zDUfs9rhGBdnEwCyz-IgHXtRwFbXcniYoouR5QllrGjcm1RuJN961b-BjbhKBy7ZB08LvbzDo2qxH1aZ7J72fAsHyCs9hHrjZ9dZeG9nH-0KbDJJUz0l1iTbz7kCAC-2e1nnS-PEvmmsvvpQf1T9cFzMydr3tBSxRDoMOZESxghM5PqTDZtyCKGVuB10K7udAPkhVkofG6lenTyHAKC9ofQ7AeMwWJzzrhMCBjJfvObg1WfLKjYHbb3lQ4f9kTfXajFcmGbMs56lN4z9xzqu5UVRVCP_YihgzLrhFtCbR4eRWakH_ofXRqT7X7kGmQ4L4RcpecUIW0toWyU5LXZLRnll1Y2BnnoWsV52XXNqUQGdAzLWfVzE1RUrEnumKcKFgB37WShoQihFKO3_h33mRacxEf052xdBpIDSfCfSMsz9uwRlnYYiNBWjPTOqAeF7H8xvurcYXo0a6YWXPPsGafsEOHlmTEXc7tGKbsw/http%3A%2F%2Ftag.clrstm.com%2Fvast%2FgjS-IaL0qHz2vt3p9pddeQH4_Sknto5N6dzeU0JvexiQODxJD2qqBLoFHGPw89oZHvE-2_rFCWCvteujN2hs-EmS1AXN08OAFI-H6a1-4gSmAE0yczSZoTg54mA2vrrOf4hOB5rIMhwdyw0xgD9pw2A4pUjvy-TWjy0bTqkoRnd8w8d21lJdKzL9pb6C0Q1wkr5iJTV0X6Lrrj6vK0uKThLIe-Nsv2LsJZQ32nYyZmCBjFDTNiwgkHHknXm8_HLhiIpCgVXSoMDHvUkwXQYYoXWNaH4NDi_OyQRuMLWgWKvYwmaCvisttqrpf4V6qLfLFLE56iUjUDop2I0AAKCivpGGeFXnadjG9punW4WDqyUC1dcWxS0Tr-PEAwklBE_iphZQ3g/http%3A%2F%2Ftag.clrstm.com%2Fimp%2Fadi%2FgjS-IfSMO_eNEzZ82IaFMIJUhBJJ4qBcKWK_YuiEbK_7um0GbN3sE2KAIr5kPFxUcOUA1Vfvgo4dHNdtSqFTSQUMX0FLqYTQRUBM7GmkqaSBcdpL6Hjeu3ptLjjRjJbRFXvY5lXK_Za66PerDAHHs9Oj521lN6IRNPKnsiu7nS7Jyd-KBH5AaXcPo3GGIZ0jnKIhbKL94YsaPoqYJhu7dqvzpOEQXmcDRhTr8CbRYwP1uO3w6bEEkkrY36vG9VhJ7BgU8FLJFLOFjExzDvCSgWKEeFtLALiW4jdojhCK2x5KrQlH3OQf5dnmT1q-jQNwzd6qlpyuJy8isdNpzOWoFRZezNFYt3ynzsESQtNWKSr8qLJF3LBJ9w00fVEqZ0NQtgnj1A%2F%24%7BAUCTION_PRICE%7D%2F%24%7BCLICK_URL%3AURLENCODE%7D/95F3D6A156408244";

		String FIRST_WRAPPER_XML = "src/test/resources/firstWrapper.xml";
		String FIRST_WRAPPER_URL_STRING = "http://tag.clrstm.com/vast/gjS-IaL0qHz2vt3p9pddeQH4_Sknto5N6dzeU0JvexiQODxJD2qqBLoFHGPw89oZHvE-2_rFCWCvteujN2hs-EmS1AXN08OAFI-H6a1-4gSmAE0yczSZoTg54mA2vrrOf4hOB5rIMhwdyw0xgD9pw2A4pUjvy-TWjy0bTqkoRnd8w8d21lJdKzL9pb6C0Q1wkr5iJTV0X6Lrrj6vK0uKThLIe-Nsv2LsJZQ32nYyZmCBjFDTNiwgkHHknXm8_HLhiIpCgVXSoMDHvUkwXQYYoXWNaH4NDi_OyQRuMLWgWKvYwmaCvisttqrpf4V6qLfLFLE56iUjUDop2I0AAKCivpGGeFXnadjG9punW4WDqyUC1dcWxS0Tr-PEAwklBE_iphZQ3g";
		String MOCKED_FIRST_WRAPPER_URL_STRING = "/vast/gjS-IaL0qHz2vt3p9pddeQH4_Sknto5N6dzeU0JvexiQODxJD2qqBLoFHGPw89oZHvE-2_rFCWCvteujN2hs-EmS1AXN08OAFI-H6a1-4gSmAE0yczSZoTg54mA2vrrOf4hOB5rIMhwdyw0xgD9pw2A4pUjvy-TWjy0bTqkoRnd8w8d21lJdKzL9pb6C0Q1wkr5iJTV0X6Lrrj6vK0uKThLIe-Nsv2LsJZQ32nYyZmCBjFDTNiwgkHHknXm8_HLhiIpCgVXSoMDHvUkwXQYYoXWNaH4NDi_OyQRuMLWgWKvYwmaCvisttqrpf4V6qLfLFLE56iUjUDop2I0AAKCivpGGeFXnadjG9punW4WDqyUC1dcWxS0Tr-PEAwklBE_iphZQ3g";

		String AD_XML = "src/test/resources/nonWrapper.xml";
		String AD_URL_STRING = "http://pr.ybp.yahoo.com/ab/secure/false/imp/VLKWKpF-yqYuIxXBgBOcVU-tfralEMxJvgIW8yC_y4fcbC58RypCSCtbray5i9H7NACSD2TwfaPlsWpQtEeNwvQ2PnIQJwFaW2drsKpoHI-YtuXbvSrhJC4LBVOCGBwwaDpTRFa3VwDCWBPOf3X-CGYmSq-SjAhuYHfB1NQx6-Qxhz_zVIWeE1IhoGdnq-fyhlhWpYjEvuPBS33r3YHdVWLNRMHkRAbprj-h5XxuFfm0JgI2RNaj9Hg_dASrxpgK0_grE1fZPfAGKjlTvGSLFbKCKaxLdY7jUeLDjmSvvpwi9-PdBI5DHAGqedOGjoxIyss8zthEVWZXqiQmnIih1eXVDumiPCwdJ85JzgLCGcBYFWScxbSuYLNNX7fmBwQklV8Md91B5x2_UBTBf056MZqSDwROqQQtxFUmZuJumhdhHfnaWl_vQGNirT2imlb7Mbsj_PyD4lTY7gNX8p8I_aQDEoOtx9vYE9OHXdhImjdxnWKUrjO2ciAucRWc43i-FkV0afSGyncS5WvH_l82uw1eoThaCUwJjSiHI6cAwnaeXmQG277ttrcrIur7FhqUSvgyhng1UEge0MWNuxJ3wpwUCKzsA87pYifghlF94Xku5JNDZIFXVbFIkp9TzPnlw-_Wj5RLqV2MnsQuBvJXIT-JzXxDHJklRky-l0upS05z5vbQ6iLz90hJtFV7LyzwJYy3qTAXtYeuX5ZuScqET2zpDpvR3zfS/wp/DA7AFFCA6F021CAB";
		String MOCKED_AD_URL_STRING = "/ab/secure/false/imp/VLKWKpF-yqYuIxXBgBOcVU-tfralEMxJvgIW8yC_y4fcbC58RypCSCtbray5i9H7NACSD2TwfaPlsWpQtEeNwvQ2PnIQJwFaW2drsKpoHI-YtuXbvSrhJC4LBVOCGBwwaDpTRFa3VwDCWBPOf3X-CGYmSq-SjAhuYHfB1NQx6-Qxhz_zVIWeE1IhoGdnq-fyhlhWpYjEvuPBS33r3YHdVWLNRMHkRAbprj-h5XxuFfm0JgI2RNaj9Hg_dASrxpgK0_grE1fZPfAGKjlTvGSLFbKCKaxLdY7jUeLDjmSvvpwi9-PdBI5DHAGqedOGjoxIyss8zthEVWZXqiQmnIih1eXVDumiPCwdJ85JzgLCGcBYFWScxbSuYLNNX7fmBwQklV8Md91B5x2_UBTBf056MZqSDwROqQQtxFUmZuJumhdhHfnaWl_vQGNirT2imlb7Mbsj_PyD4lTY7gNX8p8I_aQDEoOtx9vYE9OHXdhImjdxnWKUrjO2ciAucRWc43i-FkV0afSGyncS5WvH_l82uw1eoThaCUwJjSiHI6cAwnaeXmQG277ttrcrIur7FhqUSvgyhng1UEge0MWNuxJ3wpwUCKzsA87pYifghlF94Xku5JNDZIFXVbFIkp9TzPnlw-_Wj5RLqV2MnsQuBvJXIT-JzXxDHJklRky-l0upS05z5vbQ6iLz90hJtFV7LyzwJYy3qTAXtYeuX5ZuScqET2zpDpvR3zfS/wp/DA7AFFCA6F021CAB";
		
		String DOWN_URL_STRING = "http://google.com/urlIsDown";
		String MOCKED_DOWN_URL_STRING = "/urlIsDown";
		
		String INVALID_XML = "src/test/resources/invalid.xml";
		String INVALID_XML_STRING = "http://google.com/invalidXml";
		String MOCKED_INVALID_XML_STRING = "/invalidXml";
		
		String FTP_URL_STRING = "ftp://random.com";

		server.request(by(uri(MOCKED_SECOND_WRAPPER_URL_STRING))).response(file(SECOND_WRAPPER_XML));
		server.request(by(uri(MOCKED_FIRST_WRAPPER_URL_STRING))).response(file(FIRST_WRAPPER_XML));
		server.request(by(uri(MOCKED_AD_URL_STRING))).response(header("Access-Control-Allow-Origin", "*"),
				attachment("awesome.xml", file(AD_XML)));
		server.request(by(uri(MOCKED_DOWN_URL_STRING))).response(status(400));
		server.request(by(uri(MOCKED_INVALID_XML_STRING))).response(file(INVALID_XML));
		runner = runner(server);
		runner.start();
		String serverUrl = "http://localhost:" + server.port();
		String serverFtpUrl = "ftp://localhost:" + server.port();
		URL MOCKED_SECOND_WRAPPER_URL = new URL(serverUrl + MOCKED_SECOND_WRAPPER_URL_STRING);
		URL MOCKED_FIRST_WRAPPER_URL = new URL(serverUrl + MOCKED_FIRST_WRAPPER_URL_STRING);
		URL MOCKED_AD_URL = new URL(serverUrl + MOCKED_AD_URL_STRING);
		URL MOCKED_DOWN_URL = new URL(serverUrl + MOCKED_DOWN_URL_STRING);
		URL MOCKED_INVALID_XML = new URL(serverUrl + MOCKED_INVALID_XML_STRING);
		URL MOCKED_FTP_URL = new URL(serverFtpUrl + MOCKED_AD_URL_STRING);
		PowerMock.expectNew(URL.class, SECOND_WRAPPER_URL_STRING).andReturn(MOCKED_SECOND_WRAPPER_URL);
		PowerMock.expectNew(URL.class, FIRST_WRAPPER_URL_STRING).andReturn(MOCKED_FIRST_WRAPPER_URL).anyTimes();
		PowerMock.expectNew(URL.class, AD_URL_STRING).andReturn(MOCKED_AD_URL).anyTimes();
		PowerMock.expectNew(URL.class, DOWN_URL_STRING).andReturn(MOCKED_DOWN_URL);
		PowerMock.expectNew(URL.class, INVALID_XML_STRING).andReturn(MOCKED_INVALID_XML);
		PowerMock.expectNew(URL.class, FTP_URL_STRING).andReturn(MOCKED_FTP_URL);
		PowerMock.replayAll();
		List<VastMediaObject> expectedMediaObjectList = buildMediaObjectList();
		List<VastMediaObject> mediaObjectList;

		// No. of Wrapper XML = WRAPPER_XML_MAX_LIMIT
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets(FIRST_WRAPPER_URL_STRING);
		Assert.assertEquals(VastReturnCodeEnum.WARNING, vastReturnObject.getVastReturnCode());
		mediaObjectList = (List<VastMediaObject>) vastReturnObject.getValObject();
		Assert.assertArrayEquals(expectedMediaObjectList.toArray(), mediaObjectList.toArray());

		// No. of Wrapper XML > WRAPPER_XML_MAX_LIMIT
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets(SECOND_WRAPPER_URL_STRING);
		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
		Assert.assertEquals(ErrorEnum.WRAPPER_MAX_LIMIT_EXCEEDED,
				vastReturnObject.getVastErrorObjectList().get(0).getError());

		// No. of Wrapper XML < WRAPPER_XML_MAX_LIMIT
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets(AD_URL_STRING);
		Assert.assertEquals(VastReturnCodeEnum.OK, vastReturnObject.getVastReturnCode());
		mediaObjectList = (List<VastMediaObject>) vastReturnObject.getValObject();
		Assert.assertArrayEquals(expectedMediaObjectList.toArray(), mediaObjectList.toArray());

		// Invalid protocol
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets("ftp://random.com");
		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
		Assert.assertEquals(ErrorEnum.URL_INVALID_PROTOCOL,
				vastReturnObject.getVastErrorObjectList().get(0).getError());

		// Cannot connect to URL
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets(DOWN_URL_STRING);
		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
		Assert.assertEquals(ErrorEnum.URL_CANT_CONNECT, vastReturnObject.getVastErrorObjectList().get(0).getError());

		// Invalid XML
		vastReturnObject = testVastXmlUtils.validateVastAndFetchAssets(INVALID_XML_STRING);
		Assert.assertEquals(VastReturnCodeEnum.ERROR, vastReturnObject.getVastReturnCode());
		Assert.assertEquals(ErrorEnum.XML_PARSING_EXCEPTION,
				vastReturnObject.getVastErrorObjectList().get(0).getError());
	}

}

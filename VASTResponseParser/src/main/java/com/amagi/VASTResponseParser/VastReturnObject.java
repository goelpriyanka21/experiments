package com.amagi.VASTResponseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by priyanka. Return object returned by all functions in
 * com.amagi.validateXML.VastXmlUtils are instances of
 * com.amagi.validateXML.VastReturnObject
 */

public class VastReturnObject {
//	/** The value returned from function */
//	private Object valObject;
	/** The return code while fetching valObject from function */
	private VastReturnCodeEnum vastReturnCode;
	/** All the errors and warnings while fetching valObject from function */
	private List<VastErrorObject> vastErrorObjectList;

	public VastReturnObject() {
//		valObject = null;
		vastErrorObjectList = null;
	}

//	public void setValObject(Object valObj) {
//		valObject = valObj;
//	}

	/**
	 *
	 * Function to set valObject and vastReturnCode
	 *
	 * @param valObj
	 *            The valObject to be set
	 * @param returnCode
	 *            The ReturnCode to be set
	 */
//	public void setValObjectAndCode(Object valObj, VastReturnCodeEnum returnCode) {
//		valObject = valObj;
//		vastReturnCode = returnCode;
//	}
	
	public void setCode(VastReturnCodeEnum returnCode) {
		vastReturnCode = returnCode;
	}

//	public Object getValObject() {
//		return valObject;
//	}

	public void setVastErrorObject(List<VastErrorObject> errorObjectList) {
		vastErrorObjectList = errorObjectList;
	}

	public List<VastErrorObject> getVastErrorObjectList() {
		return vastErrorObjectList;
	}

	public void setVastReturnCode(VastReturnCodeEnum returnCode) {
		vastReturnCode = returnCode;
	}

	public VastReturnCodeEnum getVastReturnCode() {
		return vastReturnCode;
	}

	/**
	 *
	 * Function to append ErrorObject to vastErrorObjectList and set
	 * vastReturnCode. If vastErrorObjectList is not initialized, it creates an
	 * empty list and appends ErrorObject to it
	 *
	 * @param vastErrorObject
	 *            The errorObject to be appended
	 * @param returnCode
	 *            The return code to be set
	 */
	public void appendVastErrorObjectListAndCode(VastReturnCodeEnum returnCode, VastErrorObject vastErrorObject) {
		if (vastErrorObjectList==null) {
			vastErrorObjectList = new ArrayList<VastErrorObject>();
		}
		vastErrorObjectList.add(vastErrorObject);
		vastReturnCode = returnCode;
	}

	/**
	 *
	 * Function to add warnings to vastErrorObjectList If vastErrorObjectList is
	 * not initialized, it creates an empty list and appends warnings to it
	 *
	 * @param warningsList
	 *            List of Warnings to be added to vastErrorObjectList
	 */
	public void addWarningsToErrorList(List<VastErrorObject> warningsList) {
		if (null == vastErrorObjectList) {
			setVastErrorObject(new ArrayList<VastErrorObject>());
		}
		vastErrorObjectList.addAll(warningsList);
	}
}

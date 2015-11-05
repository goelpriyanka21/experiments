package com.amagi.VASTResponseParser;

import org.apache.commons.lang.StringUtils;

/**
 * Created by priyanka. Each instance of com.amagi.validateXML.VastMediaObject
 * corresponds to MediaFile object in VAST XML
 */
public class VastMediaObject {
	/** URL of the media file in VAST XML */
	private String vastMediaUrl;
	/** Bitrate of the media file in VAST XML */
	private int vastBitrate;
	/** Width of the media file in VAST XML */
	private int vastWidth;
	/** Height of the media file in VAST XML */
	private int vastHeight;

	@Override
	public boolean equals(Object o) {
		// If the object is compared with itself then return true
		if (o == this) {
			return true;
		}

		/*
		 * Check if o is an instance of com.amagi.validateXML.VastMediaObject or
		 * not "null instanceof [type]" also returns false
		 */
		if (!(o instanceof VastMediaObject)) {
			return false;
		}

		// typecast o to com.amagi.validateXML.VastMediaObject so that we can
		// compare data members
		VastMediaObject c = (VastMediaObject) o;

		// Compare the data members and return accordingly
		return StringUtils.equals(vastMediaUrl, c.getVastMediaUrl()) && vastWidth == c.getVastWidth()
				&& vastHeight == c.getVastHeight() && vastBitrate == c.getVastBitrate();
	}

	/**
	 * Constructor to instantiate a media object<br>
	 * Use this constructor when bitrate is present as an attribute in MediaFile
	 * Object
	 *
	 * @param mediaUrl
	 *            URL of the media file in VAST XML
	 * @param width
	 *            Width of the media file in VAST XML
	 * @param height
	 *            Height of the media file in VAST XML
	 * @param bitrate
	 *            Bitrate of the media file in VAST XML
	 */
	public VastMediaObject(String mediaUrl, int width, int height, int bitrate) {
		vastMediaUrl = mediaUrl;
		vastWidth = width;
		vastHeight = height;
		vastBitrate = bitrate;
	}

	/**
	 * Constructor to instantiate a media object<br>
	 * Use this constructor when bitrate is not present as an attribute in
	 * MediaFile Object
	 *
	 * @param mediaUrl
	 *            URL of the media file in VAST XML
	 * @param width
	 *            Width of the media file in VAST XML
	 * @param height
	 *            Height of the media file in VAST XML
	 */
	public VastMediaObject(String mediaUrl, int width, int height) {
		vastMediaUrl = mediaUrl;
		vastWidth = width;
		vastHeight = height;
		vastBitrate = 0;
	}

	public String getVastMediaUrl() {
		return vastMediaUrl;
	}

	public void setVastBitrate(int bitrate) {
		vastBitrate = bitrate;
	}

	public int getVastBitrate() {
		return vastBitrate;
	}

	public void setVastWidth(int width) {
		vastWidth = width;
	}

	public int getVastWidth() {
		return vastWidth;
	}

	public void setVastHeight(int height) {
		vastHeight = height;
	}

	public int getVastHeight() {
		return vastHeight;
	}
}

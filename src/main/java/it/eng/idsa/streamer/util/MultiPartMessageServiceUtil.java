package it.eng.idsa.streamer.util;

import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */


/**
 * MultiPartMessageServiceUtil.
 */
public class MultiPartMessageServiceUtil {
	
	public static String getHeader(String body) {
		MultipartMessage deserializedMultipartMessage = MultipartMessageProcessor.parseMultipartMessage(body);
		return deserializedMultipartMessage.getHeaderContentString();
	}

	public static String getPayload(String body) {
		MultipartMessage deserializedMultipartMessage = MultipartMessageProcessor.parseMultipartMessage(body);
		return deserializedMultipartMessage.getPayloadContent();
	}

}

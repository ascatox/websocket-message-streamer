package it.eng.idsa.streamer.util;


import de.fraunhofer.iais.eis.Message;
import nl.tno.ids.common.multipart.MultiPart;
import nl.tno.ids.common.multipart.MultiPartMessage;

/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */


/**
 * Service Implementation for managing MultiPartMessage.
 */
public class MultiPartMessageServiceUtil {
	
	public static String getHeader(String body) {
		MultiPartMessage deserializedMultipartMessage = MultiPart.parseString(body);
		return deserializedMultipartMessage.getHeaderString();
	}

	public static String getPayload(String body) {
		MultiPartMessage deserializedMultipartMessage = MultiPart.parseString(body);
		return deserializedMultipartMessage.getPayload();
	}

	public static Message getMessage(String body) {
		MultiPartMessage deserializedMultipartMessage = MultiPart.parseString(body);
		return deserializedMultipartMessage.getHeader();
	}


}

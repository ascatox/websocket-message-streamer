
package it.eng.idsa.streamer.service;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ResultMessageBuilder;
import it.eng.idsa.multipart.builder.MultipartMessageBuilder;
import it.eng.idsa.multipart.domain.MultipartMessage;
import it.eng.idsa.multipart.processor.MultipartMessageProcessor;
import it.eng.idsa.multipart.util.DateUtil;
import it.eng.idsa.streamer.util.RejectionMessageType;

import java.net.URI;

import static de.fraunhofer.iais.eis.util.Util.asList;


/**
 * 
 * @author Milan Karajovic and Gabriele De Luca
 *
 */


public class RejectionMessageService {

    private static final String informationModelVersion = "2.1.0-SNAPSHOT"; //TODO Pom.xml

	public static void sendRejectionMessage(RejectionMessageType rejectionMessageType, Message message) throws Exception {
		Message rejectionMessage = createRejectionMessage(rejectionMessageType.toString(), message);
		MultipartMessage builtMessage = new MultipartMessageBuilder()
    			.withHeaderContent(rejectionMessage)
    			.build();
		String stringMessage = MultipartMessageProcessor.multipartMessagetoString(builtMessage, false);
		throw new Exception(stringMessage);
	}
	
	private static Message createRejectionMessage(String rejectionMessageType, Message message) {
		Message rejectionMessage = null;
		switch(rejectionMessageType) {
			case "RESULT_MESSAGE":
				rejectionMessage = createResultMessage(message);
				break;
			case "REJECTION_MESSAGE_COMMON":
				rejectionMessage = createRejectionMessageCommon(message);
				break;
			case "REJECTION_TOKEN":
				rejectionMessage = createRejectionToken(message);
				break;
			case "REJECTION_MESSAGE_LOCAL_ISSUES":
				rejectionMessage = createRejectionMessageLocalIssues(message);
				break;
			case "REJECTION_TOKEN_LOCAL_ISSUES":
				rejectionMessage = createRejectionTokenLocalIssues(message);
				break;
			case "REJECTION_COMMUNICATION_LOCAL_ISSUES":
				rejectionMessage = createRejectionCommunicationLocalIssues(message);
				break;	
			default:
				rejectionMessage = createResultMessage(message);
				break;
		}
		return rejectionMessage;
	}

	private static Message createResultMessage(Message header) {
		return new ResultMessageBuilder()
				._issuerConnector_(whoIAm())
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				._recipientConnector_(asList(header.getIssuerConnector()))
				._correlationMessage_(header.getId())
				.build();
	}

	private static Message createRejectionMessageCommon(Message header) {
		return new RejectionMessageBuilder()
				._issuerConnector_(whoIAm())
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				._recipientConnector_(header!=null?asList(header.getIssuerConnector()):asList(URI.create("auto-generated")))
				._correlationMessage_(header!=null?header.getId():URI.create(""))
				._rejectionReason_(RejectionReason.MALFORMED_MESSAGE)
				.build();
	}
	
	private static Message createRejectionToken(Message header) {
		return new RejectionMessageBuilder()
				._issuerConnector_(whoIAm())
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				._recipientConnector_(asList(header.getIssuerConnector()))
				._correlationMessage_(header.getId())
				._rejectionReason_(RejectionReason.NOT_AUTHENTICATED)
				.build();
	}


	private static URI whoIAm() {
		//TODO
		return URI.create("auto-generated");
	}

	private static Message createRejectionMessageLocalIssues(Message header) {
		return new RejectionMessageBuilder()
				._issuerConnector_(URI.create("auto-generated"))
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				//._recipientConnectors_(header!=null?asList(header.getIssuerConnector()):asList(URI.create("auto-generated")))
				._correlationMessage_(URI.create("auto-generated"))
				._rejectionReason_(RejectionReason.MALFORMED_MESSAGE)
				.build();
	}
	
	private static Message createRejectionTokenLocalIssues(Message header) {
		return new RejectionMessageBuilder()
				._issuerConnector_(header.getIssuerConnector())
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				._recipientConnector_(asList(header.getIssuerConnector()))
				._correlationMessage_(header.getId())
				._rejectionReason_(RejectionReason.NOT_AUTHENTICATED)
				.build();
	}
	
	private static Message createRejectionCommunicationLocalIssues(Message header) {
		return new RejectionMessageBuilder()
				._issuerConnector_(header.getIssuerConnector())
				._issued_(DateUtil.now())
                ._modelVersion_(informationModelVersion)
				._recipientConnector_(asList(header.getIssuerConnector()))
				._correlationMessage_(header.getId())
				._rejectionReason_(RejectionReason.NOT_FOUND)
				.build();
	}
}


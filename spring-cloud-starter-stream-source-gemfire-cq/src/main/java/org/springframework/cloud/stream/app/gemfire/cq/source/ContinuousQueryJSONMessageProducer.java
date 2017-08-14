package org.springframework.cloud.stream.app.gemfire.cq.source;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.listener.ContinuousQueryListenerContainer;
import org.springframework.integration.gemfire.inbound.ContinuousQueryMessageProducer;
import org.springframework.integration.gemfire.inbound.CqEventType;
import org.springframework.messaging.Message;

import com.gemstone.gemfire.cache.query.CqEvent;
import com.gemstone.gemfire.pdx.JSONFormatter;
import com.gemstone.gemfire.pdx.PdxInstance;

public class ContinuousQueryJSONMessageProducer extends ContinuousQueryMessageProducer {

	private static Log logger = LogFactory.getLog(ContinuousQueryJSONMessageProducer.class);

	@Autowired
	private ClientCacheFactoryBean clientCache;

	@Autowired
	private PoolFactoryBean gemfirePool;

	private volatile Set<CqEventType> supportedEventTypes =
			new HashSet<CqEventType>(Arrays.asList(CqEventType.CREATED, CqEventType.UPDATED));

	public ContinuousQueryJSONMessageProducer(ContinuousQueryListenerContainer queryListenerContainer,
			String query) {
		super(queryListenerContainer, query);
	}

	@Override
	protected void onInit() {
		super.onInit();
		clientCache.isReadyForEvents();
	}

	@Override
	public void onEvent(CqEvent event) {
		if (isEventSupported(event)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("processing cq event key [%s] event [%s]", event.getQueryOperation()
						.toString(), event.getKey()));
			}
			Message<?> message = null;
			Object object = evaluatePayloadExpression(event);

			if (object instanceof Message) {
				message = (Message<?>) object;
			}
			else {
				object = createJSONObject(object);
				message = getMessageBuilderFactory().withPayload(object).build();
			}
			sendMessage(message);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object createJSONObject( Object payloadObject) {

		String jsonNewValue = null;
		LinkedHashMap payloadHashMap = null;
		boolean isValueConvertedToJSON = false;

		if ( payloadObject != null && payloadObject instanceof java.util.LinkedHashMap) {
			payloadHashMap = (LinkedHashMap) payloadObject;
			if(payloadHashMap.get("value") != null &&
					payloadHashMap.get("value") instanceof PdxInstance) {

				PdxInstance newVaule = (PdxInstance) payloadHashMap.get("value");
				jsonNewValue = JSONFormatter.toJSON(newVaule);
				payloadHashMap.put("value", jsonNewValue);
				isValueConvertedToJSON = true;
			}
		}

		if (payloadHashMap == null || isValueConvertedToJSON == false) {
			return payloadObject;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("json payload after processing cq event: [%s]",
					payloadHashMap.toString()));
		}

		return (Object)payloadHashMap;
	}

	private boolean isEventSupported(CqEvent event) {

		String eventName = event.getQueryOperation().toString() +
				(event.getQueryOperation().toString().endsWith("Y") ? "ED" : "D");
		CqEventType eventType = CqEventType.valueOf(eventName);
		return this.supportedEventTypes.contains(eventType);
	}

}

package twins.data;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import twins.logic.item.AdvancedItemsService;

import twins.logic.operation.OperationBoundary;

@Component
public class AsyncRateHandler {
	private ObjectMapper jackson;
	private AdvancedItemsService advancedItem;

	@Autowired
	public AsyncRateHandler(ObjectMapper jackson) {
		this.jackson = new ObjectMapper();
	}

//	@JmsListener(destination = "RateQueue")
//	@Transactional
//	public void handleMessagesFromMom(String json) {
//		try {
//			OperationBoundary opBoundary = this.jackson.readValue(json, OperationBoundary.class);
//
//			advancedItem.rateServiceProvider(opBoundary.getOperationId().getSpace(),
//					opBoundary.getInvokedBy().getUserId().getEmail(), opBoundary.getItem(),
//					opBoundary.getOperationAttributes());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
}

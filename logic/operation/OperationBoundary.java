package twins.logic.operation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Lob;

import twins.logic.Id;
import twins.logic.item.Item;
import twins.logic.user.User;

public class OperationBoundary {
	private Id operationId;
	private String type;
	private Item item;
	private Date createdTimestamp;
	private User invokedBy;
	private Map<String, Object> operationAttributes;

	public OperationBoundary() {
		this.createdTimestamp = new Date();
		this.operationAttributes = new HashMap<>();
	}

	public OperationBoundary(Id operationId, String type, Item item, User invokedBy) {
		this();
		this.operationId = operationId;
		this.type = type;
		this.item = item;
		this.invokedBy = invokedBy;
	}

	public Id getOperationId() {
		return operationId;
	}

	public void setOperationId(Id operationId) {
		this.operationId = operationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public User getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(User invokedBy) {
		this.invokedBy = invokedBy;
	}
	
	@Lob
	public Map<String, Object> getOperationAttributes() {
		return operationAttributes;
	}

	public void setOperationAttributes(Map<String, Object> operationAttributes) {
		this.operationAttributes = operationAttributes;
	}
}

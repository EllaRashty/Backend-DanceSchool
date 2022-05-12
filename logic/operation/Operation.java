package twins.logic.operation;

import twins.logic.Id;

public class Operation {
	private Id operationId;
	
	public Operation() {
	}

	public Operation(Id operationId) {
		this.operationId = operationId;
	}

	public Id getOperationId() {
		return operationId;
	}

	public void setOperationId(Id operationId) {
		this.operationId = operationId;
	}
	
}

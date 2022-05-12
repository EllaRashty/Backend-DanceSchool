package twins.data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*
									OPERATIONS
----------------------------------------------------------------------------------------------------------
	OPERATION_ID | TYPE | ITEM_ID | USER_INVOKED_OPERATAION | OPERATION_ATTRIBUTES | OPERATION_CREATION
	  <PK>
*/
@Entity
@Table(name = "OPERATIONS")
public class OperationEntity {

	private String operationId;
	private String type;
	private String itemId;
	private String invokedByUser;
	private String operationAttributes;
	private Date createdTimestamp;

	public OperationEntity() {

	}

	@Id
	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String spaceId) {
		this.operationId = spaceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getInvokedByUser() {
		return invokedByUser;
	}

	public void setInvokedByUser(String invokedByUser) {
		this.invokedByUser = invokedByUser;
	}

	@Column(columnDefinition = "TEXT")
	public String getOperationAttributes() {
		return operationAttributes;
	}

	public void setOperationAttributes(String operationAttributes) {
		this.operationAttributes = operationAttributes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPERATION_CREATION")

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

}

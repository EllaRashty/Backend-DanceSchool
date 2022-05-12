package twins.logic.operation;

import java.util.List;

import twins.data.OperationEntity;
import twins.logic.OperationsService;

public interface AdvancedOperationsService extends OperationsService{

	public List<OperationBoundary> getAllOperations(int size, int page, String adminSpace, String adminEmail);

	OperationBoundary handleAsyncOperations(OperationEntity opEntity);
}

package twins.restAPI.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import twins.logic.OperationsService;
import twins.logic.operation.OperationBoundary;

@RestController
public class OperationController {

	private OperationsService operationsService;

	@Autowired
	public OperationController(OperationsService operationsService) {
		super();
		this.operationsService = operationsService;
	}

	@RequestMapping(path = "/twins/operations",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeOperationOnItem(@RequestBody OperationBoundary input) {
		return this.operationsService.invokeOperation(input);
	}

	@RequestMapping(path = "/twins/operations/async",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public OperationBoundary asyncOperation(@RequestBody OperationBoundary input) {
		//input.setOperationId(new Id("mySpace", "" + UUID.randomUUID().toString()));
		//return input;
		return operationsService.invokeAsynchronousOperation(input);
	}
}

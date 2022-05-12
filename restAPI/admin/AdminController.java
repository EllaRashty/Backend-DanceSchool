package twins.restAPI.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twins.logic.item.AdvancedItemsService;
import twins.logic.operation.AdvancedOperationsService;
import twins.logic.operation.OperationBoundary;
import twins.logic.user.AdvancedUsersService;
import twins.logic.user.UserBoundary;

@RestController
public class AdminController {
	private AdvancedUsersService usersService;
	private AdvancedItemsService itemsSrevice;
	private AdvancedOperationsService operationService;

	@Autowired
	public AdminController(AdvancedUsersService usersService, AdvancedItemsService itemsSrevice,
			AdvancedOperationsService operationService) {
		super();
		this.usersService = usersService;
		this.itemsSrevice = itemsSrevice;
		this.operationService = operationService;
	}

	@RequestMapping(path = "/twins/admin/users/{userSpace}/{userEmail}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserBoundary> getManyUsers(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestParam(name = "size", required = false, defaultValue = "100") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.usersService.getAllUsers(size, page, space, email);
	}

	@RequestMapping(path = "/twins/admin/operations/{userSpace}/{userEmail}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OperationBoundary> getAllOperations(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestParam(name = "size", required = false, defaultValue = "5") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.operationService.getAllOperations(size, page, space, email);
	}

	@RequestMapping(path = "/twins/admin/users/{userSpace}/{userEmail}", 
			method = RequestMethod.DELETE)
	public void deleteAllUsersInSpace(
			@PathVariable("userSpace") String space, 
			@PathVariable("userEmail") String email) {
		this.usersService.deleteAllUsers(space, email);
	}

	@RequestMapping(path = "/twins/admin/items/{userSpace}/{userEmail}", method = RequestMethod.DELETE)
	public void deleteAllItemsInSpace(@PathVariable("userSpace") String space, @PathVariable("userEmail") String email) {
		this.itemsSrevice.deleteAllItems(space, email);
	}

	@RequestMapping(path = "/twins/admin/operations/{userSpace}/{userEmail}", 
			method = RequestMethod.DELETE)
	public void deleteAllOperationsInSpace(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		this.operationService.deleteAllOperations(space, email);
	}
}

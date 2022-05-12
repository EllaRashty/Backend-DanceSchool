package twins.logic.operation;
import java.util.Random;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import twins.data.OperationEntity;
import twins.data.UserRole;
import twins.data.item.ItemDao;
import twins.data.operation.OperationDao;
import twins.data.user.UserDao;
import twins.logic.Id;
import twins.logic.item.AdvancedItemsService;
import twins.logic.item.Item;
import twins.logic.item.ItemServiceImplementation;
import twins.logic.user.AdvancedUsersService;
import twins.logic.user.User;
import twins.logic.user.UserId;
import twins.logic.user.UsersServiceImplementation;

@Service
public class OperationServiceImplementation implements AdvancedOperationsService {

	private OperationDao operationDao;
	private String space;
	private ObjectMapper jackson;
	private AdvancedItemsService itemsService;
	private AdvancedUsersService usersService;
	private JmsTemplate jmsTemplate;

	final private int DEFAULT_SIZE = 5;
	final private int DEFAULT_PAGE = 0;
	final private String SEARCH = "search";
	//final private String RATE_SERVICE_PROVIDER = "rateServiceProvider";
	final private String CREATE_NEW_SERVICE_PROVIDER = "createNewServiceProvider";
	final private String UPDATE_SERVICE_PROVIDER = "updateServiceProvider";

	public OperationServiceImplementation() {
	}

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Autowired
	public OperationServiceImplementation(OperationDao operationDao, ItemDao itemDao, UserDao userDao) {
		super();
		this.operationDao = operationDao;
		this.jackson = new ObjectMapper();
		this.itemsService = new ItemServiceImplementation(itemDao, userDao);
		this.usersService = new UsersServiceImplementation(userDao);
	}

	private boolean checkInvokedBy(String invokedBy) {
		return !invokedBy.isEmpty() || invokedBy != null;
	}

	private boolean checkOpId(String opId) {
		return !opId.isEmpty() || opId != null;
	}

	private boolean checkOpType(String opType) {
		return !opType.isEmpty() || opType != null;
	}

	private OperationEntity convertFromBoundary(OperationBoundary operation) {
		OperationEntity entity = new OperationEntity();
		String opId = operation.getOperationId().getSpace() + " " + operation.getOperationId().getId();
		String opType = operation.getType();

		String item = operation.getItem() != null
				? operation.getItem().getItemId().getSpace() + " " + operation.getItem().getItemId().getId()
				: null; // ask
		String invokedBy = operation.getInvokedBy().getUserId().getSpace() + " "
				+ operation.getInvokedBy().getUserId().getEmail();
		String attributes = this.marshal(operation.getOperationAttributes());
		entity.setOperationAttributes(attributes);
		if (checkOpId(opId) && checkOpType(opType) && checkInvokedBy(invokedBy)) {
			entity.setOperationId(opId);
			entity.setType(opType);
			entity.setItemId(item);
			entity.setInvokedByUser(invokedBy);
			entity.setOperationAttributes(attributes);
			entity.setCreatedTimestamp(operation.getCreatedTimestamp());
		} else {
			throw new RuntimeException();
		}
		return entity;
	}

	private OperationBoundary convertToBoundary(OperationEntity opEntity) {
		OperationBoundary newOpBoundary = new OperationBoundary();
		String[] opSpaceId = opEntity.getOperationId().split(" ");
		newOpBoundary.setOperationId(new Id(opSpaceId[0], opSpaceId[1]));
		newOpBoundary.setType(opEntity.getType());
		String[] itemSpaceId = opEntity.getItemId().split(" ");
		newOpBoundary.setItem(new Item(new Id(itemSpaceId[0], itemSpaceId[1])));
		newOpBoundary.setCreatedTimestamp(opEntity.getCreatedTimestamp());
		String[] userSpaceId = opEntity.getInvokedByUser().split(" ");
		newOpBoundary.setInvokedBy(new User(new UserId(userSpaceId[0], userSpaceId[1])));
		newOpBoundary
				.setOperationAttributes((Map<String, Object>) this.unmarshal(opEntity.getOperationAttributes(), Map.class));
		return newOpBoundary;
	}

	@Override
	@Transactional
	public void deleteAllOperations(String adminSpace, String adminEmail) {
		if (!usersService.hasPermission(adminSpace, adminEmail, UserRole.ADMIN)) {
			throw new RuntimeException("Only " + UserRole.ADMIN.name() + " can deleteAllOperations!");
		}

		this.operationDao.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperationBoundary> getAllOperations(int size, int page, String adminSpace, String adminEmail) {
		if (!usersService.hasPermission(adminSpace, adminEmail, UserRole.ADMIN)) {
			throw new RuntimeException("Only " + UserRole.ADMIN.name() + " can getAllOperations!");
		}
		Page<OperationEntity> pageOfEntities = this.operationDao
				.findAll(PageRequest.of(page, size, Direction.ASC, "type", "operationId"));

		List<OperationEntity> entities = pageOfEntities.getContent();
		List<OperationBoundary> rv = new ArrayList<>();
		for (OperationEntity entity : entities) {
			OperationBoundary boundary = convertToBoundary(entity);
			rv.add(boundary);
		}
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperationBoundary> getAllOperations(String adminSpace, String adminEmail) {
		// Iterable<OperationEntity> entities = this.operationDao.findAll();
		// List<OperationBoundary> userBoundaries = new ArrayList<>();
		// for (OperationEntity entity : entities) {
		// userBoundaries.add(convertToBoundary(entity));
		// }
		// return userBoundaries;
		throw new RuntimeException("deprecated operetion - use the new API getAllUsers(size, page)");
	}

	@Override
	public OperationBoundary invokeAsynchronousOperation(OperationBoundary operation) {
		User user = operation.getInvokedBy();
		if (user == null || user.getUserId() == null || user.getUserId().getSpace() == null
				|| user.getUserId().getEmail() == null) {
			throw new RuntimeException("User is null");
		}
		String space = user.getUserId().getSpace();
		String email = user.getUserId().getEmail();
		if (!this.usersService.hasPermission(space, email, UserRole.PLAYER)) {
			throw new RuntimeException("Only Player can invokeOperation!");
		}
		System.out.println("Tal");
		String newId = UUID.randomUUID().toString();
		operation.setOperationId(new Id(this.space, newId));
		OperationEntity opEntity = convertFromBoundary(operation);
		opEntity.setCreatedTimestamp(new Date());

		Integer size = (Integer) operation.getOperationAttributes().remove("size");
		Integer page = (Integer) operation.getOperationAttributes().remove("page");

		if (size == null) {
			size = DEFAULT_SIZE;
		}
		if (page == null) {
			page = DEFAULT_PAGE;
		}

		this.operationDao.save(opEntity);
		return handleAsyncOperations(opEntity);
	}

	@Override
	@Transactional
	public OperationBoundary handleAsyncOperations(OperationEntity opEntity) {
//		if (opEntity.getType().equals(RATE_SERVICE_PROVIDER)) {
//			try {
//				String opJson = this.jackson.writeValueAsString(opEntity);
//				// send json to MOM
//				this.jmsTemplate.send("RateQueue", session -> session.createTextMessage(opJson));
//				return convertToBoundary(opEntity);
//
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//
//		}
		return null;
	}

	@Override
	@Transactional
	public Object invokeOperation(OperationBoundary operation) {
		User user = operation.getInvokedBy();
		if (user == null || user.getUserId() == null || user.getUserId().getSpace() == null
				|| user.getUserId().getEmail() == null) {
			throw new RuntimeException("User is null");
		}

		String space = user.getUserId().getSpace();
		String email = user.getUserId().getEmail();
		if (!this.usersService.hasPermission(space, email, UserRole.PLAYER)) {
			throw new RuntimeException("Only Player can invokeOperation!");
		}

		String newId = UUID.randomUUID().toString();
		operation.setOperationId(new Id(this.space, newId));
		OperationEntity opEntity = convertFromBoundary(operation);
		opEntity.setCreatedTimestamp(new Date());

		Integer size = (Integer) operation.getOperationAttributes().remove("size");
		Integer page = (Integer) operation.getOperationAttributes().remove("page");

		if (size == null) {
			size = DEFAULT_SIZE;
		}
		if (page == null) {
			page = DEFAULT_PAGE;
		}

		Object rv = null;
		Map<String, Object> operationAttributes = operation.getOperationAttributes();

		switch (opEntity.getType()) {
			case SEARCH:
				String type = (String) operationAttributes.remove("type");
				type = type == null ? "" : type;
				rv = this.itemsService.getAllItemsByAttributes(size, page, space, email, type, operationAttributes);
				break;
//			case RATE_SERVICE_PROVIDER:
//				rv = this.itemsService.rateServiceProvider(space, email, operation.getItem(), operationAttributes);
//				break;
//			case CREATE_NEW_SERVICE_PROVIDER:
//				rv = this.itemsService.createNewServiceProvider(space, email, operationAttributes);
//				break;
			case UPDATE_SERVICE_PROVIDER:
				rv = this.itemsService.updateServiceProvider(space, email, operation.getItem(), operationAttributes);
				break;
			default:
				break;
		}

		this.operationDao.save(opEntity);
		return rv;
	}

	private String marshal(Object moreDetails) {
		try {
			return this.jackson.writeValueAsString(moreDetails);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// read this value from Spring configuration
	@Value("${spring.application.name:space}")
	public void setSpace(String space) {
		this.space = space;
	}

	private <T> T unmarshal(String json, Class<T> type) {
		try {
			return this.jackson.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	private int calculate(String teacherURL, String studentURL) {
		Random rn = new Random();
		int grade = rn.nextInt(100) + 1;
		return grade;
	}
	
	private int[] calculateArr(String teacherURL, String studentURL) {
		Random rn = new Random();
		int size = rn.nextInt(300) + 50;
		int arr[] = new int [size];
		for(int i =0; i< size; i++)
			arr[i] = rn.nextInt(100) + 1;
		return arr;		
	}
	///////////////////////////////////////////////////////////////////////////////////////////

}

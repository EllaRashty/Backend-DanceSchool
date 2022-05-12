package twins.logic.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import twins.data.ItemEntity;
import twins.data.UserRole;
import twins.data.item.ItemAttributes;
import twins.data.item.ItemDao;
import twins.data.user.UserDao;
import twins.logic.Id;
import twins.logic.user.AdvancedUsersService;
import twins.logic.user.User;
import twins.logic.user.UserBoundary;
import twins.logic.user.UserId;
import twins.logic.user.UsersServiceImplementation;

@Service
public class ItemServiceImplementation implements AdvancedItemsService {
	private ItemDao itemDao;
	private ObjectMapper jackson;
	private String space;
	private AdvancedUsersService usersService;

	public ItemServiceImplementation() {
	}

	@Autowired
	public ItemServiceImplementation(ItemDao itemDao, UserDao userDao) {
		super();
		this.itemDao = itemDao;
		this.jackson = new ObjectMapper();
		this.usersService = new UsersServiceImplementation(userDao);
	}

	@Value("${spring.application.name:DanceSchool}")
	public void setSpace(String space) {
		this.space = space;
	}

	@Override
	@Transactional
	public ItemBoundary createItem(String userSpace, String userEmail, ItemBoundary item) {
		if (!this.usersService.hasPermission(userSpace, userEmail, UserRole.MANAGER)) {
			throw new RuntimeException("Only " + UserRole.MANAGER.name() + " can createItem!");
			
		}
		System.out.println("kjnhk");
		
		item.setCreatedBy(new User(new UserId(userSpace, userEmail)));
		item.setItemId(new Id(this.space, UUID.randomUUID().toString()));

		ItemEntity entity = convertFromBoundary(item);
		
		if (userSpace != null && checkEmail(userEmail)) {
			entity.setSpaceEmail(userSpace + " " + userEmail);
		} else {
			throw new RuntimeException();
		}

		entity.setCreatedTimestamp(new Date());
		entity = this.itemDao.save(entity); // store entity to database using INSERT query

		// on success: Tx COMMIT
		// on exception: Tx ROLLBACK

		return this.convertToBoundary(entity);
	}

	private ItemBoundary convertToBoundary(ItemEntity entity) {
		ItemBoundary boundary = new ItemBoundary();
		String[] spaceId = entity.getSpaceId().split(" ");
		boundary.setItemId(new Id(spaceId[0], spaceId[1]));
		boundary.setType(entity.getType());
		boundary.setName(entity.getName());
		boundary.setActive(entity.getActive());
		boundary.setCreatedTimestamp(entity.getCreatedTimestamp());
		String[] spaceEmail = entity.getSpaceEmail().split(" ");
		boundary.setCreatedBy(new User(new UserId(spaceEmail[0], spaceEmail[1])));
		boundary.setImg(entity.getImg());
		boundary.setItemAttributes((Map<String, Object>) this.unmarshal(entity.getItemAttributes(), Map.class));
		
		Integer numberOfLessons = entity.getNumberOfLessons();
		
		if (numberOfLessons == null)
			numberOfLessons = 0;
		Lesson[] lessons = new Lesson [numberOfLessons];
		for(int i = 0; i < numberOfLessons; i++) {
			String[] nameLengthUrl = entity.getLessons()[i].split(" ");
//			lessons[i].setName(nameLengthUrl[0]);
//			lessons[i].setLength(nameLengthUrl[1]);
//			lessons[i].setUrl(nameLengthUrl[2]);
			lessons[i] = new Lesson (nameLengthUrl[0], nameLengthUrl[1], nameLengthUrl[2]);
		}
		boundary.setLessons(lessons);
		boundary.setNumberOfLessons(numberOfLessons);
		return boundary;
	}

	private ItemEntity convertFromBoundary(ItemBoundary item) {
		ItemEntity entity = new ItemEntity();

		String spaceId = item.getItemId().getSpace() + " " + item.getItemId().getId();
		String type = item.getType();
		String name = item.getName();
		String email = item.getCreatedBy().getUserId().getEmail();
		String img = item.getImg();
		if (checkEmail(email)) {
			String spaceEmail = item.getCreatedBy().getUserId().getSpace() + " " + email;
			if (spaceId != null && spaceEmail != null && type != null && name != null) {
				entity.setSpaceId(spaceId);
				entity.setSpaceEmail(spaceEmail);
				entity.setType(type);
				entity.setName(name);
				entity.setImg(img);
			}
		} else {
			throw new RuntimeException();
		}

		//Lesson array 
		Integer numberOfLessons = item.getNumberOfLessons();
		String[] lessons = new String[numberOfLessons];
		Lesson[] l = item.getLessons();
		
		for(int i = 0; i < numberOfLessons; i++) {
			lessons[i] = l[i].getName() + " " + l[i].getLength() + " " + l[i].getUrl();
		}
		entity.setLessons(lessons);
		entity.setNumberOfLessons(numberOfLessons);		
		
		entity.setActive(item.getActive());
		entity.setCreatedTimestamp(item.getCreatedTimestamp());
		String json = this.marshal(item.getItemAttributes());
		entity.setItemAttributes(json);
		return entity;
	}

	private boolean checkEmail(String email) {
		return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
	}

	private String marshal(Object itemAttributes) {
		try {
			return this.jackson.writeValueAsString(itemAttributes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// use Jackson to convert JSON to Object
	private <T> T unmarshal(String json, Class<T> type) {
		try {
			return this.jackson.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional
	public ItemBoundary updateItem(String space, String userEmail, String itemId, ItemBoundary update) {
		if (!this.usersService.hasPermission(space, userEmail, UserRole.MANAGER)) {
			throw new RuntimeException("Only " + UserRole.MANAGER.name() + " can updateItem!");
		}
		update.setCreatedBy(new User(new UserId(space, userEmail)));
		update.setItemId(new Id(space, itemId));
		Optional<ItemEntity> op = this.itemDao.findById(space + " " + itemId);
		if (op.isPresent()) {
			ItemEntity existing = op.get();
			ItemEntity updatedEntity = this.convertFromBoundary(update);

			updatedEntity.setSpaceId(existing.getSpaceId());
			updatedEntity.setSpaceEmail(space + " " + userEmail);
			updatedEntity.setCreatedTimestamp(existing.getCreatedTimestamp());

			return convertToBoundary(this.itemDao.save(updatedEntity));
		} else {
			throw new ItemNotFoundException("Item with id: " + itemId + " could not be found!");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemBoundary> getAllItems(String userSpace, String userEmail) {
		// Transaction - BEGIN

		// Iterable<ItemEntity> allEntities = this.itemDao.findAll();
		// List<ItemBoundary> itemBounderies = new ArrayList<>();
		// for (ItemEntity entity : allEntities) {
		// itemBounderies.add(convertToBoundary(entity));
		// }
		// return itemBounderies;
		throw new RuntimeException("deprecated items - use the new API getAllItems(size, page)");
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemBoundary> getAllItems(int size, int page, String space, String email) {
		UserBoundary user = this.usersService.login(space, email);
		List<ItemEntity> entities = null;

		if ((user.getRole().equals(UserRole.MANAGER.name()))||(user.getRole().equals(UserRole.ADMIN.name()))){
			Page<ItemEntity> pageOfEntities = this.itemDao
					.findAll(PageRequest.of(page, size, Direction.ASC, "spaceId", "name"));
			entities = pageOfEntities.getContent();
		} else if (user.getRole().equals(UserRole.PLAYER.name())) {
			entities = this.itemDao.findAllByActive(true, PageRequest.of(page, size, Direction.ASC, "spaceId", "name"));
		} else {
			throw new RuntimeException(user.getRole() + " can't getAllItems!");
		}

		List<ItemBoundary> rv = new ArrayList<>();
		for (ItemEntity entity : entities) {
			ItemBoundary boundary = convertToBoundary(entity);
			rv.add(boundary);
		}
		return rv;
	}

	@Override
	@Transactional(readOnly = true)
	public ItemBoundary getSpecificItem(String userSpace, String userEmail, String itemSpace, String itemId) {
		UserBoundary user = this.usersService.login(userSpace, userEmail);

		Optional<ItemEntity> op = this.itemDao.findById(itemSpace + " " + itemId);
		if (!op.isPresent()) {
			throw new ItemNotFoundException("Item with id: " + itemId + " could not be found!");
		}

		ItemEntity entity = op.get();
		if (!entity.getActive() && !user.getRole().equals(UserRole.MANAGER.name())) {
			throw new RuntimeException(user.getRole() + " can't get a specific item that is deleted!");
		}
		return convertToBoundary(entity);
	}

	@Override
	public void deleteAllItems(String adminSpace, String adminEmail) {
		UserRole role = UserRole.ADMIN;
		if (!this.usersService.hasPermission(adminSpace, adminEmail, role)) {
			throw new RuntimeException("Only " + role.name() + " can delete all items!");
		}
		this.itemDao.deleteAll();
	}

	// pattern = %plumbers%Tel Aviv% will give all active plumbers
	private String getPattern(Map<String, Object> itemAttributes) {
		Collection<Object> attributes = itemAttributes.values();
		String pattern = "%";
		for (Object attributeObj : attributes) {
			pattern += attributeObj.toString() + "%";
		}		
		return pattern;
	}

	private void checkItem(Item item) {
		if (item == null || item.getItemId() == null || item.getItemId().getSpace() == null
				|| item.getItemId().getId() == null) {
			throw new RuntimeException("item is null");
		}
	}

//	@Override
//	@Transactional(readOnly = false)
//	public ItemBoundary rateServiceProvider(String space, String email, Item item, Map<String, Object> itemAttributes) {
//		UserBoundary user = this.usersService.login(space, email);
//		if (!user.getRole().equals(UserRole.PLAYER.name())) {
//			throw new RuntimeException(user.getRole() + " Only players can rate!");
//		}
//		if (itemAttributes == null) {
//			throw new RuntimeException("rateServiceProvider: itemAttributes was not given!");
//		}
//		checkItem(item); // Throw RuntimeException
//		Optional<ItemEntity> op = this.itemDao.findById(item.getItemId().getSpace() + " " + item.getItemId().getId());
//		if (!op.isPresent()) {
//			throw new ItemNotFoundException("Item with id: " + item.getItemId().getId() + " could not be found!");
//		}
//
//		ItemEntity entity = op.get();
//		if (!entity.getActive()) {
//			throw new ItemNotFoundException("Item with id: " + item.getItemId().getId() + " was deleted!");
//		}
//
//		ItemBoundary boundary = convertToBoundary(entity);
//		// User is permitted to give a rating without a review.
//		String review = (String) itemAttributes.get("review");
//		review = review == null ? "" : review;
//		String rating = (String) itemAttributes.get("rating");
//		if (rating == null) {
//			throw new RuntimeException("rateServiceProvider: rating was not given!");
//		}
//
//		List<String> newReviewsList = (List<String>) boundary.getItemAttributes().get(ItemAttributes.reviews.name());
//		newReviewsList.add(review);
//		boundary.getItemAttributes().put(ItemAttributes.reviews.name(), newReviewsList);
//		
//		List<Integer> newRatingList = (List<Integer>) boundary.getItemAttributes().get(ItemAttributes.ratings.name());
//		newRatingList.add(Integer.parseInt(rating));
//		boundary.getItemAttributes().put(ItemAttributes.ratings.name(), newRatingList);
//
//		ItemEntity updatedItemEntity = convertFromBoundary(boundary);
//		this.itemDao.save(updatedItemEntity);
//		return boundary;
//	}


	
//	@Override
//	@Transactional(readOnly = false)
//	public ItemBoundary createNewServiceProvider(String space, String email, Map<String, Object> newAttributes) {
//		if (newAttributes == null) {
//			throw new RuntimeException("newAttributes is null");
//		}
//		Id newId = new Id(space, UUID.randomUUID().toString());
//		String type = (String) newAttributes.get("type");
//		String name = (String) newAttributes.get("name");
//		if (type == null || name == null) {
//			throw new RuntimeException("type and/or name is null");
//		}
//		User user = new User(new UserId(space, email));
//		int numberOfLessons = (int) newAttributes.get("numberOfLessons");
//		ItemBoundary boundary = new ItemBoundary(newId, type, name, user, numberOfLessons);
//		System.out.println(newAttributes);
//		String typeOfDance = (String) newAttributes.get(ItemAttributes.typeOfDance.name());
//		typeOfDance = typeOfDance == null ? "" : typeOfDance;
//		String workingHours = (String) newAttributes.get(ItemAttributes.workingHours.name());
//		workingHours = workingHours == null ? "" : workingHours;
//		String description = (String) newAttributes.get(ItemAttributes.description.name());
//		description = description == null ? "" : description;
//
//		Map<String, Object> itemAttributes = new HashMap<>();
//		itemAttributes.put(ItemAttributes.typeOfDance.name(), typeOfDance);
//		itemAttributes.put(ItemAttributes.workingHours.name(), workingHours);
//		itemAttributes.put(ItemAttributes.description.name(), description);
//		itemAttributes.put(ItemAttributes.reviews.name(), new ArrayList<String>());
//		boundary.setItemAttributes(itemAttributes);
//
//		ItemEntity entity = convertFromBoundary(boundary);
//		this.itemDao.save(entity);
//		return boundary;
//	}

	@Override
	public ItemBoundary updateServiceProvider(String space, String email, Item item,
			Map<String, Object> updatedAttributes) {
		checkItem(item);
		Optional<ItemEntity> op = this.itemDao.findById(item.getItemId().getSpace() + " " + item.getItemId().getId());
		if (!op.isPresent()) {
			throw new ItemNotFoundException("Item with id: " + item.getItemId().getId() + " could not be found!");
		}
		ItemEntity entity = op.get();
		if (!entity.getActive()) {
			throw new ItemNotFoundException("Item with id: " + item.getItemId().getId() + " was deleted!");
		}

		ItemBoundary boundary = convertToBoundary(entity);
		User createdBy = boundary.getCreatedBy();
		if (!createdBy.getUserId().getSpace().equals(space) || !createdBy.getUserId().getEmail().equals(email)) {
			throw new RuntimeException("User with email " + email + " didn't create this item!");
		}

		Map<String, Object> itemAttributes = boundary.getItemAttributes();
		if (updatedAttributes != null) {
			Set<String> keys = updatedAttributes.keySet();
			for (String key : keys) {
				if (key.equals("name")) {
					boundary.setName((String) updatedAttributes.get(key));
				} else if (key.equals("type")) {
					boundary.setType((String) updatedAttributes.get(key));
				} else {
					itemAttributes.put(key, updatedAttributes.get(key));
				}
			}
			boundary.setItemAttributes(itemAttributes);
		}
		this.itemDao.save(convertFromBoundary(boundary));
		return boundary;
	}

	@Override
	public List<ItemBoundary> getAllItemsByAttributes(int size, int page, String space, String email, String type,
			Map<String, Object> attributes) {
		UserBoundary user = this.usersService.login(space, email);
		List<ItemEntity> entities = null;
		String pattern = getPattern(attributes).toLowerCase();

		if (user.getRole().equals(UserRole.MANAGER.name())) {
			entities = this.itemDao.findAllByTypeAndNameLikeOrItemAttributesLike(type, pattern, pattern,
					PageRequest.of(page, size, Direction.ASC, "space_id", "type"));
		} else if (user.getRole().equals(UserRole.PLAYER.name())) {
			entities = this.itemDao.findAllByActiveAndTypeAndNameLikeOrItemAttributesLike(true, type, pattern, pattern,
					PageRequest.of(page, size, Direction.ASC, "space_id", "type"));
		} else {
			throw new RuntimeException(user.getRole() + " can't searchByItemAttributes!");
		}

		List<ItemBoundary> rv = new ArrayList<>();
		for (ItemEntity entity : entities) {
			ItemBoundary boundary = convertToBoundary(entity);
			rv.add(boundary);
		}
		return rv;
	}

}

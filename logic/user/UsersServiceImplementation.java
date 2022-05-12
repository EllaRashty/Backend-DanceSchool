package twins.logic.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twins.data.UserEntity;
import twins.data.UserRole;
import twins.data.user.UserDao;

@Service
public class UsersServiceImplementation implements AdvancedUsersService {
	private UserDao userDao;
	private String space;

	public UsersServiceImplementation() {
	}

	@Autowired
	public UsersServiceImplementation(UserDao userDao) {
		super();
		this.userDao = userDao;
	}

	// read this value from Spring configuration
	@Value("${spring.application.name}")
	public void setSpace(String space) {
		this.space = space;
	}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		UserEntity entity = convertFromBoundary(user);
		if (this.userDao.existsById(entity.getSpaceEmail())) {
			throw new RuntimeException("User already exists!");
		}
		entity.setUserTimestamp(new Date());
		entity = this.userDao.save(entity);

		return convertToBoundary(entity);
	}

	private UserBoundary convertToBoundary(UserEntity entity) {
		UserBoundary boundary = new UserBoundary();
		String[] spaceEmail = entity.getSpaceEmail().split(" ");
		boundary.setUserId(new UserId(spaceEmail[0], spaceEmail[1]));
		boundary.setRole(entity.getRole());
		boundary.setUsername(entity.getUsername());
		boundary.setAvatar(entity.getAvatar());
		return boundary;
	}

	private UserEntity convertFromBoundary(UserBoundary user) {
		UserEntity entity = new UserEntity();
		String email = user.getUserId().getEmail().toLowerCase();
		String role = user.getRole();
		String username = user.getUsername();
		String avatar = user.getAvatar();
		if (checkEmail(email) && checkRole(role) && checkUsername(username) && checkAvatar(avatar)) {
			entity.setSpaceEmail(this.space + " " + email);
			entity.setRole(role);
			entity.setUsername(username);
			entity.setAvatar(avatar);
		} else {
			throw new RuntimeException();
		}
		return entity;
	}

	private boolean checkAvatar(String avatar) {
		return !avatar.isEmpty() || avatar != null;
	}

	private boolean checkUsername(String username) {
		return !username.isEmpty() || username != null;
	}

	private boolean checkRole(String role) {
		return UserRole.valueOf(role) != null;
	}

	private boolean checkEmail(String email) {
		return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
	}

	@Override
	public boolean hasPermission(String userSpace, String userEmail, UserRole role) {
		UserBoundary user = login(userSpace, userEmail);
		return (UserRole.valueOf(user.getRole()) == role);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userSpace, String userEmail) {
		if (checkEmail(userEmail)) {
			return convertToBoundary(getUserById(userSpace, userEmail));
		} else {
			throw new RuntimeException("Email format is invalid (use: example@example.com)"+ userEmail); // TODO: return bad email
		}
	}

	private UserEntity getUserById(String userSpace, String userEmail) {
		Optional<UserEntity> op = this.userDao.findById(userSpace + " " + userEmail);
		if (op.isPresent()) {
			UserEntity entity = op.get();
			return entity;
		} else {
			throw new RuntimeException("User not found!"); // TODO: return status = 404 instead of status = 500
		}
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userSpace, String userEmail, UserBoundary update) {
		UserEntity entity = getUserById(userSpace, userEmail);
		UserEntity updatedEntity = convertFromBoundary(update);
		updatedEntity.setSpaceEmail(entity.getSpaceEmail());
		updatedEntity.setUserTimestamp(entity.getUserTimestamp());
		return convertToBoundary(this.userDao.save(updatedEntity));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail) {
		// login(adminSpace, adminEmail);
		// Iterable<UserEntity> entities = this.userDao.findAll();
		// List<UserBoundary> userBoundaries = new ArrayList<>();
		// for (UserEntity entity : entities) {
		// userBoundaries.add(convertToBoundary(entity));
		// }
		// return userBoundaries;
		throw new RuntimeException("deprecated users - use the new API getAllUsers(size, page)");

	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminSpace, String adminEmail) {
		UserRole role = UserRole.ADMIN;
		if (!hasPermission(adminSpace, adminEmail, role)) {
			throw new RuntimeException("Only " + role.name() + " can deleteAllUsers!");
		}

		this.userDao.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(int size, int page, String space, String email) {
		System.err.println("space" + space + "size" + size +"email" + email );
		if (!hasPermission(space, email, UserRole.ADMIN)) {
			throw new RuntimeException("Only " + UserRole.ADMIN.name() + " can getManyUsers!");
		}

		Page<UserEntity> pageOfEntities = this.userDao
				.findAll(PageRequest.of(page, size, Direction.ASC, "spaceEmail", "username"));

		List<UserEntity> entities = pageOfEntities.getContent();
		List<UserBoundary> rv = new ArrayList<>();
		for (UserEntity entity : entities) {
			UserBoundary boundary = convertToBoundary(entity);

			rv.add(boundary);
		}
		
		return rv;
	}

}

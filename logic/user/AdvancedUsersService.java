package twins.logic.user;

import java.util.List;

import twins.data.UserRole;
import twins.logic.UsersService;

public interface AdvancedUsersService extends UsersService {

	public List<UserBoundary> getAllUsers(int size, int page, String space, String email);

	public boolean hasPermission(String userSpace, String userEmail, UserRole role);
}

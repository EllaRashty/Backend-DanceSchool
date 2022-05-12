package twins.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/*
						USERS
--------------------------------------------------------------
	SPACE_EMAIL | USERNAME | ROLE | AVATAR | USER_CREATION
	    <PK>    |
*/
@Entity
@Table(name = "USERS")
public class UserEntity {
	private String spaceEmail; // UserId
	private String role;
	private String username;
	private String avatar;
	private Date userTimestamp;

	public UserEntity() {
	}

	@Id
	public String getSpaceEmail() {
		return spaceEmail;
	}

	public void setSpaceEmail(String spaceEmail) {
		this.spaceEmail = spaceEmail;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "USER_CREATION")
	public Date getUserTimestamp() {
		return userTimestamp;
	}

	public void setUserTimestamp(Date userTimestamp) {
		this.userTimestamp = userTimestamp;
	}

}

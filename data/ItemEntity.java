package twins.data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ITEMS")
public class ItemEntity {

	private String spaceId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private String spaceEmail; // createBy
	private String itemAttributes;
	private Integer numberOfLessons;
	private String[] lessons;
	private String img;


	@Id
	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public String[] getLessons() {
		return lessons;
	}

	public void setLessons(String[] lessons) {
		this.lessons = lessons;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ITEM_CREATION")
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getSpaceEmail() {
		return spaceEmail;
	}

	public void setSpaceEmail(String spaceEmail) {
		this.spaceEmail = spaceEmail;
	}

	@Column(columnDefinition = "TEXT")
	public String getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(String itemAttributes) {
		this.itemAttributes = itemAttributes;
	}

	public Integer getNumberOfLessons() {
		return numberOfLessons;
	}

	public void setNumberOfLessons(Integer numberOfLessons) {
		this.numberOfLessons = numberOfLessons;
	}

	public String getImg() {
		// TODO Auto-generated method stub
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}

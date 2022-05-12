package twins.logic.item;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import twins.logic.Id;
import twins.logic.user.User;

//{
//    "itemId": {
//        "space": "DanceSchool",
//        "id": "4"
//    },
//    "type": "Beginner",
//    "name":"First steps in modern dance",
//    "createdBy":{
//        "userId":{
//            "space": "DanceSchool",
//            "email": "teacher2@gmail.com"
//        }
//    },
//    "itemAttributes": {
//      "typeOfDance":"Modern dance",
//    	"workingHours":"10 hours",
//      "reviews":["good", "bad"],
//      "description":"A classical modern dance class suitable for beginners"       
//    }    
//}


public class ItemBoundary {
	private Id itemId;
	private String type; 
	private String name; 
	private Boolean active;
	private Date createdTimestamp;
	private User createdBy;
	private String img;
	private Map<String, Object> itemAttributes; // See ItemAttributes enum class for details.
	private Integer numberOfLessons;
	private Lesson[] lessons;   

	public ItemBoundary() {
		this.active = true;
		this.createdTimestamp = new Date();
		this.itemAttributes = new HashMap<>();
		//this.lessons = new Lesson[numberOfLessons];
	}

	public ItemBoundary(Id itemId, String type, String name, User createdBy,String img, Integer numberOfLessons) {
		this();
		this.itemId = itemId;
		this.type = type;
		this.name = name;
		this.createdBy = createdBy;
		this.img = img;
		this.numberOfLessons = numberOfLessons;
		if(numberOfLessons != null)
			this.lessons = new Lesson[numberOfLessons];
	}
	public Lesson[] getLessons() {
		return lessons;
	}

	public void setLessons(Lesson[] lessons) {
		this.lessons = lessons;
	}
	
	public Id getItemId() {
		return itemId;
	}

	public void setItemId(Id itemId) {
		this.itemId = itemId;
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

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	} 

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Map<String, Object> getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(Map<String, Object> itemAttributes) {
		this.itemAttributes = itemAttributes;
	}

	public Integer getNumberOfLessons() {
		return numberOfLessons;
	}

	public void setNumberOfLessons(Integer numberOfLessons) {
		this.numberOfLessons = numberOfLessons;
	}

}

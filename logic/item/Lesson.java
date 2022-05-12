package twins.logic.item;

public class Lesson {
	private String name;
//	private int id;
	private String length;
	private String url;
	
	
	public Lesson() {
	}

	public Lesson(String name, String length, String url) {
		this.name = name;
		this.length = length;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
	
	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}

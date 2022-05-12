package twins.logic.user;

public class ActiveClass{
	private String classId;
	private String lessonId;
	private String time;
	
	public ActiveClass(String classId, String lessonId, String time) {
		super();
		this.classId = classId;
		this.lessonId = lessonId;
		this.time = time;
	}
	
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getLessonId() {
		return lessonId;
	}
	public void setLessonId(String lessonId) {
		this.lessonId = lessonId;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	

}

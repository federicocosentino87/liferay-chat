package liferay.chat.model;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

public class UserPojo {

	public UserPojo(long userId, String fullName, String portraitUrl) {
		super();
		this.userId = userId;
		this.fullName = fullName;
		this.portraitUrl = portraitUrl;
	}
	
	long userId;
	String fullName;
	String portraitUrl;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getPortraitUrl() {
		return portraitUrl;
	}
	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}
	
	public JSONObject toJSON() {
		JSONObject json = JSONFactoryUtil.createJSONObject();
		
		json.put("userId", userId);
		json.put("fullName", fullName);
		json.put("portraitUrl", portraitUrl);
		
		return json;
	}
	
}

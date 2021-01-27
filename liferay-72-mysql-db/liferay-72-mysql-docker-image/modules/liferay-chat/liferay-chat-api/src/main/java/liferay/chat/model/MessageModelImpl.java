package liferay.chat.model;

import java.io.Serializable;

import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;

@JSON(strict = true)
public class MessageModelImpl extends BaseModelImpl<ChatMessage> {

	@Override
	public Serializable getPrimaryKeyObj() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toXmlString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getModelClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(ChatMessage arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

}

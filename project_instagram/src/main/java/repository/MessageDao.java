package repository;

import java.util.List;
import java.util.Map;

public interface MessageDao {

	public Map<String, List<?>> selectRecentMessages(int user_id);
	
	public int insertDirectTextMessage(int user_id, int target_user_id, String contents);
}
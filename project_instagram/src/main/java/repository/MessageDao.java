package repository;

import java.util.List;
import java.util.Map;

import entity.RoomInfo;

public interface MessageDao {

	public Map<String, List<?>> selectRecentMessages(int user_id);
	
	public int insertDirectTextMessage(int user_id, int target_user_id, String contents);
	
	public List<RoomInfo> selectSpecificRoom(int user_id, List<Integer> target_user_ids);
	
	public boolean insertNewRoom(int user_id, List<Integer> target_user_ids);
}

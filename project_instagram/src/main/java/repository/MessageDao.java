package repository;

import java.util.List;
import java.util.Map;

import entity.Message;
import entity.RoomInfo;

public interface MessageDao {

	public Map<String, List<?>> selectRecentMessages(int user_id);
	
	public int insertDirectTextMessage(int user_id, int room_id, String contents);

	public int insertDirectImageMessage(int user_id, int room_id, String file_name);
	
	public int selectSpecificRoomId(int user_id, List<Integer> target_user_ids);
	
	public int insertNewRoom(int user_id, List<Integer> target_user_ids);
	
	public List<Message> selectMessages(int room_id);
	
	public List<RoomInfo> selectRoomInfoForInit(int user_id);
	
	public List<Integer> insertMessageReaction(int user_id, int message_id);

	public List<Integer> deleteMessageReaction(int user_id, int message_id);
	
	public int updateMessageToDelete(int message_id);
	
	public List<Integer> selectRoomIdByMessageId(List<Integer> message_ids);
}

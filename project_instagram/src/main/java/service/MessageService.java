package service;

import java.util.List;

import response_dto.MessageResDto;
import response_dto.RoomSummaryResDto;

public interface MessageService {

	public boolean insertDirectTextMessage(int user_id, int room_id, String contents);
	
	public boolean insertDirectImageMessage(int user_id, int room_id, String file_name);
	
	public List<MessageResDto> insertNewRoom(int user_id, List<Integer> target_user_ids);
	
	public List<RoomSummaryResDto> selectRoomInfoForInit(int user_id);
	
	public List<MessageResDto> selectMessages(int user_id, int room_id);
	
	public List<Integer> insertMessageReaction(int user_id, int message_id);

	public List<Integer> deleteMessageReaction(int user_id, int message_id);
	
	public List<Boolean> checkRoomIdByMessageId(int room_id, List<Integer> message_ids);
	
	public boolean updateMessageToDelete(int message_id);
}

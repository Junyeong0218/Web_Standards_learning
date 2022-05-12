package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBConnectionMgr;
import entity.Message;
import entity.RoomInfo;

public class MessageDaoImpl implements MessageDao {

	private DBConnectionMgr db;
	
	public MessageDaoImpl() {
		db = DBConnectionMgr.getInstance();
	}
	
	@Override
	public Map<String, List<?>> selectRecentMessages(int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		Map<String, List<?>> messageMap = new HashMap<String, List<?>>();
		
		try {
			conn = db.getConnection();
			sql = "SELECT "
						+ "dm.id, "
						
						+ "dm.user_id, "
						+ "um.username, "
						+ "um.`name`, "
						+ "um.has_profile_image, "
						+ "up.file_name, "
	
						+ "dm.target_user_id, "
						+ "um2.username, "
						+ "um2.`name`, "
						+ "um2.has_profile_image, "
						+ "up2.file_name, "
	
						+ "dm.`contents`, "
						+ "dm.is_image, "
						+ "dm.image_id, "
						+ "dmi.file_name AS image_message_file_name, "
						+ "dm.create_date, "
						+ "dmr.id AS reaction_flag "
					+ "FROM "
						+ "direct_message_mst dm "
						+ "LEFT OUTER JOIN user_mst um ON(um.id = dm.user_id AND um.id != ?) "
						+ "LEFT OUTER JOIN user_profile_image up ON(up.user_id = um.id) "
						+ "LEFT OUTER JOIN user_mst um2 ON(um2.id = dm.target_user_id AND um2.id != ?) "
						+ "LEFT OUTER JOIN user_profile_image up2 ON(up2.user_id = um2.id) "
						+ "LEFT OUTER JOIN direct_message_image dmi ON(dmi.id = dm.image_id) "
						+ "LEFT OUTER JOIN direct_message_reaction dmr ON(dmr.direct_message_id = dm.id) "
					+ "WHERE "
						+ "dm.target_user_id = ? OR dm.user_id = ?"
					+ "GROUP BY "
						+ "um.id "
					+ "ORDER BY"
						+ "dm.create_date desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, user_id);
			pstmt.setInt(3, user_id);
			pstmt.setInt(4, user_id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}

		return messageMap;
	}
	
	@Override
	public int insertDirectTextMessage(int user_id, int room_id, String contents) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "insert into direct_message_mst values(0, ?, ?, ?, 0, null, now(), now(), 0, null);";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, room_id);
			pstmt.setString(3, contents);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return result;
	}
	
	@Override
	public int insertDirectImageMessage(int user_id, int room_id, String file_name) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		final String contents = "메시지를 보냈습니다";
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "insert into direct_message_image values(0, ?, now(), now());";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, file_name);
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				pstmt.close();
				sql = "select id from direct_message_image where file_name = ?;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, file_name);
				rs = pstmt.executeQuery();
				
				int image_id = 0;
				
				while(rs.next()) {
					image_id = rs.getInt("id");
				}
				
				if(image_id != 0) {
					pstmt.close();
					sql = "insert into direct_message_mst values(0, ?, ?, ?, 1, ?, now(), now(), 0, null);";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, user_id);
					pstmt.setInt(2, room_id);
					pstmt.setString(3, contents);
					pstmt.setInt(4, image_id);
					
					result += pstmt.executeUpdate();
				}
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return result;
	}
	
	@Override
	public int selectSpecificRoomId(int user_id, List<Integer> target_user_ids) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int room_id = 0;
		
		try {
			conn = db.getConnection();
			sql = "SELECT "
						+ "room_users.id, "
						+ "room_users.room_id, "
						+ "room_users.user_id, "
						+ "COUNT(room_users2.user_id) AS entered_users_count "
					+ "FROM "
						+ "direct_message_room_entered_users room_users "
						+ "LEFT OUTER JOIN direct_message_room_mst room ON(room.id = room_users.room_id) "
						+ "LEFT OUTER JOIN direct_message_room_entered_users room_users2 ON(room_users2.room_id = room.id) "
					+ "WHERE "
						+ "room_users.user_id IN(" + user_id + ", ";
			for(int id : target_user_ids) {
				sql += id + ", ";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += ") "
					+ "GROUP BY "
						+ "room_users.id "
					+ "HAVING "
						+ "entered_users_count = ?;";
			System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, target_user_ids.size() + 1);
			rs = pstmt.executeQuery();
			
			int row_count = 0;
			if(rs != null) {
				rs.last();
				row_count = rs.getRow();
				
				if(row_count == target_user_ids.size() + 1) {
					rs.first();
					room_id = rs.getInt("room_id");
				}
			}
			
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return room_id;
	}
	
	@Override
	public int insertNewRoom(int user_id, List<Integer> target_user_ids) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int room_id = 0;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "insert into direct_message_room_mst values(0, ?, now(), now());";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				pstmt.close();
				sql = "select id from direct_message_room_mst where made_user_id = ? order by create_date desc;";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, user_id);
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					room_id = rs.getInt("id");
				}
				target_user_ids.add(user_id);
				
				if(room_id != 0) {
					for(int i = 0; i < target_user_ids.size(); i++) {
						pstmt.close();
						sql = "insert into direct_message_room_entered_users values(0, ?, ?, now(), now());";
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, room_id);
						pstmt.setInt(2, target_user_ids.get(i));
						result += pstmt.executeUpdate();
					}
				}
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		if(result > 0) {
			return room_id;
		} else {
			return 0;
		}
	}
	
	@Override
	public List<Message> selectMessages(int room_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<Message> messages = new ArrayList<Message>();
		
		try {
			conn = db.getConnection();
			sql  = "SELECT "
						+ "message.id, "
						+ "room_users.room_id, "
						+ "room_users.user_id, "
						+ "message.`contents`, "
						+ "message.is_image, "
						+ "message.image_id, "
						+ "image.file_name, "
						+ "reaction.user_id as like_user_id, "
						+ "message.create_date "
					+ "FROM  "
						+ "direct_message_room_entered_users room_users "
						+ "LEFT OUTER JOIN direct_message_mst message ON(message.user_id = room_users.user_id and message.room_id = room_users.room_id) "
						+ "LEFT OUTER JOIN direct_message_image image ON(image.id = message.image_id AND message.is_image = 1) "
						+ "LEFT OUTER JOIN direct_message_reaction reaction ON(reaction.direct_message_id = message.id) "
					+ "WHERE "
						+ "room_users.room_id = ? "
					+ "GROUP BY "
						+ "message.id, "
						+ "reaction.user_id "
					+ "ORDER BY "
						+ "message.create_date desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, room_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Message message = new Message();
				message.setId(rs.getInt("id"));
				message.setRoom_id(rs.getInt("room_id"));
				message.setUser_id(rs.getInt("user_id"));
				message.setContents(rs.getString("contents"));
				message.set_image(rs.getBoolean("is_image"));
				message.setImage_id(rs.getInt("image_id"));
				message.setFile_name(rs.getString("file_name"));
				message.setLike_user_id(rs.getInt("like_user_id"));
				message.setCreate_date(rs.getTimestamp("create_date") != null ? rs.getTimestamp("create_date").toLocalDateTime() : null);
				
				messages.add(message);
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return messages;
	}
	
	@Override
	public List<RoomInfo> selectRoomInfoForInit(int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<RoomInfo> rooms = new ArrayList<RoomInfo>();
		
		try {
			conn = db.getConnection();
			sql = "select "
						+ "dm.id, "
						+ "room_users.room_id, "
						+ "room_users2.user_id, "
						+ "um.username, "
						+ "um.`name`, "
						+ "um.has_profile_image, "
						+ "up.file_name, "
						+ "dm.`contents`, "
						+ "dm.create_date "
					+ "from "
						+ "direct_message_room_entered_users room_users "
						+ "left outer join direct_message_room_entered_users room_users2 on(room_users2.room_id = room_users.room_id) "
						+ "left outer join user_mst um on(um.id = room_users2.user_id) "
						+ "left outer join user_profile_image up on(up.user_id = um.id) "
						+ "left outer join direct_message_mst dm on(dm.room_id = room_users.room_id and "
							+ "dm.create_date = (select "
																	+ "create_date "
																+ "from "
																	+ "direct_message_mst "
																+ "where "
																	+ "room_id = dm.room_id "
																+ "order by "
																	+ "create_date desc "
																+ "limit 1)) "
					+ "where "
						+ "room_users.user_id = ? "
					+ "group by "
						+ "room_users.room_id, "
						+ "room_users2.user_id "
					+ "order by "
						+ "dm.create_date desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				RoomInfo room = new RoomInfo();
				room.setId(rs.getInt("id"));
				room.setRoom_id(rs.getInt("room_id"));
				room.setUser_id(rs.getInt("user_id"));
				room.setUsername(rs.getString("username"));
				room.setName(rs.getString("name"));
				room.setHas_profile_image(rs.getBoolean("has_profile_image"));
				room.setFile_name(rs.getString("file_name"));
				room.setContents(rs.getString("contents"));
				room.setCreate_date(rs.getTimestamp("create_date") != null ? rs.getTimestamp("create_date").toLocalDateTime() : null);
				
				rooms.add(room);
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		System.out.println(rooms);
		return rooms;
	}
	
	@Override
	public List<Integer> toggleMessageReaction(int user_id, int message_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int result = 0;
		List<Integer> like_users = new ArrayList<Integer>();
		
		try {
			conn = db.getConnection();
			sql = "select id from direct_message_reaction where user_id = ? and direct_message_id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, message_id);
			rs = pstmt.executeQuery();
			
			int id  = 0;
			
			if(rs.next()) {
				id = rs.getInt("id");
			}
			
			pstmt.close();
			
			if(id == 0) {
				sql = "insert into direct_message_reaction values(0, ?, ?, now(), now());";
			} else {
				sql = "delete from direct_message_reaction where user_id = ? and direct_message_id = ?;";
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, message_id);
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				pstmt.close();
				rs.close();
				sql = "select user_id from direct_message_reaction where direct_message_id = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, message_id);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					like_users.add(rs.getInt("user_id"));
				}
			}
		} catch (SQLDataException e) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return like_users;
	}
}

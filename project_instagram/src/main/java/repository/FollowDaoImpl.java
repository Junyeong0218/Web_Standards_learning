package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import db.DBConnectionMgr;
import entity.Activity;
import entity.ArticleComment;
import entity.ArticleDetail;
import entity.Follow;
import entity.HashTag;
import entity.User;
import entity.UserProfile;
import response_dto.UserRecommendResDto;

public class FollowDaoImpl implements FollowDao {
	
	private DBConnectionMgr db;
	
	public FollowDaoImpl() {
		db = DBConnectionMgr.getInstance();
	}
	
	@Override
	public List<UserRecommendResDto> selectRecommendUsers(int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		List<UserRecommendResDto> userList = new ArrayList<UserRecommendResDto>();
		
		try {
			conn = db.getConnection();
			sql = "select "
					+ "um.id ,"
					+ "um.username, "
					+ "um.name, "
					+ "ud.has_profile_image, "
					+ "`up`.file_name "
				+ "from "
					+ "user_mst um "
					+ "left outer join user_detail ud on(ud.user_id = um.id) "
					+ "left outer join user_profile_image `up` on(um.id = `up`.user_id) "
				+ "where "
					+ "um.id != ? and "
					+ "um.disable_flag = 0 and "
					+ "um.id not in(select fm.partner_user_id from follow_mst fm where fm.user_id = ?) "
				+ "group by "
					+ "um.id "
				+ "limit 5;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, user_id);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				UserRecommendResDto resDto = new UserRecommendResDto();
				resDto.setId(rs.getInt("id"));
				resDto.setUsername(rs.getString("username"));
				resDto.setName(rs.getString("name"));
				resDto.setHas_profile_image(rs.getInt("has_profile_image") == 1 ? true : false);
				resDto.setFile_name(rs.getCharacterStream("file_name") == null ? "" : rs.getString("file_name"));
				
				userList.add(resDto);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return userList;
	}

	@Override
	public int insertFollowUser(int partner_user_id, int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "insert into follow_mst values(0, ?, ?, null, 'COMMON', now(), now());";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, partner_user_id);
			
			result = pstmt.executeUpdate();
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return result;
	}
	
	@Override
	public int deleteFollowUser(int partner_user_id, int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "delete from follow_mst where user_id = ? and partner_user_id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, partner_user_id);
			
			result = pstmt.executeUpdate();
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return result;
	}
	
	@Override
	public int insertFollowHashTag(int hash_tag_id, int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "insert into follow_mst values(0, ?, null, ?, null, now(), now());";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, hash_tag_id);
			
			result = pstmt.executeUpdate();
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return result;
	}
	
	@Override
	public int deleteFollowHashTag(int hash_tag_id, int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		int result = 0;
		
		try {
			conn = db.getConnection();
			sql = "delete from follow_mst where user_id = ? and followed_hash_tag_id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, hash_tag_id);
			
			result = pstmt.executeUpdate();
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return result;
	}
	
	@Override
	public List<Activity> selectActivities(int user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<Activity> activities = new ArrayList<Activity>();
		
		try {
			conn = db.getConnection();
			sql = "SELECT "
						+ "al.id, "
						+ "al.user_id, "
						+ "um.username, "
						+ "ud.has_profile_image, "
						+ "up.file_name, "

						+ "al.related_user_id, "
						+ "um2.username AS related_username, "
						+ "ud2.has_profile_image AS related_user_has_profile_image, "
						+ "up2.file_name AS related_user_file_name, "

						+ "al.activity_flag, "
						+ "al.activity_message, "

						+ "al.article_id, "
						+ "media.media_name, "

						+ "al.comment_id, "
						+ "ac.`contents`, "
						
						+ "al.follow_id, "
						+ "fm.follower_group, "

						+ "al.create_date, "
						+ "al.update_date "
					+ "FROM "
						+ "activity_logs al "
						+ "LEFT OUTER JOIN user_mst um ON(um.id = al.user_id) "
						+ "LEFT OUTER JOIN user_detail ud ON(ud.user_id = al.user_id) "
						+ "LEFT OUTER JOIN user_profile_image up ON(up.user_id = al.user_id) "
						+ "LEFT OUTER JOIN user_mst um2 ON(um2.id = al.related_user_id) "
						+ "LEFT OUTER JOIN user_detail ud2 ON(ud2.user_id = al.related_user_id) "
						+ "LEFT OUTER JOIN user_profile_image up2 ON(up2.user_id = al.related_user_id) "
						+ "LEFT OUTER JOIN article_media media ON(media.article_id = al.article_id) "
						+ "LEFT OUTER JOIN article_comment ac ON(ac.id = al.comment_id) "
						+ "LEFT OUTER JOIN follow_mst fm ON(fm.id = al.follow_id) "
					+ "WHERE  "
						+ "al.related_user_id = ? and al.user_id != ? "
					+ "GROUP BY "
						+ "al.id "
					+ "ORDER BY "
						+ "al.create_date desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, user_id);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Activity activity = new Activity();
				activity.setId(rs.getInt("id"));
				activity.setUser_id(rs.getInt("user_id"));
				activity.setUsername(rs.getString("username"));
				activity.setHas_profile_image(rs.getInt("has_profile_image") == 0 ? false : true);
				activity.setFile_name(rs.getCharacterStream("file_name") == null ? "" : rs.getString("file_name"));
				activity.setRelated_user_id(rs.getInt("related_user_id"));
				activity.setRelated_username(rs.getString("related_username"));
				activity.setRelated_user_has_profile_image(rs.getInt("related_user_has_profile_image") == 0 ? false : true);
				activity.setRelated_user_file_name(rs.getCharacterStream("related_user_file_name") == null ? "" : rs.getString("related_user_file_name"));
				activity.setActivity_flag(rs.getString("activity_flag"));
				activity.setActivity_message(rs.getString("activity_message"));
				
				ArticleDetail articleDetail = new ArticleDetail();
				articleDetail.setArticle_id(rs.getInt("article_id"));
				articleDetail.setMedia_name(rs.getString("media_name"));
				
				activity.setArticleDetail(articleDetail);
				
				ArticleComment comment = new ArticleComment();
				comment.setId(rs.getInt("comment_id"));
				comment.setContents(rs.getString("contents"));
				
				activity.setArticleComment(comment);
				
				Follow follow = new Follow();
				follow.setId(rs.getInt("follow_id"));
				follow.setFollower_group(rs.getString("follower_group"));
				
				activity.setFollow(follow);
				activity.setCreate_date(rs.getTimestamp("create_date") != null ? rs.getTimestamp("create_date").toLocalDateTime() : null);
				activity.setUpdate_date(rs.getTimestamp("update_date") != null ? rs.getTimestamp("update_date").toLocalDateTime() : null);
				
				activities.add(activity);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		
		return activities;
	}
	
	@Override
	public List<UserProfile> selectUserProfileInfo(String username, int session_user_id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<UserProfile> userProfile = new ArrayList<UserProfile>();
		
		try {
			conn = db.getConnection();
			sql = "select "
						+ "am.id, "
						+ "um.id as `user_id`, "
						+ "um.username, "
						+ "um.`name`, "
						+ "ud.has_profile_image, "
						+ "up.file_name, "
						+ "am.is_stored, "
						+ "am.create_date, "
						+ "count(distinct ar.id) as `like_count`, "
						+ "count(distinct ac.id) as `comment_count`, "
						
						+ "media.media_type, "
						+ "media.media_name, "
						
						+ "fm3.partner_user_id, "
						+ "count(distinct fm.partner_user_id) as following, "
						+ "count(distinct fm2.user_id) as follower "
					+ "from "
						+ "user_mst um "
						+ "left outer join user_detail ud on(ud.user_id = um.id) "
						+ "left outer join user_profile_image up on(up.user_id = um.id) "
						+ "left outer join article_mst am on(am.user_id = um.id and am.deleted_flag = false) "
						+ "left outer join article_media media on(media.article_id = am.id and media.media_name like \"%01%\") "
						+ "left outer join article_reaction ar on(ar.article_id = am.id) "
						+ "left outer join article_comment ac on(ac.article_id = am.id) "
						+ "left outer join follow_mst fm on(fm.user_id = um.id) "
						+ "left outer join follow_mst fm2 on(fm2.partner_user_id = um.id) "
						+ "left outer join follow_mst fm3 on(fm3.user_id = ? and fm3.partner_user_id = um.id) "
					+ "where "
						+ "um.username = ? "
					+ "group by "
						+ "am.id "
					+ "order by "
						+ "am.create_date desc;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, session_user_id);
			pstmt.setString(2, username);
			System.out.println("session_user_id : " + session_user_id);
			System.out.println("target_username : " + username);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				UserProfile profile = new UserProfile();
				
				profile.setArticle_id(rs.getInt("id"));
				profile.setUser_id(rs.getInt("user_id"));
				profile.setUsername(rs.getString("username"));
				profile.setName(rs.getString("name"));
				profile.setHas_profile_image(rs.getInt("has_profile_image") == 0 ? false : true);
				profile.setFile_name(rs.getCharacterStream("file_name") == null ? "" : rs.getString("file_name"));
				profile.setMedia_type(rs.getString("media_type"));
				profile.setMedia_name(rs.getString("media_name"));
				profile.set_stored(rs.getBoolean("is_stored"));
				profile.setCreate_date(rs.getTimestamp("create_date") != null ? rs.getTimestamp("create_date").toLocalDateTime() : null);
				profile.setLike_count(rs.getInt("like_count"));
				profile.setComment_count(rs.getInt("comment_count"));
				profile.setFollow_flag(rs.getInt("partner_user_id") > 0 ? true : false);
				profile.setFollower(rs.getInt("follower"));
				profile.setFollowing(rs.getInt("following"));
				System.out.println(profile);
				userProfile.add(profile);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt);
		}
		System.out.println(userProfile);
		return userProfile;
	}
	
	@Override
	public List<User> selectFollowingUsers(int user_id, int page_indicator) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<User> followingUserList = new ArrayList<User>();
		
		try {
			conn = db.getConnection();
			sql = "select "
						+ "fm.id, "
						+ "fm.partner_user_id, "
						+ "um.username, "
						+ "um.name, "
						+ "ud.has_profile_image, "
						+ "up.file_name "
					+ "from "
						+ "follow_mst fm "
						+ "left outer join user_mst um on(um.id = fm.partner_user_id) "
						+ "left outer join user_detail ud on(ud.user_id = fm.partner_user_id) "
						+ "left outer join user_profile_image up on(up.user_id = fm.partner_user_id) "
					+ "where "
						+ "fm.user_id = ? "
					+ "limit ?, 11;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, page_indicator * 10);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				User user = new User();
				user.setId(rs.getInt("partner_user_id"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setHas_profile_image(rs.getInt("has_profile_image") == 1 ? true : false);
				user.setFile_name(rs.getCharacterStream("file_name") == null ? "" : rs.getString("file_name"));
				
				followingUserList.add(user);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return followingUserList;
	}
	
	@Override
	public List<HashTag> selectFollowingHashTags(int user_id, int page_indicator) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<HashTag> followingHashTagList = new ArrayList<HashTag>();
		
		try {
			conn = db.getConnection();
			sql = "SELECT "
						+ "htm.id, "
						+ "htm.tag_name, "
						+ "htm.create_date, "
						+ "htm.update_date "
					+ "FROM "
						+ "follow_mst fm "
						+ "LEFT OUTER JOIN hash_tag_mst htm ON(htm.id = fm.followed_hash_tag_id) "
					+ "WHERE "
						+ "fm.user_id = ? "
					+ "LIMIT ?, 11;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, page_indicator * 10);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				if(rs.getInt("id") == 0) continue;
				HashTag hashTag = new HashTag();
				hashTag.setId(rs.getInt("id"));
				hashTag.setTag_name(rs.getString("tag_name"));
				hashTag.setCreate_date(rs.getTimestamp("create_date") == null ? null : rs.getTimestamp("create_date").toLocalDateTime());
				hashTag.setUpdate_date(rs.getTimestamp("update_date") == null ? null : rs.getTimestamp("update_date").toLocalDateTime());
				
				followingHashTagList.add(hashTag);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return followingHashTagList;
	}
	
	@Override
	public List<User> selectFollwers(int user_id, int page_indicator) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<User> followers = new ArrayList<User>();
		
		try {
			conn = db.getConnection();
			sql = "select "
						+ "fm.user_id, "
						+ "um.username, "
						+ "um.`name`, "
						+ "ud.has_profile_image, "
						+ "up.file_name "
					+ "from "
						+ "follow_mst fm "
						+ "left outer join user_mst um on(um.id = fm.user_id) "
						+ "left outer join user_detail ud on(ud.user_id = fm.user_id) "
						+ "left outer join user_profile_image up on(up.user_id = fm.user_id) "
					+ "where "
						+ "fm.partner_user_id = ? "
					+ "group by "
						+ "fm.user_id "
					+ "limit ?, 11";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, page_indicator);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				User user = new User();
				user.setId(rs.getInt("user_id"));
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setHas_profile_image(rs.getInt("has_profile_image") == 1 ? true : false);
				user.setFile_name(rs.getCharacterStream("file_name") == null ? "" : rs.getString("file_name"));
				
				followers.add(user);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return followers;
	}
	
	@Override
	public int selectUserCount(String username) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		int userCount = 0;
		
		try {
			conn = db.getConnection();
			sql = "select count(username) from user_mst where username = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, username);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				userCount = rs.getInt(1);
			}
		} catch(SQLDataException e1) {
			System.out.println("no row!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.freeConnection(conn, pstmt, rs);
		}
		
		return userCount;
	}
}

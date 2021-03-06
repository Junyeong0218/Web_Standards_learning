package repository;

import java.util.List;

import entity.ArticleDetail;
import entity.LatestSearchDetail;
import entity.SearchKeyword;
import entity.User;

public interface SearchDao {

	public List<LatestSearchDetail> selectLatestSearches(int user_id);
	
	public List<SearchKeyword> selectHashTags(String keyword, int user_id);
	
	public List<SearchKeyword> selectKeyword(String keyword, int user_id);
	
	public List<ArticleDetail> selectArticlesAboutHashTag(String tag_name);
	
	public int insertLatestSearch(boolean isUser, int id, int user_id);
	
	public List<User> selectUsers(String keyword, int user_id);
}

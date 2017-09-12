package spring_mongo.dao;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import spring_mongo.model.BoardsModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/3.
 */
public interface BoardsDao {
    /**
     * 查询数据
     *
     * @author：tuzongxun
     * @Title: findAll
     * @param @return
     * @return List<UserModel>
     * @date May 13, 2016 3:07:39 PM
     * @throws
     */
    public List<BoardsModel> findAll();

    /**
     * 新增数据
     *
     * @author：tuzongxun
     * @Title: insertUser
     * @param @param user
     * @return void
     * @date May 13, 2016 3:09:45 PM
     * @throws
     */
    public void insertUser(BoardsModel boardsModel);

    /**
     * 删除数据
     *
     * @author：tuzongxun
     * @Title: removeUser
     * @param @param userName
     * @return void
     * @date May 13, 2016 3:09:55 PM
     * @throws
     */
    public void removeUser(String boardName);

    /**
     * 修改数据
     *
     * @author：tuzongxun
     * @Title: updateUser
     * @param @param user
     * @return void
     * @date May 13, 2016 3:10:06 PM
     * @throws
     */
    public void updateUser(BoardsModel boardsModel);

    /**
     * 按条件查询
     *
     * @author：tuzongxun
     * @Title: findForRequery
     * @param
     * @return void
     * @date May 13, 2016 3:23:37 PM
     * @throws
     */
    public List<BoardsModel> findForRequery(String boardName);

    /**
     * mongodb简单的分组查询
     *
     * @author：tuzongxun
     * @Title: mongoGroup
     * @param @return
     * @return BasicDBList
     * @date Jul 19, 2016 8:18:54 AM
     * @throws
     */
    public BasicDBList mongoGroup();

    public void saveData(DBObject obj);
}

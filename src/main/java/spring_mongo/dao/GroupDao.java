package spring_mongo.dao;

import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.GroupModel;
import spring_mongo.model.TicketModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/6.
 */
@Transactional
public interface GroupDao {
    public GroupModel getByID(String id);
    /**
     *
     * 获取楼宇单元
     * @return
     */
    List<GroupModel> getBuildings(String engineerManager);

    List<GroupModel> getUnits(GroupModel groupModel);
    /**
     *
     * 获取项目
     * @return
     */
    List<GroupModel> getProjects(String engineerManager);
    /**
     *
     * 获取区域
     * @return
     */
    List<GroupModel> getAreas(String engineerManager);




}

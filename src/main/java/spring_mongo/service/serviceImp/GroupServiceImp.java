package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.GroupDao;
import spring_mongo.model.GroupModel;
import spring_mongo.service.GroupService;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/6.
 */
@Service
@Transactional
public class GroupServiceImp implements GroupService{
    @Autowired
    private GroupDao groupDao;
    @Override
    public GroupModel getByID(String id) {
        return groupDao.getByID(id);
    }

    @Override
    public List<GroupModel> getBuildings(String engineerManager) {
        return groupDao.getBuildings(engineerManager);
    }

    @Override
    public List<GroupModel> getProjects(String engineerManager) {
        return groupDao.getProjects(engineerManager);
    }

    @Override
    public List<GroupModel> getAreas(String engineerManager) {
        return groupDao.getAreas(engineerManager);
    }

    @Override
    public List<GroupModel> getUnits(GroupModel groupModel) {
        return groupDao.getUnits(groupModel);
    }


}

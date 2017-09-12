package spring_mongo.service;

import spring_mongo.model.GroupModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/6.
 */
public interface GroupService {

    public GroupModel getByID(String id);

    List<GroupModel> getBuildings(String engineerManager);

    List<GroupModel> getProjects(String engineerManager);

    List<GroupModel> getAreas(String engineerManager);

    List<GroupModel> getUnits(GroupModel groupModel);



}

package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.GroupModel;
import spring_mongo.service.GroupService;
import spring_mongo.util.JsonResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by augadrian on 2017/7/12.
 */
@Controller
@RequestMapping(value = "/api/groupManager", produces = "application/json;charset=UTF-8")
public class GroupController {


    @Autowired
    private GroupService groupService;

    JsonResult jsonResult=new JsonResult();
    ObjectMapper mapToJson = new ObjectMapper();
    /**
     *
     * 查询项目
     * @return
     */
    @RequestMapping(value = "getProjects",method = RequestMethod.GET)
    @ResponseBody
    public String getProjects(String engineerManager)  throws JsonProcessingException {
       List typeList=new ArrayList();
        List<GroupModel> list= groupService.getProjects(engineerManager);
        if (list != null&&list.size()>0) {
            for(GroupModel groupModel:list){
                groupModel.set_id(null);
                String name= groupModel.getName();
                typeList.add(name);
            }
            jsonResult.setData(typeList);
            jsonResult.setMessage("请求成功");
            jsonResult.setStatusCode(200);
        } else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }
    /**
     *
     * 查询区域
     * @return
     */
    @RequestMapping(value = "getAreas",method = RequestMethod.GET)
    @ResponseBody
    public String getAreas(String engineerManager)  throws JsonProcessingException {
        List typeList=new ArrayList();
        List<GroupModel> list= groupService.getAreas(engineerManager);
        if (list != null&&list.size()>0) {
            for(GroupModel groupModel:list){
                groupModel.set_id(null);
                String name= groupModel.getName();
                typeList.add(name);
            }
            jsonResult.setData(typeList);
            jsonResult.setMessage("请求成功");
            jsonResult.setStatusCode(200);
        }else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }
    /**
     *
     * 查询楼宇单元
     * @return
     */
    @RequestMapping(value = "getBuildings",method = RequestMethod.GET)
    @ResponseBody
    public String getBuildings(String engineerManager)  throws JsonProcessingException {
        Map<String,Object> map=new HashMap<String, Object>();

        List<GroupModel> list= groupService.getBuildings(engineerManager);
        if (list != null&&list.size()>0) {
            for(GroupModel groupModel:list){
                groupModel.setObjectID(groupModel.get_id().toString());
               groupModel.set_id(null);
            }
            map.put("list",list);
            map.put("StatusCode",200);
            map.put("message","请求成功");
        }else {
            map.put("StatusCode",500);
            map.put("message","请求成功");
        }

        return mapToJson.writeValueAsString(map);
    }
    @RequestMapping(value = "getUnits",method = RequestMethod.GET)
    @ResponseBody
    public String getUnit(GroupModel groupModel)  throws JsonProcessingException {
        Map<String,Object> map=new HashMap<String, Object>();
        List typeList=new ArrayList();
        List<GroupModel> list= groupService.getUnits(groupModel);
        if (list != null&&list.size()>0) {
            for(GroupModel groupModel1:list){
                groupModel1.set_id(null);
                String name= groupModel1.getName();
                typeList.add(name);
            }
            map.put("StatusCode",200);
            map.put("message","请求成功");
            map.put("list",typeList);

        }else {
            map.put("StatusCode",500);
            map.put("message","请求成功");
        }

        return mapToJson.writeValueAsString(map);
    }
}

package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.CategoriesModel;
import spring_mongo.service.CategoriesService;
import spring_mongo.util.JsonResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
@Controller
@RequestMapping(value = "/api/categoriesManager", produces = "application/json;charset=UTF-8")
public class CategoriesController {
   JsonResult jsonResult=new JsonResult();
    ObjectMapper mapToJson = new ObjectMapper();

    @Autowired
    private CategoriesService categoriesService;
    /**
     *
     * 查询一级维修类型
     * @return
     */
    @RequestMapping(value = "getFixTtypeGroup",method = RequestMethod.GET)
    @ResponseBody
    public String getFixTtypeGroup(String engineerManager)  throws JsonProcessingException {
        List<CategoriesModel> categoriesModelList= categoriesService.getFixTtypeGroup(engineerManager);
        if(categoriesModelList!=null&&categoriesModelList.size()>0){
            for(CategoriesModel categoriesModel:categoriesModelList){
               categoriesModel.setObjectID(categoriesModel.get_id().toString());
               categoriesModel.set_id(null);
                jsonResult.setData(categoriesModelList);
                jsonResult.setMessage("请求成功");
                jsonResult.setStatusCode(200);
            }
        } else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     *
     * 查询二级维修类型
     * @return
     */
    @RequestMapping(value = "getFixTtype",method = RequestMethod.GET)
    @ResponseBody
    public String getFixTtype(String objectID)  throws JsonProcessingException {
        List<CategoriesModel> categoriesModelList= categoriesService.getFixTtype(objectID);
        if(categoriesModelList!=null&&categoriesModelList.size()>0){
            for(CategoriesModel categoriesModel:categoriesModelList){
                categoriesModel.setObjectID(categoriesModel.get_id().toString());
                categoriesModel.set_id(null);
                jsonResult.setData(categoriesModelList);
                jsonResult.setMessage("请求成功");
                jsonResult.setStatusCode(200);
            }
        } else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }
}

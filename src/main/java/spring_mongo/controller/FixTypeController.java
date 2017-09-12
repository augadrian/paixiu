package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.FixTypeModel;
import spring_mongo.service.FixTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guxiaowei on 2017/7/19.
 */
@Controller
@RequestMapping(value = "/api/fixTypeManager", produces = "application/json;charset=UTF-8")
public class FixTypeController {
    @Autowired
    private FixTypeService fixTypeService;
    ObjectMapper mapToJson = new ObjectMapper();

    /**
     * 根据二级维修类型查找三级维修类型列表
     * @return
     */
    @RequestMapping(value = {"/getListByParent"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByParent(String parent) throws JsonProcessingException {
        Map<String,Object> jsonResult = new HashMap<String, Object>();
        List<FixTypeModel> fixTypeModels = fixTypeService.getListByParent(parent);
        if (fixTypeModels!=null){
            jsonResult.put("statusCode",200);
            jsonResult.put("list",fixTypeModels);
            jsonResult.put("message","请求成功");
        }else {
            jsonResult.put("statusCode",500);
            jsonResult.put("message","请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }
}

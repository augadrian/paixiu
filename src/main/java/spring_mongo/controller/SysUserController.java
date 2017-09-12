package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.SysUserModel;
import spring_mongo.service.SysUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guxiaowei on 2017/8/30.
 */
@Controller
@RequestMapping(value = "/api/sysUserManager", produces = "application/json;charset=UTF-8")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    ObjectMapper mapToJson = new ObjectMapper();
    /**
     * @return
     */
    @RequestMapping(value = {"/getUserByID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getUserByID(String userID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try{
            SysUserModel sysUserModel = sysUserService.getByID(userID);
            jsonResult.put("list", sysUserModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        }catch (Exception e){
            e.printStackTrace();
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }
}

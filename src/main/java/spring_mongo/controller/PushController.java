package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.PushModel;
import spring_mongo.service.PushService;
import net.sf.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/10.
 */
@Controller
@RequestMapping(value = "/api/pushManager", produces = "application/json;charset=UTF-8")
public class PushController {
    @Autowired
    private PushService pushService;
    @Autowired
    private HttpServletRequest request;
    ObjectMapper mapToJson = new ObjectMapper();

    /**
     * 根据用户id 得到当前推送消息的设置
     *
     * @return
     */

    @RequestMapping(value = {"/getPushByUser"}, method = RequestMethod.GET)
    @ResponseBody
    public String getPushByUser(String userId) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<PushModel> pushModelList = pushService.findPushByUserId(userId);
        if (pushModelList != null) {
            if (pushModelList.size() != 0) {
                jsonResult.put("list", pushModelList);
            } else {
                PushModel pushModel = new PushModel(userId, "1", "1", "1", "1", "1");
                List<PushModel> pushModelList2 = new ArrayList<>();
                pushModelList2.add(pushModel);
                jsonResult.put("list", pushModelList2);
                //新增设置
                pushService.insertPush(pushModel);
            }
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据用户id  修改  推送消息的设置
     *
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/updatePushByUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String updatePushByUser(String jsonString) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        System.out.println("开始");
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        System.out.println("========="+jsonObject);
        PushModel pushModel = pushService.getByUserId(jsonObject.get("user").toString());

        if (pushModel != null) { //存在记录
            //更新设置
            pushService.updatePush(pushModel.getUser(), "bad", jsonObject.get("bad").toString());
            pushService.updatePush(pushModel.getUser(), "examine", jsonObject.get("examine").toString());
            pushService.updatePush(pushModel.getUser(), "order", jsonObject.get("order").toString());
            pushService.updatePush(pushModel.getUser(), "overtime", jsonObject.get("overtime").toString());
            pushService.updatePush(pushModel.getUser(), "warn", jsonObject.get("warn").toString());
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }




}

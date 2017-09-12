package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.model.NoticeModel;
import spring_mongo.service.NoticeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/13.
 */
@Controller
@RequestMapping(value = "/api/noticeManager", produces = "application/json;charset=UTF-8")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    ObjectMapper mapToJson = new ObjectMapper();

    /**
     * 通过用户 得到消息列表
     *
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/getNoticeByUser"}, method = RequestMethod.GET)
    @ResponseBody
    public String getNoticeByUser(String userId, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {

            List<NoticeModel> noticeModelList = noticeService.findNoticeByUserId(userId, pageNo, pageSize);
            Integer itemCount = noticeService.findNoticeByUserId(userId).size();
            if (noticeModelList != null) {
                jsonResult.put("message", "请求成功");
                jsonResult.put("list", noticeModelList);
                jsonResult.put("statusCode", 200);

                jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
                jsonResult.put("itemCount", itemCount);
                jsonResult.put("pageNo", pageNo);
            } else {
                jsonResult.put("statusCode", 500);
                jsonResult.put("message", "请求失败");
            }
        }catch (Exception e){
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }



        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 插入消息
     *
     * @param noticeModel
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/insertNotice"}, method = RequestMethod.POST)
    @ResponseBody
    public String insertNotice(NoticeModel noticeModel) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            noticeService.insert(noticeModel);
        } catch (Exception e) {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
            return mapToJson.writeValueAsString(jsonResult);
        }
        jsonResult.put("message", "请求成功");
        jsonResult.put("statusCode", 200);
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 更新 消息的 已读未读状态
     * 0--未读   1--已读
     * @param noticeId
     * @param state
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/updateNotice"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateNotice(String noticeId, String state) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        if(!"0".equals(state)&&!"1".equals(state)){
            jsonResult.put("statusCode", 400);
            jsonResult.put("message", "参数有误 state ：0 / 1");
            return mapToJson.writeValueAsString(jsonResult);
        }
        if("0".equals(state)){
            jsonResult.put("statusCode", 400);
            jsonResult.put("message", "只能未读改已读");
            return mapToJson.writeValueAsString(jsonResult);
        }
        try {
            noticeService.updateState(noticeId, state);
        } catch (Exception e) {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
            return mapToJson.writeValueAsString(jsonResult);
        }
        jsonResult.put("message", "请求成功");
        jsonResult.put("statusCode", 200);
        return mapToJson.writeValueAsString(jsonResult);
    }
}

package spring_mongo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import spring_mongo.dao.TicketDao;
import spring_mongo.model.*;
import spring_mongo.service.*;
import spring_mongo.model.*;
import spring_mongo.service.TicketService;
import spring_mongo.service.UserService;
import spring_mongo.util.AppPush;
import spring_mongo.util.AppPushUtils;
import spring_mongo.util.JsonResult;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by guxiaowei on 2017/7/3.
 */
@Controller
@RequestMapping(value = "/api/ticketManager", produces = "application/json;charset=UTF-8")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;
    @Autowired
    private TicketDao ticketDao;
    @Autowired
    private PushService pushService;


    ObjectMapper mapToJson = new ObjectMapper();
    JsonResult jsonResult = new JsonResult();

    /**
     * 根据工单状态查询（抢单列表）
     * StanlyGK
     *
     * @param status
     * @return
     */
    @RequestMapping(value = {"/getListByStatus"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByStatus(String engineer, String status, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            //EngineerModel engineerModel = ticketService.getEngineerByID(engineer);
            //List<String> area = engineerModel.getArea();
            //List<String> fixType = engineerModel.getFix_type();
            List<TicketListModel> ticketModelList = ticketService.findForRequery(engineer, status, pageNo, pageSize);
//            List<TicketListModel> ticketResultList = new ArrayList<TicketListModel>();
//            for (TicketListModel ticketListModel : ticketModelList) {
//                for (int i = 0; i < fixType.size(); i++) {
//                    if (ticketListModel.getFix_type().equals(fixType.get(i))) {
//                        ticketResultList.add(ticketListModel);
//                    }
//                }
//            }
            Integer itemCount = ticketService.findForRequery(engineer, status, 0, 0).size();
            jsonResult.put("list", ticketModelList);
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 查看工单详情
     * StanlyGK
     *
     * @param id
     * @return
     */
    @RequestMapping(value = {"/findByObjectId"})
    @ResponseBody
    public String findByObjectId(String id) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        TicketModel ticketModel = ticketService.findDocumentById(id);
        String data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime());
        System.out.println(data);
        if (ticketModel != null) {
            jsonResult.put("data", ticketModel);
            jsonResult.put("message", "查询成功");
            jsonResult.put("status", 200);
        } else {
            jsonResult.put("status", 500);
            jsonResult.put("message", "查询失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 管理者app端催单，并推送
     * StanlyGK
     *
     * @param ticketID
     * @param managerID
     * @return
     */
    @RequestMapping(value = {"/updateTicketByReminder"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateTicketByReminder(String ticketID, String managerID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        HistoryModel historyModel = new HistoryModel("reminder", "TS05", managerID, new Date());
        TicketModel ticketModel = ticketService.updateTicketByReminder(ticketID, historyModel);
        ticketModel.set_id(null);
        if (ticketModel != null) {
            if (pushToOne(ticketModel.getMain_engineer(), "催111单", "催单催单催单", "催单", 5)) {
                jsonResult.put("list", ticketModel);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            } else {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            }
        } else {
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 管理者app端取消工单
     * StanlyGK
     *
     * @param ticketID
     * @param managerID
     * @return
     */
    @RequestMapping(value = {"/updateTicketByCancel"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateTicketByCancel(String ticketID, String managerID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try{
            HistoryModel historyModel = new HistoryModel("cancel", "TS20", managerID, new Date());
            TicketModel ticketModel = ticketService.updateTicketByReminder(ticketID, historyModel);
            ticketModel.set_id(null);
            jsonResult.put("list", ticketModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        }catch (Exception e){
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 管理者app端改派遣
     * StanlyGK
     *
     * @param ticketID
     * @param managerID
     * @return
     */
    @RequestMapping(value = {"/updateTicketByReverse"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateTicketByReverse(String ticketID, String managerID,String viceManagerID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try{
            HistoryModel historyModel = new HistoryModel();
            TicketModel ticketModel = new TicketModel();
            if (viceManagerID.equals("") || viceManagerID==null){
                historyModel = new HistoryModel("assignTo", "TS03", managerID, new Date());
                ticketModel = ticketService.updateTicketByReverse(ticketID, historyModel);
            }else {
                TicketModel ticketModelAdd = ticketService.getByID(ticketID);
                ticketModelAdd.setMainNo(ticketModelAdd.getNo());
                ticketModelAdd.setMainEngineer(ticketModelAdd.getMain_engineer());
                String[] viceManager = viceManagerID.split(",");
                List<String> viceList = new ArrayList<String>();
                Collections.addAll(viceList, viceManager);
                List<String> vice_id = new ArrayList<String>();
                List<String> vice_no = new ArrayList<String>();
                for (int i=0;i<viceList.size();i++){
                    ticketModelAdd.setMain_engineer(viceList.get(i));
                    ticketModelAdd.setVice_engineer(new ArrayList<String>());
                    ticketModelAdd.setNo(createCode());
                    TicketModel ticketModelResult = ticketService.cooperateToAdd(ticketModelAdd);
                    vice_no.add(ticketModelAdd.getNo());
                    vice_id.add(ticketModelResult.get_id().toString());
                }
                historyModel = new HistoryModel("cooperate", "TS03", managerID, new Date());
                ticketModel = ticketService.updateTicketByCooperate(ticketID, historyModel,viceList,vice_no,vice_id);
            }
            ticketModel.set_id(null);
            jsonResult.put("list", ticketModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        }catch (Exception e){
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 抢单成功接口
     * StanlyGK
     *
     * @param id
     * @param engineer
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/updateTicket"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateTicket(String id, String engineer) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        TicketModel ticketModelCheck = ticketService.getByID(id);
        List<TicketModel> ticketModelList = ticketService.getListByEngineer(engineer);
        if (ticketModelList != null) {
            jsonResult.put("message", "你有工单未完成！");
            jsonResult.put("statusCode", 400);
        } else if (ticketModelCheck.getStatus().equals("TS04")) {
            jsonResult.put("message", "工单已被抢走！");
            jsonResult.put("statusCode", 300);
        } else {
            TicketModel ticketModel = ticketService.updateTicket(id, engineer);
            if (ticketModel != null) {
                jsonResult.put("data", ticketModel);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            } else {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            }
        }

        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 根据工程师查询
     * StanlyGK
     *
     * @param engineer
     * @return
     */
    @RequestMapping(value = {"/getListByEngineer"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByEngineer(String engineer) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.getListByEngineer(engineer);
        if (ticketModelList != null) {
            jsonResult.put("list", ticketModelList);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据工程主管查询相关项目的工单
     * StanlyGK
     *
     * @param engineerManager
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = {"/getListByManagerID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByManagerID(String engineerManager, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketModelList = ticketService.getListByManagerID(engineerManager, pageNo, pageSize);
        Integer itemCount = ticketService.getListByManagerID(engineerManager, 0, 0).size();
        if (ticketModelList != null) {
            jsonResult.put("list", ticketModelList);
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 返回流水账号yyyyMMdd+5位数字不重复
     *
     * @return
     */
    public String createCode() {
        String date = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        int ran = (int) (Math.random() * 90000) + 10000;
        String orderCode = date + ran;
        return orderCode;
    }

    /**
     * 添加一张补单
     *
     * @return
     */
    @RequestMapping(value = {"/addSingleSupplementModel"}, method = RequestMethod.POST)
    @ResponseBody
    public String addSingleSupplementModel(String ticketObj) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            SingleSupplementModel singleSupplementModel = mapToJson.readValue(ticketObj, SingleSupplementModel.class);
            if (singleSupplementModel.getProject() == null ||
                    singleSupplementModel.getProject() == "" ||
                    singleSupplementModel.getType() == null ||
                    singleSupplementModel.getType() == "" ||
                    singleSupplementModel.getMain_engineer() == null ||
                    singleSupplementModel.getMain_engineer() == "" ||
                    singleSupplementModel.getArea() == null ||
                    singleSupplementModel.getArea() == "" ||
                    singleSupplementModel.getUnit() == null ||
                    singleSupplementModel.getUnit() == "" ||
                    singleSupplementModel.getFix_type_group() == null ||
                    singleSupplementModel.getFix_type_group() == "" ||
                    singleSupplementModel.getFix_type() == null ||
                    singleSupplementModel.getFix_type() == "" ||
                    singleSupplementModel.getFix_type3() == null ||
                    singleSupplementModel.getVerificateTimeDetail() == null ||
                    singleSupplementModel.getFix_type3_num() == null) {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            } else {
                singleSupplementModel.setNo(createCode());
                ticketService.saveSingleSupplement(singleSupplementModel);
                TicketModel ticketModel = ticketService.getTicketByNo(singleSupplementModel.getNo());
                if (singleSupplementModel.getName() != null && singleSupplementModel.getName() != ""
                        && singleSupplementModel.getCount() != null && singleSupplementModel.getCount() != "") {
                    String[] name = singleSupplementModel.getName().split(",");
                    String[] count = singleSupplementModel.getCount().split(",");
                    for (int i = 0; i < name.length; i++) {
                        PartModel partModel = new PartModel(name[i], Integer.parseInt(count[i]));
                        ticketModel = ticketService.updateTicketByPart(ticketModel.get_id().toString(), partModel);
                    }
                }
                ticketModel.setObjectID(ticketModel.get_id().toString());
                ticketModel.set_id(null);


                jsonResult.put("data", ticketModel);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);

            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 通过工单号查询工单
     *
     * @return
     */
    @RequestMapping(value = {"/getTicketByNo"}, method = RequestMethod.GET)
    @ResponseBody
    public String getTicketByNo(String ticketNo) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            TicketListModel ticketModel = ticketService.getTicketByTicketNo(ticketNo);
            jsonResult.put("data", ticketModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 补单列表
     *
     * @return
     */
    @RequestMapping(value = {"/getListByFrom"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByFrom(String engineer, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            List<TicketModel> ticketModelList = ticketService.getListByFrom(engineer, pageNo, pageSize);
            Integer itemCount = ticketService.getListByFrom(engineer, 0, 0).size();
            jsonResult.put("list", ticketModelList);
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }
        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 查询父类物料
     *
     * @return
     */
    @RequestMapping(value = {"/getMaterielParent"}, method = RequestMethod.GET)
    @ResponseBody
    public String getMaterielParent() throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            List<MaterielModel> materielModelList = ticketDao.getMaterielParent();
            jsonResult.put("statusCode", 200);
            jsonResult.put("message", "请求成功");
            jsonResult.put("list", materielModelList);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 查询子类物料
     *
     * @return
     */
    @RequestMapping(value = {"/getMaterielListByParentID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getMaterielListByParentID(String parentID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            List<MaterielModel> materielModelList = ticketDao.getMaterielListByParentID(parentID);
            jsonResult.put("statusCode", 200);
            jsonResult.put("message", "请求成功");
            jsonResult.put("list", materielModelList);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据工单号查询服务细则
     *
     * @return
     */
    @RequestMapping(value = {"/getServiceListByTicketID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getServiceListByTicketID(String ticketID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        try {
            TicketServiceModel ticketServiceModel = ticketService.getServiceListByTicketID(ticketID);
            jsonResult.put("data", ticketServiceModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据主键获取工程师
     * StanlyGK
     *
     * @param engineer
     * @return
     */
    @RequestMapping(value = {"/getEngineerByID"}, method = RequestMethod.POST)
    @ResponseBody
    public String getEngineerByID(String engineer) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        EngineerModel engineerModel = ticketService.getEngineerByID(engineer);
        List<String> area = engineerModel.getArea();
        System.out.println(area.toString());
        if (engineerModel != null) {
            jsonResult.put("data", engineerModel.getFix_type().toString());
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据待派遣工单搜索相关工程师
     * StanlyGK
     *
     * @param ticketID
     * @return
     */
    @RequestMapping(value = {"/getEngineerByTicketID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getEngineerByTicketID(String ticketID) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<EngineerModel> engineerModelList = ticketService.getEngineerByTicketID(ticketID);
        Integer itemCount = engineerModelList.size();
        if (engineerModelList != null) {
            jsonResult.put("list", engineerModelList);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据工程主管查询其管辖区域内的所有工程师
     * StanlyGK
     *
     * @param engineerManager
     * @return
     */
    @RequestMapping(value = {"/getEngineerByManagerID"}, method = RequestMethod.GET)
    @ResponseBody
    public String getEngineerByManagerID(String engineerManager) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<EngineerModel> engineerModelList = ticketService.getEngineerByManagerID(engineerManager);
        for (EngineerModel engineerModel : engineerModelList) {
            engineerModel.set_id(null);
        }
        Integer itemCount = engineerModelList.size();
        if (engineerModelList != null) {
            jsonResult.put("list", engineerModelList);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 抢单时 的 消息推送
     * 步骤一：根据工单id查询 得到各参数
     * 步骤二：通过各参数 查询所有工程师
     * 步骤三：开始推送  修改工单的推送状态
     *
     * @param ticketId  工单id 用于筛选
     * @param title     标题
     * @param titleText 标题内容
     * @param transText 穿透内容
     * @param time      有效时长
     */
//    @RequestMapping(value = {"/pushByTicketId"})
//    @ResponseBody
    public Boolean pushByTicketId(String ticketId, String title, String titleText, String transText, int time) throws JsonProcessingException {
        try {
            List<TicketModel> ticketModelList = ticketService.findTicketById(ticketId);
            TicketModel ticketModel = ticketModelList.get(0);  //得到需要查询的工单
            List<UserListModel> userlist = ticketService.findUserByTicket(ticketModel.getProject(), ticketModel.getArea(), "staff");//根据Project+area+staff筛选用户
            Iterator<UserListModel> iterator = userlist.iterator();
            List<String> getUserList = new ArrayList();
            while (iterator.hasNext()) {
                UserListModel userListModel = iterator.next();
                List fix_type_group = userListModel.getFix_type_group();  //维修类型
                List fix_type = userListModel.getFix_type();  //维修类型 详情
                if (fix_type_group.contains(ticketModel.getFix_type_group()) && fix_type.contains(ticketModel.getFix_type())) {
                    getUserList.add(userListModel.get_id().toString());//添加
                }
            }
            System.out.println("得到的人员列表 +" + getUserList);
            //开始推送
            AppPushUtils.sendGeTui(title, titleText, transText, getUserList, time);
            ticketService.updateTicketByKey(ticketId, "pushState", "1");//修改为  0--未推送 1--已推送
            //更新工单的推送状态

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean pushByTicketId2(String ticketId, String title, String titleText, String transText, int time) throws JsonProcessingException {
        try {
            List<TicketModel> ticketModelList = ticketService.findTicketById(ticketId);
            TicketModel ticketModel = ticketModelList.get(0);  //得到需要查询的工单
            List<UserListModel> userlist = ticketService.findUserByTicket(ticketModel.getProject(), ticketModel.getArea(), "staff");//根据Project+area+staff筛选用户
            Iterator<UserListModel> iterator = userlist.iterator();
            List<String> getUserList = new ArrayList();
            while (iterator.hasNext()) {
                UserListModel userListModel = iterator.next();
                List fix_type_group = userListModel.getFix_type_group();  //维修类型
                List fix_type = userListModel.getFix_type();  //维修类型 详情
                if (fix_type_group.contains(ticketModel.getFix_type_group()) && fix_type.contains(ticketModel.getFix_type())) {
                    getUserList.add(userListModel.get_id().toString());//添加
                }
            }
            System.out.println("得到的人员列表 +" + getUserList);
            //开始推送
            AppPushUtils.sendGeTui(title, titleText, transText, getUserList, time);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 工单 类型推送
     *
     * @param ticketId  工单id
     * @param userId    用户id
     * @param type      类型
     * @param title     标题
     * @param titleText 标题内容
     * @param transText 穿透内容
     * @param time      有效时间
     * @return
     * @throws JsonProcessingException
     */
    public Boolean pushNotice(String ticketId, String userId, String type, String title, String titleText, String transText, int time) throws JsonProcessingException {
        try {
            //判断该用户消息类型推送是否开启

            List<PushModel> pushModelList = pushService.findPushByUserId(userId);
            if (pushModelList != null) { //表示有记录
                PushModel pushModel = pushModelList.get(0);
                switch (type) {
                    case "bad": {
                        if ("0".equals(pushModel.getBad())) {//关闭
                            return false;
                        }
                        break;
                    }
                    case "examine": {
                        if ("0".equals(pushModel.getExamine())) {//关闭
                            return false;
                        }
                        break;
                    }
                    case "order": {
                        if ("0".equals(pushModel.getOrder())) {//关闭
                            return false;
                        }
                        break;
                    }
                    case "overtime": {
                        if ("0".equals(pushModel.getOvertime())) {//关闭
                            return false;
                        }
                        break;
                    }
                    case "warn": {
                        if ("0".equals(pushModel.getWarn())) {//关闭
                            return false;
                        }
                        break;
                    }
                }
            }
            List<String> cids = new ArrayList<>();
            cids.add(userId);
            //开始推送
            AppPushUtils.sendGeTui(title, titleText, transText, cids, time);
            //插入消息记录
            NoticeModel noticeModel = new NoticeModel();
            noticeModel.setUserId(userId);
            noticeModel.setIco("ico");
            noticeModel.setType(type);
            noticeModel.setTicketId(ticketId);
            TicketModel ticket = ticketService.findDocumentById(ticketId);
            noticeModel.setTicketNo(ticket.getNo());
            StringBuilder address = new StringBuilder();
            address.append(ticketDao.getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? ticketDao.getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? ticketDao.getGroupByID(ticket.getArea()).getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? ticketDao.getGroupByID(ticket.getUnit()).getName() : "" + "—")
                    .append(ticket.getRoom() != null ? ticket.getProject() : "");
            noticeModel.setAddress(address.toString());
            noticeModel.setState("0");  //0--未读
            noticeModel.setEngineerId(ticket.getMain_engineer());
            UserModel userModel = userService.getUserById(ticket.getMain_engineer());
            noticeModel.setEngineerName(userModel.getName());
            //添加消息列表
            noticeService.insert(noticeModel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 催单 推送
     *
     * @param userId
     * @param title
     * @param titleText
     * @param transText
     * @param time
     * @return
     * @throws JsonProcessingException
     */
    public Boolean pushToOne(String userId, String title, String titleText, String transText, int time) throws JsonProcessingException {
        try {
            List<String> cids = new ArrayList<>();
            cids.add(userId);
            //开始推送
            AppPushUtils.sendGeTui(title, titleText, transText, cids, time);

        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 根据客户姓名或手机号码搜索工单
     */
    @RequestMapping(value = {"/getListByNameOrPhone"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByNameOrPhone(String engineer, String nameOrPhone, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketModelList = ticketService.getListByNameOrPhone(engineer, nameOrPhone, pageNo, pageSize);
//        List<TicketModel> resultList = new ArrayList<TicketModel>();
//
//        for (TicketModel ticketModel : ticketModelList) {
//            ticketModel.set_id(null);
//            resultList.add(ticketModel);
//        }

        Integer itemCount = ticketService.getListByNameOrPhone(engineer, nameOrPhone, 0, 0).size();
        if (ticketModelList != null) {
            jsonResult.put("list", ticketModelList);
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据客户姓名或手机号码搜索工单
     */
    @RequestMapping(value = {"/getListByEngineerAndNameOrPhone"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByEngineerAndNameOrPhone(String engineer, String nameOrPhone, String enterId, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketModelList = ticketService.getListByEngineerAndNameOrPhone(engineer, nameOrPhone, enterId, pageNo, pageSize);
        Integer itemCount = ticketService.getListByNameOrPhone(engineer, nameOrPhone, 0, 0).size();
        if (ticketModelList != null) {
            jsonResult.put("list", ticketModelList);
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 通过id 得到 工单的审核状态（ 0-待审核 1-已通过 2-未通过）（为空默认已通过）
     *
     * @param ticketId
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/getTicketCheckState"}, method = RequestMethod.GET)
    @ResponseBody
    public String getTicketCheck(String ticketId) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.findTicketById(ticketId);
        TicketModel ticketModel = ticketModelList.get(0);  //得到查询的工单
        ticketModel.set_id(null);
        if (ticketModel != null) {
//            System.out.println("不为空");
            jsonResult.put("check", ticketModel.getCheckState() != null ? ticketModel.getCheckState() : "1"); //若为空 则为 已通过
            jsonResult.put("ticket", ticketModel);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 通过 id  更新工单的审核状态
     *
     * @param ticketId
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/updateTicketCheck"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateTicketCheck(String ticketId, String checkState) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.findTicketById(ticketId);
        TicketModel ticketModel = ticketModelList.get(0);  //得到查询的工单
        if (ticketModel != null) {
            //存在  开始更新
            TicketModel ticketModel1 = ticketService.updateCheckState(ticketId, checkState);
            ticketModel1.set_id(null);
            jsonResult.put("list", ticketModel1);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 全部工单筛选  根据工单属性
     * Integer pageNo, Integer pageSize
     */
    @RequestMapping(value = {"/getListFind"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListFind(TicketModel ticketListModel, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketModelList = ticketService.findForFind(ticketListModel);
        System.out.println("筛选 size" + ticketModelList.size());
//        List<TicketListModel> ticketModelListNew = new ArrayList<>();
//        if (ticketListModel.getAbnormal() != null && ticketListModel.getAbnormal().split(",")[0].equals("0")) {//超时接单
//            for (int i = 0; i < ticketModelList.size(); i++) {
//                TicketListModel ticketModel = ticketModelList.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if ("TS03".equals(ticketModel.getStatus()) && dateAddMinutes(ticketModel.getAssign_time(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
//                    //yi超时
//                    ticketModelListNew.add(ticketModel);
////                    System.out.println("移除");
////                    ticketModelList.remove(i);
////                    i--;
//                }
//            }
//        }
//        if (ticketListModel.getAbnormal() != null && ticketListModel.getAbnormal().split(",")[1].equals("0")) {//超时上门
////            List<TicketModel> list = ticketService.findTimeOutTickets(ticketModelList);
//            for (int i = 0; i < ticketModelList.size(); i++) {
//                TicketListModel ticketModel = ticketModelList.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if ("TS04".equals(ticketModel.getStatus()) && dateAddMinutes(ticketModel.getAccept_time(), 8 * 60 + Integer.valueOf(groupModel.getResponse_time())).after(new Date())) {
//                    //未超时
//                    ticketModelListNew.add(ticketModel);
////                    System.out.println("移除");
////                    ticketModelList.remove(i);
////                    i--;
//                }
//            }
//        }
//        if (ticketListModel.getAbnormal() != null && ("0".equals(ticketListModel.getAbnormal().split(",")[0]) || "0".equals(ticketListModel.getAbnormal().split(",")[1]))) {
//            ticketModelList = ticketModelListNew;
//        }

        List<TicketListModel> result = new ArrayList<TicketListModel>();
        Integer itemCount = ticketModelList.size(); //总数
//        System.out.println("总数" + itemCount);
        int pageCount = 0;  //总页数
        if (ticketModelList != null && itemCount > 0) {
            pageCount = (itemCount + pageSize - 1) / pageSize;//总页数
//            if (pageNo >= pageCount) {
//                pageNo = pageCount;
//            }
            int start = (pageNo - 1) * pageSize;
            int end = pageNo * pageSize;
            if (end >= itemCount) {
                end = itemCount;
            }
            for (int i = start; i < end; i++) {
                result.add(ticketModelList.get(i));
            }
        }
//        System.out.println("result的数量" + result.size());
// address拼接
        Iterator<TicketListModel> iterator = result.iterator();
        while (iterator.hasNext()) {
            TicketListModel ticketModel = iterator.next();
            StringBuilder address = new StringBuilder();
            address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                    .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
            ticketModel.setAddress(address.toString());
        }
        if (ticketModelList != null) {
            for (TicketListModel ticketModel : ticketModelList) {
                ticketModel.setObjectID(ticketModel.get_id().toString());
                ticketModel.set_id(null);
                ticketModel.setCreateTime(ticketModel.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime()) : "");
            }
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
            jsonResult.put("list", result);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 工单录入的筛选  根据工单属性
     * Integer pageNo, Integer pageSize
     */
    @RequestMapping(value = {"/getListLu"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListLu(TicketModel ticketListModel, Integer pageNo, Integer pageSize, String userId) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketModelList = ticketService.findForLu(ticketListModel, pageNo, pageSize);
//        List<TicketListModel> ticketModelListNew = new ArrayList<>();
//        if (ticketListModel.getAbnormal() != null && ticketListModel.getAbnormal().split(",")[0].equals("0")) {//超时接单
//            for (int i = 0; i < ticketModelList.size(); i++) {
//                TicketListModel ticketModel = ticketModelList.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if ("TS03".equals(ticketModel.getStatus()) && dateAddMinutes(ticketModel.getAssign_time(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
//                    ticketModelListNew.add(ticketModel);
//                    //                    System.out.println("移除");
////                    ticketModelList.remove(i);
////                    i--;
//                }
//            }
//        }
//        if (ticketListModel.getAbnormal() != null && ticketListModel.getAbnormal().split(",")[1].equals("0")) {//超时上门
////            List<TicketModel> list = ticketService.findTimeOutTickets(ticketModelList);
//            for (int i = 0; i < ticketModelList.size(); i++) {
//                TicketListModel ticketModel = ticketModelList.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if ("TS04".equals(ticketModel.getStatus()) && dateAddMinutes(ticketModel.getAccept_time(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
//                    //未超时
//                    ticketModelList.remove(i);
//                    i--;
//                }
//            }
//
//        }
//
//        if (ticketListModel.getAbnormal() != null && ("0".equals(ticketListModel.getAbnormal().split(",")[0]) || "0".equals(ticketListModel.getAbnormal().split(",")[1]))) {
//            ticketModelList = ticketModelListNew;
//        }

        List<TicketListModel> result = new ArrayList<TicketListModel>();
        Integer itemCount = ticketModelList.size(); //总数
//        System.out.println("总数" + itemCount);
        int pageCount = 0;  //总页数
        if (ticketModelList != null && itemCount > 0) {
            pageCount = (itemCount + pageSize - 1) / pageSize;//总页数
            if (pageNo >= pageCount) {
                pageNo = pageCount;
            }
            int start = (pageNo - 1) * pageSize;
            int end = pageNo * pageSize;
            if (end >= itemCount) {
                end = itemCount;
            }
            for (int i = start; i < end; i++) {
                result.add(ticketModelList.get(i));
            }
        }
//        System.out.println("result的数量" + result.size());
// address拼接
        Iterator<TicketListModel> iterator = result.iterator();
        while (iterator.hasNext()) {
            TicketListModel ticketModel = iterator.next();
            StringBuilder address = new StringBuilder();
            address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                    .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
            ticketModel.setAddress(address.toString());
        }
        if (ticketModelList != null) {
            for (TicketListModel ticketModel : ticketModelList) {
                ticketModel.setObjectID(ticketModel.get_id().toString());
                ticketModel.set_id(null);
                ticketModel.setCreateTime(ticketModel.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime()) : "");

            }
            jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("pageNo", pageNo);
            jsonResult.put("statusCode", 200);
            jsonResult.put("message", "请求成功");
            jsonResult.put("list", result);

        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 工时审核筛选
     * Integer pageNo, Integer pageSize
     */
    @RequestMapping(value = {"/ScanneTicket"}, method = RequestMethod.GET)
    @ResponseBody
    public String ScanneTicket(TicketModel ticketListModel, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.ScanneTicket(ticketListModel, pageNo, pageSize);
        Integer itemCount = ticketService.ScanneTicket(ticketListModel, 0, 0).size();
        Iterator<TicketModel> iterator = ticketModelList.iterator();
        while (iterator.hasNext()) {
            TicketModel ticketModel = iterator.next();
            StringBuilder address = new StringBuilder();
            address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                    .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                    .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
            ticketModel.setAddress(address.toString());
        }
        if (ticketModelList != null) {
            for (TicketModel ticketModel : ticketModelList) {
                ticketModel.setObjectID(ticketModel.get_id().toString());
                ticketModel.set_id(null);
                ticketModel.setCreateTime(ticketModel.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime()) : "");

                jsonResult.put("list", ticketModelList);
                jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
                jsonResult.put("itemCount", itemCount);
                jsonResult.put("pageNo", pageNo);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);

    }

    /**
     * 根据手机号查询项目区域 楼宇单元
     */
    @RequestMapping(value = {"/getListByPhone"}, method = RequestMethod.GET)
    @ResponseBody
    public String getListByPhone(String phone, String engineerManager) throws JsonProcessingException {
        List<TicketModelList> ticketModelList = ticketService.findByPhone(phone, engineerManager);
        if (ticketModelList != null && ticketModelList.size() > 0) {
            for (TicketModelList ticketModelList1 : ticketModelList) {
                ticketModelList1.set_id(null);
                jsonResult.setData(ticketModelList);
                jsonResult.setMessage("手机号匹配成功");
                jsonResult.setStatusCode(200);
            }
        }/* else if (ticketModelList.size()<=1) {
            jsonResult.setData(ticketModelList);
            jsonResult.setMessage("手机号匹配失败");
            jsonResult.setStatusCode(200);

        }*/ else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
            jsonResult.setData(null);

        }

        return mapToJson.writeValueAsString(jsonResult);

    }

    /***
     *
     * 获取工时审核列表
     */
    @RequestMapping(value = "getTicketList", method = RequestMethod.GET)
    @ResponseBody
    public String getTicketList(Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketListModel> ticketListModels = ticketService.findAll(pageNo, pageSize);
        Integer itemCount = ticketService.findAll(0, 0).size();
        if (ticketListModels != null && ticketListModels.size() > 0) {
            for (TicketListModel ticketListModel : ticketListModels) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    long startTime = simpleDateFormat.parse(simpleDateFormat.format(ticketListModel.getAccept_time())).getTime();
                    long endTime = simpleDateFormat.parse(simpleDateFormat.format(ticketListModel.getFinish_time())).getTime();
                    int serviceTime = (int) (endTime - startTime) / (1000 * 60);
                    ticketListModel.setService_time(serviceTime);
                    ticketListModel.set_id(null);
                } catch (Exception e) {
                }
            }
            jsonResult.put("itemCount", itemCount);
            jsonResult.put("list", ticketListModels);
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {

            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);

        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 工单录入
     */
    @RequestMapping(value = "saveTickets", method = RequestMethod.POST)
    @ResponseBody
    public String saveTickets(TicketModel ticketModel) throws JsonProcessingException {
        if (ticketModel != null) {

            ticketModel.setNo(createCode());
            ticketModel.setCreateAt(new Date());
            ticketModel.setCheckState("0");
            ticketModel.setIs_vip(false);
            ticketModel.setStatus("TS01");
            ticketModel.setLevel("PL01");
            ticketService.saveTickets(ticketModel);
            ticketModel.setObjectID(ticketModel.get_id().toString());
            ticketModel.set_id(null);
            jsonResult.setData(ticketModel);
            jsonResult.setMessage("请求成功");
            jsonResult.setStatusCode(200);
        } else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据手机号前7位模糊查询出手机号列表
     *
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "getPhoneList", method = RequestMethod.GET)
    @ResponseBody
    public String getPhoneList(String phone, String engineerManager) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> list = new ArrayList<>();
        List<TicketModel> ticketModelList = ticketService.getPhoneList(phone, engineerManager);
        if (ticketModelList != null && ticketModelList.size() > 0) {
            for (TicketModel ticketModel : ticketModelList) {
                list.add(ticketModel.getPhone());
            }
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = list.size() - 1; j > i; j--) {
                    if (list.get(j).equals(list.get(i))) {
                        list.remove(j);
                    }
                }
            }
            map.put("list", list);
            map.put("statusCode", 200);
            map.put("message", "请求成功");
            // jsonResult.setData(list);
            // jsonResult.setStatusCode(200);
            //jsonResult.setMessage("请求成功");
        } else {
            map.put("statusCode", 500);
            map.put("message", "请求失败");
            //jsonResult.setMessage("请求失败");
            // jsonResult.setStatusCode(500);
        }
        return mapToJson.writeValueAsString(map);
    }

    /**
     * 审核工程师工时
     *
     * @param
     * @return
     */
    @RequestMapping(value = {"/updateTaskTime"}, method = RequestMethod.POST)
    @ResponseBody
    public String taskTimeAudit(String[] ticketNo, String[] checkState) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        if (ticketNo.length != checkState.length) {
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        } else {
            List<TicketModel> ticketList = null;
            try {
                ticketList = ticketService.auditTaskIime(ticketNo, checkState);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ticketList == null || ticketList.size() == 0) {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            } else {
                for (TicketModel ticketModele : ticketList) {
                    ticketModele.set_id(null);
                }
                jsonResult.put("list", ticketList);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     *
     *补单拒绝申请
     * @param no
     * @param checkState
     * @param refuseReason
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/refuseApplication"}, method = RequestMethod.POST)
    @ResponseBody
    public String refuseApplication(String no,String checkState,String refuseReason) throws JsonProcessingException {
         TicketModel ticketModel=ticketService.refuseApplication(no, checkState, refuseReason);
            Map<String, Object> jsonResult = new HashMap<String, Object>();
            if (ticketModel == null ) {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            } else {
                jsonResult.put("list", ticketModel);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        return mapToJson.writeValueAsString(jsonResult);
    }
    /**
     * 修改工程师核定工时
     *
     * @param ticketNo,checkTaskTime
     * @return
     */
    @RequestMapping(value = {"/updateCheckTaskTime"}, method = RequestMethod.POST)
    @ResponseBody
    public String editTaskTime(String ticketNo, int[] checkTaskTime, String[] fixType) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        if (checkTaskTime.length != fixType.length) {
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        } else {
            List<TicketModel> ticketModels = null;
            try {
                ticketModels = ticketService.editEngineerTaskIime(ticketNo, checkTaskTime, fixType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ticketModels == null || ticketModels.size() == 0) {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            } else {
                for (TicketModel ticketModele : ticketModels) {
                    ticketModele.set_id(null);
                }
                jsonResult.put("list", ticketModels);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        }
        return mapToJson.writeValueAsString(jsonResult);
    }
    /**
     * 修改工程师核定工时(已核定状态)
     *
     * @param ticketNo,checkTaskTime
     * @return
     */
    @RequestMapping(value = {"/updateRatifyTaskTime"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateRatifyTaskTime(String ticketNo, int[] checkTaskTime, String[] fixType) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        if (checkTaskTime.length != fixType.length) {
            jsonResult.put("message", "请求失败");
            jsonResult.put("statusCode", 500);
        } else {
            List<TicketModel> ticketModels = null;
            try {
                ticketModels = ticketService.updateRatifyTaskTime(ticketNo, checkTaskTime, fixType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ticketModels == null || ticketModels.size() == 0) {
                jsonResult.put("message", "请求失败");
                jsonResult.put("statusCode", 500);
            } else {
                for (TicketModel ticketModele : ticketModels) {
                    ticketModele.set_id(null);
                }
                jsonResult.put("list", ticketModels);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    //@PostConstruct
    public void start() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

//                dingShi();
//                logger.info("打卡记录即将生成");
//                insert1();
                if (task()) {  //抢单
                    System.out.println("抢单推送任务执行成功");
                }
                if (task10()) {  //预约单状态变更
                    System.out.println("预约单状态变更执行成功");
                } else {
//                    System.out.println("上门超时推送任务执行失败");
                }
                if (task2()) {  //预警
                    System.out.println("预警推送任务执行成功");
                } else {
//                    System.out.println("预警推送任务执行失败");
                }
                if (task3()) {  //超时
                    System.out.println("超时推送任务执行成功");
                } else {
//                    System.out.println("超时推送任务执行失败");
                }
                if (task4()) {  //差评
                    System.out.println("差评推送任务执行成功");
                } else {
//                    System.out.println("差评推送任务执行失败");
                }
                if (task5()) {  //预约
                    System.out.println("预约超时推送任务执行成功");
                } else {
//                    System.out.println("预约超时推送任务执行失败");
                }
                if (task6()) {  //补单
                    System.out.println("补单推送任务执行成功");
                } else {
//                    System.out.println("补单推送任务执行失败");
                }
                if (task7()) {  //上门预警
                    System.out.println("上门预警推送任务执行成功");
                } else {
//                    System.out.println("上门预警推送任务执行失败");
                }
                if (task8()) {  //上门超时
                    System.out.println("上门超时推送任务执行成功");
                } else {
//                    System.out.println("上门超时推送任务执行失败");
                }
                if (task9()) {  //抢单派单
                    System.out.println("抢单派单及推送任务执行成功");
                } else {
//                    System.out.println("抢单派单及推送任务执行失败");
                }


            }
        };
        timer.schedule(timerTask, 1000 * 5, 1000 * 60 * 2);
    }

    //抢单推送
    private Boolean task() {
        try {  //查询 抢单推送状态为0 的所有工单  代表 待抢单的列表
            //循环开始推送
//            System.out.println("=======抢单任务循环中=======");
            List<TicketModel> list = ticketService.findByPushState("0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    ticketModel.getMain_engineer();
                    pushByTicketId(ticketModel.get_id().toString(), "新工单：" + ticketModel.getNo(), "报修人:" + ticketModel.getCustomer(), "抢单.mp3", 5);
                    ticketService.updatePushState(ticketModel.get_id().toString(), "1");// 1--抢单已推送
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //超时预警
    private Boolean task2() {
        //        查询已派遣（TS03） 且 推送状态为2 的所有工单
//          pushState 0-抢单未推送
//                    1--抢单推送 或 不推送
//                    2--未推送
//                    3--已预警推送
//                    4--已超时推送
        try {
            List<TicketModel> list = ticketService.findByStateAndPushState("TS03", "2");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    GroupModel groupModel = groupService.getByID(ticketModel.getProject());
                    //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
                    if (dateAddMinutes(ticketModel.getCreateAt(), 8 * 60 + Integer.valueOf(groupModel.getDiff_time())).after(new Date())) {
                        //开始推送
                        pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "11", "超时预警", "快点", "超时预警.mp3", 5);
                        ticketService.updatePushState(ticketModel.get_id().toString(), "3");// 3--预警已推送
                        NoticeModel noticeModel = new NoticeModel();
                        StringBuilder address = new StringBuilder();
                        address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                                .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("warn");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //超时接单
    private Boolean task3() {
        //        查询已派遣（TS03） 且 推送状态为3 的所有工单
//          pushState 0-抢单未推送
//                    1--抢单推送 或 不推送
//                    2--未推送
//                    3--已预警推送
//                    4--已超时推送
        try {
            List<TicketModel> list = ticketService.findByStateAndPushState("TS03", "3");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    GroupModel groupModel = groupService.getByID(ticketModel.getProject());
                    //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
                    if (dateAddMinutes(ticketModel.getCreateAt(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
                        //开始推送
                        pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "22", "超时了", "超时", "超时工单.mp3", 5);
                        ticketService.updatePushState(ticketModel.get_id().toString(), "4");// 4--超时已推送

                        NoticeModel noticeModel = new NoticeModel();
                        StringBuilder address = new StringBuilder();
                        address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                                .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("overtime");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //差评单
    private Boolean task4() {
        //        查询已结单（TS09） 且 推送状态为0 的所有工单
//          modState 0-未推送
//                    1--已推送
        try {
            List<TicketModel> list = ticketService.findByStateAndModState("TS09", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    //开始推送
                    pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "bad", "差评了", "差评", "差评单.mp3", 5);
                    ticketService.updateModState(ticketModel.get_id().toString(), "1");// 1--差评已推送
                    NoticeModel noticeModel = new NoticeModel();
                    StringBuilder address = new StringBuilder();
                    address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                            .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                            .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                            .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
//                    System.out.println("地址：" + address);

                    EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
                    System.out.println(userModel);
                    if (userModel != null) {
//                        System.out.println("开始插入");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("bad");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }

                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //预约超时
    private Boolean task5() {
        // 查询已预约TS02 且 推送状态为0 的所有工单
//             appointmentState 0-未推送
//                              1--已推送
        try {
            List<TicketModel> list = ticketService.findByStateAndAppointmentState("TS21", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    if (dateAddMinutes(ticketModel.getAppoint_time(), 8 * 60).after(new Date())) { //预约时间和当前时间比较
                        //开始推送
                        pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "overtime", "预约超时", "预约超时", "预约超时.mp3", 5);
                        ticketService.updateAppointmentState(ticketModel.get_id().toString(), "1");// 1--预约超时已推送
                        NoticeModel noticeModel = new NoticeModel();
                        StringBuilder address = new StringBuilder();
                        address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                                .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
//                    System.out.println("地址：" + address);

                        EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
//                    System.out.println(userModel);
                        if (userModel != null) {
//                        System.out.println("开始插入");
                            noticeModel.setState("0"); //未读
                            noticeModel.setAddress(address.toString());
                            noticeModel.setEngineerId(ticketModel.getMain_engineer());
                            noticeModel.setEngineerName(userModel.getName());
                            noticeModel.setUserId(ticketModel.getMain_engineer());
                            noticeModel.setIco("ico");
                            noticeModel.setType("overtime");
                            noticeModel.setTicketId(ticketModel.get_id().toString());
                            noticeModel.setTicketNo(ticketModel.getNo());
                            //添加消息记录
                            noticeService.insert(noticeModel);
                        }
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //补单提醒 examine
    private Boolean task6() {
        // 查询补单 且 推送状态为0 的所有工单
//              reorderState    0-未推送
//                              1--已推送
        try {
            List<TicketModel> list = ticketService.findByFromAndReorderState("buDan", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    //开始推送
                    pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "examine", "补单提醒", "有补单啦", "补单.mp3", 5);
                    ticketService.updateReorderState(ticketModel.get_id().toString(), "1");// 1--预约超时已推送
                    NoticeModel noticeModel = new NoticeModel();
                    StringBuilder address = new StringBuilder();
                    address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                            .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                            .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                            .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
//                    System.out.println("地址：" + address);

                    EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
//                    System.out.println(userModel);
                    if (userModel != null) {
//                        System.out.println("开始插入");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("examine");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }

                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //超时上门  预警
    private Boolean task7() {
        //   查询已接单（TS04） 且 推送状态为0 的所有工单
//          arriveState 0-未推送
//                    1--已预警推送
//                    2--已超时推送
        try {
            List<TicketModel> list = ticketService.findByStateAndArriveState("TS04", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    GroupModel groupModel = groupService.getByID(ticketModel.getProject());
                    if (dateAddMinutes(ticketModel.getAccept_time(), 8 * 60 + Integer.valueOf(groupModel.getDiff_time())).after(new Date())) {
                        //开始推送
                        pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "overtime", "超时上门", "超时", "超时预警.mp3", 5);
                        ticketService.updateArriveState(ticketModel.get_id().toString(), "1");// 1--预警已推送

                        NoticeModel noticeModel = new NoticeModel();
                        StringBuilder address = new StringBuilder();
                        address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                                .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("warn");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //超时上门  超时
    private Boolean task8() {
        //   查询已接单（TS04） 且 推送状态为1 的所有工单
//          arriveState 0-未推送
//                    1--已预警推送
//                    2--已超时推送
        try {
            List<TicketModel> list = ticketService.findByStateAndArriveState("TS04", "1");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    GroupModel groupModel = groupService.getByID(ticketModel.getProject());
                    if (dateAddMinutes(ticketModel.getAccept_time(), 8 * 60 + Integer.valueOf(groupModel.getResponse_time())).after(new Date())) {
                        //开始推送
                        pushNotice(ticketModel.get_id().toString(), ticketModel.getMain_engineer(), "overtime", "超时上门", "超时", "超时工单.mp3", 5);
                        ticketService.updateArriveState(ticketModel.get_id().toString(), "2");// 4--超时已推送

                        NoticeModel noticeModel = new NoticeModel();
                        StringBuilder address = new StringBuilder();
                        address.append(ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? ticketDao.getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? ticketDao.getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                                .append(ticketDao.getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? ticketDao.getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                                .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
                        noticeModel.setState("0"); //未读
                        noticeModel.setAddress(address.toString());
                        noticeModel.setEngineerId(ticketModel.getMain_engineer());
                        EngineerModel userModel = ticketService.getEngineerByID(ticketModel.getMain_engineer());
                        noticeModel.setEngineerName(userModel.getName());
                        noticeModel.setUserId(ticketModel.getMain_engineer());
                        noticeModel.setIco("ico");
                        noticeModel.setType("overtime");
                        noticeModel.setTicketId(ticketModel.get_id().toString());
                        noticeModel.setTicketNo(ticketModel.getNo());
                        //添加消息记录
                        noticeService.insert(noticeModel);
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    //抢单派单推送
    private Boolean task9() {
        try {  //查询 抢单推送状态为1 且 抢单中(TS00) 的所有工单  代表 待自动派单的列表
            //循环开始推送
//            System.out.println("=======抢单派单推送=======");
            List<TicketModel> list = ticketService.findByStateAndPushState("TS00", "1");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
//                    System.out.println("id" + ticketModel.get_id());
//                    System.out.println("创建时间   " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime()));
//                    System.out.println("当前时间   " + new Date());
                    if (!new Date(ticketModel.getCreateAt().getTime() + (ticketModel.getGrabTime() != null ? ticketModel.getGrabTime() * 60000 : 0)).after(new Date())) {
                        System.out.println("开始派单");
                        //设置工程师
                        List<UserListModel> ul = ticketService.findUserByTicketAndOther(ticketModel.getArea(), ticketModel.getFix_type(), "STAY", "staff");
//                        System.out.println(ul);
//                        System.out.println(ul.size());
//                        System.out.println("====================");
                        //历史
                        if (ul.size() == 0)
                            continue;
//                        System.out.println("随机数" + (int) (Math.random() * ul.size()));
                        HistoryModel historyModel = new HistoryModel("assignTo", "TS03", "", new Date());
                        historyModel.setStaff(ul.get((int) (Math.random() * ul.size())).get_id().toString());
                        //更新工单状态
                        ticketService.updateTicketByKey(ticketModel.get_id().toString(), "main_engineer", historyModel.getStaff());
                        ticketService.updateTicketByKeyDate(ticketModel.get_id().toString(), "assign_time", new Date());
                        ticketService.updateTicketByKey(ticketModel.get_id().toString(), "status", "TS03");
                        //添加历史
                        TicketModel ticketMode11 = ticketService.updateTicketByReminder(ticketModel.get_id().toString(), historyModel);

                        // 推送;
                        pushByTicketId2(ticketModel.get_id().toString(), "新工单：" + ticketModel.getNo(), "报修人:" + ticketModel.getCustomer(), "签到派单.mp3", 5);
                        System.out.println("id 为" + ticketModel.get_id().toString());

                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //预约单状态变更Stanly
    private Boolean task10() {
        // 查询已预约TS02 且 推送状态为0 的所有工单
//             appointmentState 0-未推送
//                              1--已推送
        try {
            List<TicketModel> list = ticketService.findByStateAndAppointmentState("TS21", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    System.out.println(ticketModel.getNo());
                    if (ticketModel.getAppoint_time().getTime()<new Date().getTime()) { //预约时间和当前时间比较
                        Integer size =ticketService.getAllDoingTicket(ticketModel.getMain_engineer());
                        if (size==0){
                            TicketModel ticketModel1 = ticketService.updateTicketByYuYue(ticketModel.get_id().toString());
                            System.out.println(ticketModel1.getNo());
                        }
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 协同工单的一致性操作
     * 1查询协同单并更新主工单中的协同工单号；2主工单工单状态和协同工单一致
     * @return
     */
    private Boolean task11() {
        // 查询已预约TS02 且 推送状态为0 的所有工单
//             appointmentState 0-未推送
//                              1--已推送
        try {
            //查询所有工单号未统一的工单
            List<TicketModel> list = ticketService.findBycooperateState("0", "0");
            System.out.println("长度：" + list.size());
            if (list.size() > 0) {
                Iterator<TicketModel> iterator = list.iterator();
                while (iterator.hasNext()) {
                    TicketModel ticketModel = iterator.next();
                    System.out.println(ticketModel.getNo());
                    if (ticketModel.getAppoint_time().getTime()<new Date().getTime()) { //预约时间和当前时间比较
                        Integer size =ticketService.getAllDoingTicket(ticketModel.getMain_engineer());
                        if (size==0){
                            TicketModel ticketModel1 = ticketService.updateTicketByYuYue(ticketModel.get_id().toString());
                            System.out.println(ticketModel1.getNo());
                        }
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }



    /**
     * 时间加减分钟
     *
     * @param startDate 要处理的时间，Null则为当前时间
     * @param minutes   加减的分钟
     * @return
     */
    public static Date dateAddMinutes(Date startDate, int minutes) {
        if (startDate == null) {
            startDate = new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + minutes);
        return c.getTime();
    }

    /**
     * 工时审核筛选
     *
     * @return
     */
    @RequestMapping(value = {"/getTaskTimeCheckList"}, method = RequestMethod.GET)
    @ResponseBody
    public String taskTimeCheckList(TicketModel ticketListModel, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.ScanneTicket(ticketListModel, pageNo, pageSize);
        List<TicketModel> newTicketModelList = new ArrayList<>();
        if (ticketListModel.getAbnormal().split(",")[0].equals("0")) {//未审核
            for (int i = 0; i < ticketModelList.size(); i++) {
                TicketModel ticketModel = ticketModelList.get(i);
                if (ticketModel.getCheckState().equals("0")) {
                    newTicketModelList.add(ticketModel);
                    System.out.println("找到一个");
                }
            }
        }
        if (ticketListModel.getAbnormal().split(",")[1].equals("0")) {//已审核
            for (int i = 0; i < ticketModelList.size(); i++) {
                TicketModel ticketModel = ticketModelList.get(i);
                if (ticketModel.getCheckState().equals("1")) {
                    newTicketModelList.add(ticketModel);
                    System.out.println("找到一个");
                }
            }
        }
        Integer itemCount = ticketModelList.size();
        if (ticketModelList != null) {
            for (TicketModel ticketModel : ticketModelList) {
                ticketModel.set_id(null);
                jsonResult.put("list", newTicketModelList);
                jsonResult.put("pageTotal", (pageNo * pageSize - itemCount) == 0 ? itemCount / pageSize : itemCount / pageSize + 1);
                jsonResult.put("itemCount", itemCount);
                jsonResult.put("pageNo", pageNo);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 工单录入历史数据列表
     *
     * @return
     */
    @RequestMapping(value = {"/getTicketsLu"}, method = RequestMethod.GET)
    @ResponseBody
    public String getTicketsLu(String enterId, Integer pageNo, Integer pageSize) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        List<TicketModel> ticketModelList = ticketService.getTicketsLu(enterId, pageNo, pageSize);
        if (ticketModelList != null) {
            for (TicketModel ticketModel : ticketModelList) {
                ticketModel.set_id(null);
                jsonResult.put("list", ticketModelList);
                jsonResult.put("message", "请求成功");
                jsonResult.put("statusCode", 200);
            }
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 根据登录人id返回职位
     *
     * @return
     */
    @RequestMapping(value = {"/getLoginMansJob"}, method = RequestMethod.GET)
    @ResponseBody
    public String getLoginMansJob(String loginId) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        Model engineerModel = ticketService.getIdsJob(loginId);
        if (engineerModel != null) {
            jsonResult.put("mansJob", engineerModel.getName());
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 首页的 数量
     *
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = {"/getTicketNums"}, method = RequestMethod.GET)
    @ResponseBody
    public String getTicketNums(String userId) throws JsonProcessingException {
        Map<String, Object> jsonResult = new HashMap<String, Object>();
        //带派遣
        TicketModel ticket1 = new TicketModel();
        ticket1.setManagerId(userId);
        ticket1.setStatus("TS01"); //待派遣
        List<TicketListModel> ticketModelList1 = ticketService.findForFind(ticket1);
        if (ticketModelList1 != null) {
            jsonResult.put("noSend", ticketModelList1.size());
        } else {
            jsonResult.put("noSend", 0);
        }
//        // 超时接单
        List<TicketListModel>  ticketModelList2 = ticketService.getNumByKeyValue(userId,"pushState","4");
//        TicketModel ticket2 = new TicketModel();
//        ticket2.setManagerId(userId);
////        ticket2.setStatus("TS03"); //已派遣
//        List<TicketListModel> ticketModelList2 = ticketService.findForFind(ticket2);
//        System.out.println("超时接单 size  " + ticketModelList2.size());
        if (ticketModelList2 != null) {
//            for (int i = 0; i < ticketModelList2.size(); i++) {
//                TicketListModel ticketModel = ticketModelList2.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if (dateAddMinutes(ticketModel.getAssign_time(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
//                    //未超时
//                    ticketModelList2.remove(i);
//                    i--;
//                }
//            }
            jsonResult.put("overtimeOrder", ticketModelList2.size());
        } else {
            jsonResult.put("overtimeOrder", 0);
        }
//        // 超时上门
//        TicketModel ticket3 = new TicketModel();
//        ticket3.setManagerId(userId);
////        ticket3.setStatus("TS04"); //已接单
//        List<TicketListModel> ticketModelList3 = ticketService.findForFind(ticket3);
        List<TicketListModel>  ticketModelList3 = ticketService.getNumByKeyValue(userId,"arriveState","2");
//
        if (ticketModelList3 != null) {
//            for (int i = 0; i < ticketModelList3.size(); i++) {
//                TicketListModel ticketModel = ticketModelList3.get(i);
//                GroupModel groupModel = groupService.getByID(ticketModel.getProject());
//                if (groupModel == null) {
//                    continue;
//                }
//                //循环判断其创建时间 加8小时 再加上 预警时间 是否大于当前时间
//                if (dateAddMinutes(ticketModel.getAccept_time(), 8 * 60 + Integer.valueOf(groupModel.getWork_due_time())).after(new Date())) {
//                    ticketModelList3.remove(i);
//                    i--;
//                }
//            }
            jsonResult.put("overtimeVisit", ticketModelList3.size());
        } else {
            jsonResult.put("overtimeVisit", 0);
        }

        //差评单
        TicketModel ticket4 = new TicketModel();
        ticket4.setManagerId(userId);
        ticket4.setAbnormal("1,1,0"); //差评单
        List<TicketListModel> ticketModelList4 = ticketService.findForFind(ticket4);
        if (ticketModelList4 != null) {
            jsonResult.put("bad", ticketModelList4.size());
        } else {
            jsonResult.put("bad", 0);
        }

        //已挂起
        TicketModel ticket5 = new TicketModel();
        ticket5.setManagerId(userId);
        ticket5.setStatus("TS21"); //已挂起
        List<TicketListModel> ticketModelList5 = ticketService.findForFind(ticket5);
        if (ticketModelList5 != null) {
            jsonResult.put("hangUp", ticketModelList5.size());
        } else {
            jsonResult.put("hangUp", 0);
        }

        //总工单
        TicketModel ticket6 = new TicketModel();
        ticket6.setManagerId(userId);
        List<TicketListModel> ticketModelList6 = ticketService.findForFind(ticket6);
        if (ticketModelList6 != null) {
            jsonResult.put("all", ticketModelList6.size());
        } else {
            jsonResult.put("all", 0);
        }
        //已完成  已结单 已回访 电话完成 已取消
        TicketModel ticket7 = new TicketModel();
        ticket7.setManagerId(userId);
        ticket7.setStatus("TS09,TS11,TS19,TS20"); //已完成
        List<TicketListModel> ticketModelList7 = ticketService.findForFind(ticket7);
        if (ticketModelList7 != null) {
            jsonResult.put("finish", ticketModelList7.size());
            jsonResult.put("nofinish", ticketModelList6.size() - ticketModelList7.size());
        } else {
            jsonResult.put("finish", 0);
            jsonResult.put("nofinish", ticketModelList6.size());
        }


        //通知
        List<NoticeModel> list8 = noticeService.findNoticeByUserIdNo(userId);
        if (list8 != null) {
            jsonResult.put("notice", list8.size());
        } else {
            jsonResult.put("notice", 0);
        }

        if (true) {
            jsonResult.put("message", "请求成功");
            jsonResult.put("statusCode", 200);
        } else {
            jsonResult.put("statusCode", 500);
            jsonResult.put("message", "请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }

    /**
     * 预约新单
     */
    @RequestMapping(value = {"/saveAppointmentTicket"}, method = RequestMethod.POST)
    @ResponseBody
    public String saveAppointmentTicket(String ticketID, HangUp hangUp, String managerID) throws JsonProcessingException {
        HistoryModel historyModel = new HistoryModel("appoint", "TS21", managerID, new Date());
        TicketModel ticketModel = ticketService.saveAppointmentTicket(ticketID, hangUp, historyModel);
        if (ticketModel != null) {
            ticketModel.set_id(null);
            jsonResult.setData(ticketModel);
            jsonResult.setStatusCode(200);
            jsonResult.setMessage("请求成功");
        } else {
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }
        return mapToJson.writeValueAsString(jsonResult);
    }


    /**
     * 工单-接单
     */
    @RequestMapping(value = {"/isOrderproces"}, method = RequestMethod.GET)
    @ResponseBody
    public String isOrderproces(String engineerId) throws JsonProcessingException {
        try{
            List<TicketModel> ticketModelList = ticketService.isOrderproces(engineerId);
            if (ticketModelList != null && ticketModelList.size() > 0) {
                Boolean flag = false;
                for (TicketModel ticketModel : ticketModelList) {
                    if (ticketModel.getStatus()!=null && !ticketModel.getStatus().equals("") && (ticketModel.getStatus().equals("TS04") || ticketModel.getStatus().equals("TS07") || ticketModel.getStatus().equals("TS30"))) {
                        System.out.println(ticketModel.getNo());
                        flag=true;
                        break;
                    }
                }
                if (flag){
                    jsonResult.setData(true);
                    jsonResult.setStatusCode(200);
                    jsonResult.setMessage("有订单进行中，跳转到预约新单");
                }else {
                    jsonResult.setStatusCode(200);
                    jsonResult.setData(false);
                    jsonResult.setMessage("没有工单进行中，可以正常接单");
                }
            } else {
                jsonResult.setStatusCode(200);
                jsonResult.setData(false);
                jsonResult.setMessage("没有工单进行中，可以正常接单");
            }
        }catch (Exception e){
            e.printStackTrace();
            jsonResult.setStatusCode(500);
            jsonResult.setMessage("请求失败");
        }

        return mapToJson.writeValueAsString(jsonResult);
    }

}

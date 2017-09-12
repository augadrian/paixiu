package spring_mongo.dao.daoImp;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import spring_mongo.dao.CategoriesDao;
import spring_mongo.dao.GroupDao;
import spring_mongo.dao.TicketDao;
import spring_mongo.dao.UserDao;
import spring_mongo.model.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guxiaowei on 2017/7/1.
 */
@Component("TicketDaoImp")
public class TicketDaoImp extends AbstractBaseMongoTemplete implements TicketDao {

    @Autowired
    private GroupDao groupDao;
    @Autowired
    private CategoriesDao categoriesDao;
    @Autowired
    private UserDao userDao;

    /**
     * 新增工单
     *
     * @param ticket
     */
    @Override
    public void insertTicket(TicketModel ticket) {
        DBObject object = new BasicDBObject();
        object.put("project", ticket.getProject());
        mongoTemplate.insert(object, "Default.tickets");
    }

    /**
     * 删除工单
     *
     * @param ticketName
     */
    @Override
    public void removeTicket(String ticketName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.is(ticketName);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, "Default.tickets");
    }

    @Override
    public Integer getTS01Ticket(List<String> area, String status) {
        //EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria = new Criteria("area");
        criteria.in(area);
        DBObject dbObject = new BasicDBObject();
        //dbObject.put("main_engineer", engineer);
        dbObject.put("status", status);
        Query query = new BasicQuery(dbObject);
        query.addCriteria(criteria);
        List<TicketModel> list = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return list.size();
    }

    @Override
    public Integer getAllStatusTicket(List<String> area) {
        //EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria = new Criteria("area");
        criteria.in(area);
        DBObject dbObject = new BasicDBObject();
        //dbObject.put("main_engineer", engineer);
        Query query = new BasicQuery(dbObject);
        query.addCriteria(criteria);
        List<TicketModel> list = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return list.size();
    }

    @Override
    public Integer getAllFinishTicket(List<String> area) {
        Query query = new Query();
        //EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria1 = new Criteria("area");
        criteria1.in(area);
        Criteria criteria = new Criteria();

        criteria.orOperator(Criteria.where("status").is("TS09"), Criteria.where("status").is("TS11"), Criteria.where("status").is("TS19"), Criteria.where("status").is("TS20"));
        query.addCriteria(criteria1).addCriteria(criteria);
        List<TicketModel> list = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return list.size();
    }

    @Override
    public Integer getAllBadTicket(List<String> area) {
        Query query = new Query();
        //EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria = new Criteria("area");
        criteria.in(area);
        query.addCriteria(criteria).addCriteria(Criteria.where("grade").lt(3));
        List<TicketModel> list = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return list.size();
    }

    public Integer getAllDoingTicket(String engineer) {
        Query query = new Query();
        EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria1 = new Criteria("area");
        criteria1.in(engineerModel.getArea());
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("status").is("TS04"), Criteria.where("status").is("TS07"), Criteria.where("status").is("TS30"), Criteria.where("status").is("TS31"));
        query.addCriteria(criteria1).addCriteria(criteria);
        List<TicketModel> list = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return list.size();
    }

    public TicketModel updateTicketByYuYue(String ticketID) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("status", "TS04");
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    @Override
    public List<TicketModel> findBycooperateState(String state, String status) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("cooperateState", state);
        if (!status.equals("0")) {
            dbObject.put("status", status);
        }
        Query query = new BasicQuery(dbObject);
        // 参数：查询条件，更改结果，集合名
        List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModels != null ? ticketModels : null;
    }

    /**
     * 催单
     *
     * @param ticketID
     * @return
     */
    @Override
    public TicketModel updateTicketByReminder(String ticketID, HistoryModel historyModel) {
        TicketModel ticketModel = new TicketModel();
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.push("history", historyModel);
        //取消单
        if (historyModel.getAction().equals("cancel")){
            update.set("status", "TS20");
            //update.set("cancle_note", "取消工单");
            update.set("updateAt", historyModel.getDate());
        }
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    /**
     * 改派遣
     *
     * @param ticketID
     * @return
     */
    @Override
    public TicketModel updateTicketByReverse(String ticketID,HistoryModel historyModel) {
        TicketModel ticketModel = new TicketModel();
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.push("history", historyModel);
        //派遣
        update.set("status", "TS03");
        update.set("main_engineer", historyModel.getUser());
        update.set("vice_engineer", new ArrayList<String>());
        update.set("vice_id", new ArrayList<String>());
        update.set("vice_no", new ArrayList<String>());
        update.set("isCooperate", false);
        update.set("mainNo", "");
        update.set("mainEngineer", "");
        update.set("updateAt", historyModel.getDate());
        update.set("assign_time", historyModel.getDate());
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    public TicketModel updateTicketByCooperate(String ticketID,HistoryModel historyModel,List<String> viceManagerID,List<String> vice_no,List<String> vice_id){
        TicketModel ticketModel = new TicketModel();
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.push("history", historyModel);
        //协同
        update.set("status", "TS03");
        update.set("from", "cooperate");
        update.set("main_engineer", historyModel.getUser());
        update.set("vice_engineer", viceManagerID);
        update.set("vice_no", vice_no);
        update.set("vice_id", vice_id);
        update.set("updateAt", historyModel.getDate());
        update.set("assign_time", historyModel.getDate());
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    @Override
    public TicketModel updateTicketByPart(String ticketID, PartModel partModel) {
        TicketModel ticketModel = new TicketModel();
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.push("parts", partModel);
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    @Override
    public TicketModel getTicketByNo(String no) {
        TicketModel ticketModel = new TicketModel();
        Query query = new Query();
        Criteria criteria = new Criteria("no");
        criteria.is(no);
        query.addCriteria(criteria);
        List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModels.size() > 0 ? ticketModels.get(0) : null;
    }

    @Override
    public TicketListModel getTicketByTicketNo(String no) {
        Query query = new Query();
        Criteria criteria = new Criteria("no");
        criteria.is(no);
        query.addCriteria(criteria);
        List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
        List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
        if (ticketModels != null) {
            for (TicketListModel ticket : ticketModels) {
                TicketListModel ticketModel = new TicketListModel();
                ticketModel.setObjectID(ticket.get_id().toString());
                ticketModel.setCreateTime(ticket.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime()) : "");
                ticketModel.setCustomer(ticket.getCustomer());
                ticketModel.setNo(ticket.getNo());
                ticketModel.setHistory(ticket.getHistory());
                ticketModel.setProject(ticket.getProject());
                ticketModel.setGrabTime(ticket.getGrabTime());
                ticketModel.setContent(ticket.getContent());
                ticketModel.setLevel(ticket.getLevel());
                ticketModel.setGrade(ticket.getGrade());
                ticketModel.setIs_vip(ticket.getIs_vip());
                ticketModel.setFrom(ticket.getFrom());
                ticketModel.setStatus("3");
                ticketModel.setFix_type(ticket.getFix_type());
                StringBuilder address = new StringBuilder();
                address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                        .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                        .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getAddress() : "" + "—")
                        .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                ticketModel.setAddress(address.toString());
                ticketModel.set_id(null);
                ticketModelList.add(ticketModel);
            }
        }
        return ticketModelList.size() > 0 ? ticketModelList.get(0) : null;
    }

    @Override
    public List<EngineerModel> getEngineerByTicketID(String ticketID) {
        TicketModel ticketModel = getByID(ticketID);
        if (ticketModel != null) {
            DBObject dbObject = new BasicDBObject();
            dbObject.put("area", ticketModel.getArea());
            dbObject.put("fix_type", ticketModel.getFix_type());
            dbObject.put("state", "STAY");
            dbObject.put("work_status", "WORK");
            DBObject fieldObject = new BasicDBObject();
            fieldObject.put("_id", true);
            fieldObject.put("name", true);
            Query query = new BasicQuery(dbObject);
            List<EngineerModel> engineerModelList = mongoTemplate.find(query, EngineerModel.class, "Default.users");
            List<EngineerModel> engineerModels = new ArrayList<EngineerModel>();
            if (engineerModelList != null) {
                for (EngineerModel engineerModel : engineerModelList) {
                    EngineerModel engineer = new EngineerModel();
                    engineer.setObjectID(engineerModel.get_id().toString());
                    engineer.setName(engineerModel.getName());
                    engineerModels.add(engineer);
                }
            }
            return engineerModels;
        }

        return null;
    }

    /**
     * 根据工程主管查询其管辖区域内的所有工程师
     * StanlyGK
     *
     * @param engineerManager
     * @return
     */
    @Override
    public List<EngineerModel> getEngineerByManagerID(String engineerManager) {
        EngineerModel engineerModel = getEngineerByID(engineerManager);
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Query query = new Query();
        query.addCriteria(criteria);
        List<EngineerModel> engineerModelList = mongoTemplate.find(query, EngineerModel.class, "Default.users");
        for (EngineerModel model : engineerModelList) {
            model.setObjectID(model.get_id().toString());
        }
        return engineerModelList;
    }

    @Override
    public List<TicketModel> getListByEngineer(String engineer) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("main_engineer", engineer);
        dbObject.put("status", "TS04");
        Query query = new BasicQuery(dbObject);
        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModelList.size() > 0 ? ticketModelList : null;
    }

    @Override
    public EngineerModel getEngineerByID(String engineer) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", engineer);
        Query query = new BasicQuery(dbObject);
        List<EngineerModel> engineerModelList = mongoTemplate.find(query, EngineerModel.class, "Default.users");
        return engineerModelList.size() > 0 ? engineerModelList.get(0) : null;
    }


    @Override
    public TicketModel updateTicket(String id, String engineer) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(id);
        query.addCriteria(criteria);
        // 设置修改内容
        HistoryModel historyModel = new HistoryModel("accept", "TS04", engineer, new Date());
        Update update = new Update();
        update.set("main_engineer", engineer);
        update.set("updateBy", engineer);
        update.set("status", "TS04");
        update.set("updateAt", new Date());
        update.set("accept_time", new Date());
        update.push("history", historyModel);
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(id) != null ? getByID(id) : null;
    }

    @Override
    public List<TicketModel> findTicketById(String ticketId) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }

    //根据工单查工程师
    @Override
    public List<UserListModel> findUserByTicket(String project, String area, String type) {
        Query query = new Query();
        Criteria criteria = new Criteria("project");
        criteria.is(project);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("area");
        criteria2.is(area);
        query.addCriteria(criteria2);
//        Criteria criteria3 = new Criteria("type"); //主管也能接收推送   可能要删
//        criteria3.in(type);
//        query.addCriteria(criteria3);
//        Criteria criteria4 = new Criteria("ReceivePushState"); //开启接收推送   可能要删
//        criteria4.in("1");
//        query.addCriteria(criteria4);

//        Criteria criteria4 = new Criteria("fix_type");
//        criteria4.is(fix_type);
//        query.addCriteria(criteria4);

//        Criteria criteria3 = Criteria.where("validStartTime").gt(new Date()).
//                and("versionLimitList").elemMatch
//                (Criteria.where("clientId").is(109).
//                        and("platFormCode").is(2);

        List<UserListModel> userlist = mongoTemplate.find(query, UserListModel.class, "Default.users");
        return userlist;
    } //根据工单查工程师

    //抢单派遣
    @Override
    public List<UserListModel> findUserByTicketAndOther(String area, String fix, String state, String type) {
        Query query = new Query();
        Criteria criteria = new Criteria("state");//接单状态
        criteria.is(state);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("area");  //区域
        criteria2.in(area);
        query.addCriteria(criteria2);
        Criteria criteria3 = new Criteria("fix_type");  //维修类型
        criteria3.in(fix);
        query.addCriteria(criteria3);
        Criteria criteria4 = new Criteria("type");  //工程师类型
        criteria4.in(type);
        query.addCriteria(criteria4);

        List<UserListModel> userlist = mongoTemplate.find(query, UserListModel.class, "Default.users");
        return userlist;
    }

    @Override
    public TicketModel getByID(String id) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        Query query = new BasicQuery(dbObject);
        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModelList != null ? ticketModelList.get(0) : null;
    }

    @Override
    public TicketModel updateCheckState(String ticketId, String checkState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("checkState", checkState);
        // 参数：查询条件，更改结果，集合名
//        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return findDocumentById(ticketId) != null ? findDocumentById(ticketId) : null;
    }

    @Override
    public void updatePushState(String ticketId, String pushState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("pushState", pushState);
        // 参数：查询条件，更改结果，集合名
//        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public void updateArriveState(String ticketId, String arriveState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("arriveState", arriveState);
        // 参数：查询条件，更改结果，集合名
//        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public void updateModState(String ticketId, String modState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("modState", modState);
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public void updateAppointmentState(String ticketId, String appointmentState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("appointmentState", appointmentState);
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public void updateReorderState(String ticketId, String reorderState) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("reorderState", reorderState);
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public GroupModel getGroupByID(String id) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        Query query = new BasicQuery(dbObject);
        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        return groupModelList.size() > 0 ? groupModelList.get(0) : null;
    }


    @Override
    public TicketModel findDocumentById(String id) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(new ObjectId(id));
        query.addCriteria(criteria);
        TicketModel ticketModel = mongoTemplate.findOne(query, TicketModel.class, "Default.tickets");
        return ticketModel;
    }

    @Override
    public List<TicketListModel> getListByManagerID(String engineerManager, Integer pageNo, Integer pageSize) {
        EngineerModel engineerModel = getEngineerByID(engineerManager);
        Sort sort = new Sort(Sort.Direction.ASC, "status");
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Query query = new Query();
        if (pageNo != 0 && pageSize != 0) {
            query.addCriteria(criteria).with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            if (ticketModels != null) {
                for (TicketListModel ticket : ticketModels) {
                    TicketListModel ticketModel = new TicketListModel();
                    ticketModel.setObjectID(ticket.get_id().toString());
                    ticketModel.setCreateTime(ticket.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime()) : "");
                    ticketModel.setCustomer(ticket.getCustomer());
                    ticketModel.setNo(ticket.getNo());
                    ticketModel.setProject(ticket.getProject());
                    ticketModel.setContent(ticket.getContent());
                    ticketModel.setLevel(ticket.getLevel());
                    ticketModel.setGrade(ticket.getGrade());
                    ticketModel.setIs_vip(ticket.getIs_vip());
                    ticketModel.setFrom(ticket.getFrom());
                    ticketModel.setStatus(ticket.getStatus());
                    ticketModel.setFix_type(ticket.getFix_type());
                    StringBuilder address = new StringBuilder();
                    address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                            .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                            .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getAddress() : "" + "—")
                            .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                    ticketModel.setAddress(address.toString());
                    ticketModelList.add(ticketModel);
                }
            }
            return sortForLevel(ticketModelList);
        } else {
            query.addCriteria(criteria);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            return ticketModels;
        }
    }

    @Override
    public List<TicketModel> getPhoneList(String phone, String engineerManager) {
        EngineerModel engineerModel = getEngineerByID(engineerManager);
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Query query = new Query();
        query.addCriteria(criteria);
        query.addCriteria(Criteria.where("phone").regex(".*?" + phone + ".*"));
        return mongoTemplate.find(query, TicketModel.class, "Default.tickets");

    }

    @Override
    public List<MaterielModel> getMaterielParent() {
        Criteria criteria = new Criteria("type");
        criteria.is("1");
        Query query = new Query();
        query.addCriteria(criteria);
        List<MaterielModel> materielModels = mongoTemplate.find(query, MaterielModel.class, "Default.materiels");
        for (MaterielModel materielModel : materielModels) {
            materielModel.setObjectID(materielModel.get_id().toString());
            materielModel.set_id(null);
        }
        return materielModels;
    }

    @Override
    public List<MaterielModel> getMaterielListByParentID(String parentID) {
        Criteria criteria = new Criteria("parent");
        criteria.is(parentID);
        Query query = new Query();
        query.addCriteria(criteria);
        List<MaterielModel> materielModels = mongoTemplate.find(query, MaterielModel.class, "Default.materiels");
        for (MaterielModel materielModel : materielModels) {
            materielModel.setObjectID(materielModel.get_id().toString());
            materielModel.set_id(null);
        }
        return materielModels;
    }

    @Override
    public MaterielModel getMaterielByID(String materielID) {
        Criteria criteria = new Criteria("_id");
        criteria.is(materielID);
        Query query = new Query();
        query.addCriteria(criteria);
        List<MaterielModel> materielModels = mongoTemplate.find(query, MaterielModel.class, "Default.materiels");
        for (MaterielModel materielModel : materielModels) {
            materielModel.setObjectID(materielModel.get_id().toString());
            materielModel.set_id(null);
        }
        return materielModels.size() > 0 ? materielModels.get(0) : null;
    }


    public Model getdetail(String _id) {
        Criteria criteria = new Criteria("_id");
        criteria.is(_id);
        Query query = new Query();
        query.addCriteria(criteria);
        Model groupModel = mongoTemplate.findOne(query, Model.class, "Default.groups");
        return groupModel;
    }

    public Model getCategorydetail(String _id) {
        Criteria criteria = new Criteria("_id");
        criteria.is(_id);
        Query query = new Query();
        query.addCriteria(criteria);
        Model groupModel = mongoTemplate.findOne(query, Model.class, "Default.categories");
        return groupModel;
    }

    /* public Model getCategoryGroup(String _id){
         Criteria criteria3 = new Criteria("_id");
         criteria3.is(_id);
         Query query3 = new Query();
         query3.addCriteria(criteria3);
         Model groupModel=mongoTemplate.findOne(query3,Model.class,"Default.categories");
         return groupModel;
     }
     public Model getCategory(String _id){
         Criteria criteria3 = new Criteria("_id");
         criteria3.is(_id);
         Query query3 = new Query();
         query3.addCriteria(criteria3);
         Model groupModel=mongoTemplate.findOne(query3,Model.class,"Default.categories");
         return groupModel;
     }*/
    @Override
    public List<TicketModelList> findByPhone(String phone, String engineerManager) {
        EngineerModel engineerModel = getEngineerByID(engineerManager);
        Query query = new Query();
        Criteria criteria1 = new Criteria("phone");
        criteria1.is(phone);
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        query.addCriteria(criteria1);
        query.addCriteria(criteria);
        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        TicketModelList ticketModelList1 = new TicketModelList();
        if (ticketModelList != null && ticketModelList.size() > 0) {
            for (TicketModel ticketModel : ticketModelList) {
                Model areaModel = getdetail(ticketModel.getArea());
                areaModel.setObjectID(areaModel.get_id().toString());
                areaModel.set_id(null);
                List<Model> arealist = new ArrayList<Model>();
                arealist.add(areaModel);

                Model projectModel = getdetail(ticketModel.getProject());
                projectModel.setObjectID(projectModel.get_id().toString());
                projectModel.set_id(null);
                List<Model> projectlist = new ArrayList<Model>();
                projectlist.add(projectModel);

                Model unitModel = getdetail(ticketModel.getUnit());
                unitModel.setObjectID(unitModel.get_id().toString());
                List<Model> unitlist = new ArrayList<Model>();
                unitModel.set_id(null);
                unitlist.add(unitModel);


                List<Model> grouplist = new ArrayList<Model>();
                Model groupModel = getCategorydetail(ticketModel.getFix_type_group());
                groupModel.setObjectID(groupModel.get_id().toString());
                groupModel.set_id(null);
                grouplist.add(groupModel);

                List<Model> typelist = new ArrayList<Model>();
                Model typeModel = getCategorydetail(ticketModel.getFix_type());
                typeModel.setObjectID(typeModel.get_id().toString());
                typeModel.set_id(null);
                typelist.add(typeModel);

                ticketModelList1.setObjectID(ticketModel.get_id().toString());
                ticketModelList1.setArea(arealist);
                ticketModelList1.setProject(projectlist);
                ticketModelList1.setUnit(unitlist);
                ticketModelList1.setFix_type_group(grouplist);
                ticketModelList1.setFix_type(typelist);
                ticketModelList1.setPhone(ticketModel.getPhone());
                ticketModelList1.setCustomer(ticketModel.getCustomer());
            }
            List<TicketModelList> list = new ArrayList<>();
            list.add(ticketModelList1);

            return list;

        }/*else if(ticketModelList.size()<=0){
            List<TicketModelList> lists=new ArrayList<TicketModelList>();
            TicketModelList ticketModelList2=new TicketModelList();
            Model areamodel=null;
            Model projectModel=null;
            Model unitModel=null;
            Model groupModel=null;
            Model typeModel=null;
            List arealist=new ArrayList();
            List projectlist=new ArrayList();
            List unitlist=new ArrayList();
            List grouplist=new ArrayList();
            List typelist=new ArrayList();
          for(String s:engineerModel.getArea()){
               areamodel=getdetail(s);
              areamodel.setObjectID(areamodel.get_id().toString());
               areamodel.set_id(null);
          }
            for(String s:engineerModel.getProject()){
                projectModel=getdetail(s);
                projectModel.setObjectID(projectModel.get_id().toString());
                projectModel.set_id(null);
            }
            for(String s:engineerModel.getFix_type_group()){
                groupModel=getCategorydetail(s);
                groupModel.setObjectID(groupModel.get_id().toString());
                groupModel.set_id(null);

            }
            for(String s:engineerModel.getFix_type()){
                    typeModel=getCategorydetail(s);
                    typeModel.setObjectID(typeModel.get_id().toString());
                    typeModel.set_id(null);

            }
          arealist.add(areamodel);
            projectlist.add(projectModel);
            grouplist.add(groupModel);
            typelist.add(typeModel);
          ticketModelList2.setArea(arealist);
          ticketModelList2.setProject(projectlist);
          ticketModelList2.setFix_type_group(grouplist);
          engineerModel.set_id(null);
          ticketModelList2.setFix_type(typelist);
          ticketModelList2.setName(engineerModel.getName());
          ticketModelList2.setPhone(engineerModel.getPhone());
          lists.add(ticketModelList2);

       return lists;
        }*/ else {
            return null;
        }

    }

    /**
     * 根据条件查询工单，抢单
     *
     * @param ticketName
     * @return
     */
    @Override
    public List<TicketListModel> findForRequery(String engineer, String ticketName, Integer pageNo, Integer pageSize) {
        EngineerModel engineerModel = getEngineerByID(engineer);
        List<String> fixType = engineerModel.getFix_type();
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Criteria criteria1 = new Criteria("fix_type");
        criteria1.in(engineerModel.getFix_type());
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        DBObject dbObject = new BasicDBObject();
        dbObject.put("status", ticketName);
        Query query = new BasicQuery(dbObject);
        if (pageNo != 0 && pageSize != 0) {
            //query.addCriteria(criteria).addCriteria(criteria1).with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
            query.addCriteria(criteria).addCriteria(criteria1);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            for (TicketListModel ticketListModel : ticketModels) {
                if (ticketListModel.getCreateAt().getTime() > (new Date().getTime() - (ticketListModel.getGrabTime() != null ? ticketListModel.getGrabTime() * 60000 : 0))) {
                    ticketModelList.add(ticketListModel);
                }
            }


            List<TicketListModel> result = new ArrayList<TicketListModel>();

            if (ticketModelList != null && ticketModelList.size() > 0) {
                int allCount = ticketModelList.size();//总条数4
                int pageCount = (allCount + pageSize - 1) / pageSize;//总页数
                if (pageNo >= pageCount) {
                    pageNo = pageCount;
                }
                int start = (pageNo - 1) * pageSize;
                int end = pageNo * pageSize;
                if (end >= allCount) {
                    end = allCount;
                }
                for (int i = start; i < end; i++) {
                    TicketListModel ticket = ticketModelList.get(i);
                    TicketListModel ticketModel = new TicketListModel();
                    ticketModel.setObjectID(ticket.get_id().toString());
                    ticketModel.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime()));
                    ticketModel.setGrabOverTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime() + (ticket.getGrabTime() != null ? ticket.getGrabTime() * 60000 : 0)));
                    ticketModel.setCustomer(ticket.getCustomer());
                    ticketModel.setNo(ticket.getNo());
                    ticketModel.setContent(ticket.getContent());
                    ticketModel.setGrabTime(ticket.getGrabTime());
                    ticketModel.setLevel(ticket.getLevel());
                    ticketModel.setGrade(ticket.getGrade());
                    ticketModel.setStatus(ticket.getStatus());
                    ticketModel.setIs_vip(ticket.getIs_vip());
                    ticketModel.setFrom(ticket.getFrom());
                    ticketModel.setFix_type(ticket.getFix_type());
                    StringBuilder address = new StringBuilder();
                    address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                            .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                            .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getName() : "" + "—")
                            .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                    ticketModel.setAddress(address.toString());
                    result.add(ticketModel);
                }
            }
            return sortForLevel(result);
        } else {
            query.addCriteria(criteria).addCriteria(criteria1);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            for (TicketListModel ticketListModel : ticketModels) {
                if (ticketListModel.getCreateAt().getTime() > (new Date().getTime() - (ticketListModel.getGrabTime() != null ? ticketListModel.getGrabTime() * 60000 : 0))) {
                    ticketModelList.add(ticketListModel);
                }
            }
            return ticketModelList;
        }

    }

    /**
     * 查询所有工单
     *
     * @return
     */
    @Override
    public List<TicketListModel> findAll(Integer pageNo, Integer pageSize) {
        Criteria criteria1 = new Criteria();
        criteria1.orOperator(Criteria.where("from").is("budan"), Criteria.where("verificate_time").is(0));
        Query query1 = new Query(criteria1);
        if (pageNo != 0 && pageSize != 0) {
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            query1.with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
            List<TicketListModel> ticketListModels = mongoTemplate.find(query1, TicketListModel.class, "Default.tickets");
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            if (ticketListModels != null) {
                for (TicketListModel ticket : ticketListModels) {
                    TicketListModel ticketListModel = new TicketListModel();
                    ticketListModel.setObjectID(ticket.get_id().toString());
                    ticketListModel.setNo(ticket.getNo());
                    ticketListModel.setIs_vip(ticket.getIs_vip());
                    ticketListModel.setContent(ticket.getContent());
                    ticketListModel.setStandard_time(ticket.getService_time());
                    ticketListModel.setService_time(ticket.getService_time());
                    ticketListModel.setCheckState(ticket.getCheckState());
                    ticketListModel.setVerificate_time(ticket.getVerificate_time());
                    ticketListModel.setCheckState(ticket.getCheckState());
                    ticketListModel.setAccept_time(ticket.getAccept_time());
                    ticketListModel.setFinish_time(ticket.getFinish_time());
                    Query query = new Query();
                    Criteria criteria = new Criteria("_id");
                    criteria.is(ticket.getMain_engineer());
                    query.addCriteria(criteria);
                    UserModel userModel = mongoTemplate.findOne(query, UserModel.class, "Default.users");
                    UserModel userModel1 = new UserModel();
                    userModel1.setName(userModel != null ? userModel.getName() : null);
                    ticketListModel.setEngineerName(userModel1.getName());
                    StringBuilder address = new StringBuilder();
                    address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                            .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                            .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getName() : "" + "—")
                            .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                    ticketListModel.setAddress(address.toString());
                    ticketModelList.add(ticketListModel);
                }
            }
            return ticketModelList;
        } else {
            List<TicketListModel> ticketListModels = mongoTemplate.find(query1, TicketListModel.class, "Default.tickets");
            return ticketListModels;
        }

    }

    /**
     * 根据条件   筛选全部工单
     *
     * @return
     */
    @Override
    public List<TicketListModel> findForFind(TicketModel ticketListModel) {
        DBObject dbObject = new BasicDBObject();
        Query query = new BasicQuery(dbObject);
        if (ticketListModel.getManagerId() != null) { //自己管辖的区域  必填
            EngineerModel engineerModel = getEngineerByID(ticketListModel.getManagerId());
            Criteria criteria = new Criteria("area");
            criteria.in(engineerModel.getArea());
            query.addCriteria(criteria);
        }

        if (ticketListModel.getSelectList() != null && "0".equals(ticketListModel.getSelectList())) {
            dbObject.put("status", "TS21");     //已挂起
        }
        if (ticketListModel.getSelectList() != null && "1".equals(ticketListModel.getSelectList())) {
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("status").is("TS09"), Criteria.where("status").is("TS11"), Criteria.where("status").is("TS19"), Criteria.where("status").is("TS20")); //已完成
            query.addCriteria(criteria);
        }
        if (ticketListModel.getSelectList() != null && "2".equals(ticketListModel.getSelectList())) {
            Criteria criteria = new Criteria();
            criteria.norOperator(Criteria.where("status").is("TS09"), Criteria.where("status").is("TS11"), Criteria.where("status").is("TS19"), Criteria.where("status").is("TS20")); //未完成
            query.addCriteria(criteria);
        }


        if (ticketListModel.getNo() != null) { //工单单号
//            dbObject.put("no", ticketListModel.getNo());
//            query.addCriteria(Criteria.where("no").regex(".*?\\" + ticketListModel.getNo() + ".*"));  //工单单号模糊查询
            query.addCriteria(Criteria.where("no").regex(".*?" + ticketListModel.getNo() + ".*"));
        }
        if (ticketListModel.getStartTime() != null && ticketListModel.getEndTime() != null) { //时间范围
            String[] starts = ticketListModel.getStartTime().split("-");
            String[] ends = ticketListModel.getEndTime().split("-");
            int startYear = Integer.parseInt(starts[0]), endYear = Integer.parseInt(ends[0]);
            int startMonth = Integer.parseInt(starts[1]), endMonth = Integer.parseInt(ends[1]);
            int startDay = Integer.parseInt(starts[2]), endDay = Integer.parseInt(ends[2]) + 1;
            int startHrs = Integer.parseInt(starts[3]), endHrs = Integer.parseInt(ends[3]);
            int startMin = Integer.parseInt(starts[4]), endMin = Integer.parseInt(ends[4]);
            dbObject.put("createAt", BasicDBObjectBuilder.start("$gte", new Date(startYear - 1900, startMonth - 1, startDay, startHrs, startMin)).add("$lt", new Date(endYear - 1900, endMonth - 1, endDay, endHrs, endMin)).get());
        }
        if (ticketListModel.getMain_engineer() != null) { //工程师
            dbObject.put("main_engineer", ticketListModel.getMain_engineer());   //工程师
        }
        if (ticketListModel.getUserId() != null) { //我的工单
            dbObject.put("main_engineer", ticketListModel.getUserId());   //我的工单
        }
        if (ticketListModel.getCheckState() != null) {  //审核状态
            String[] checks = ticketListModel.getCheckState().split(",");
            if (checks.length == 1) {
                dbObject.put("checkState", ticketListModel.getCheckState());
            }
            if (checks.length == 3) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2]));
                query.addCriteria(criteria);
            }
            if (checks.length == 4) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2]), Criteria.where("checkState").is(checks[3]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getType() != null) {  //工单类型
            String[] types = ticketListModel.getType().split(",");
            if (types.length == 1) {
                dbObject.put("type", ticketListModel.getType());
            } else if (types.length == 2) { //多选
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("type").is(types[0]), Criteria.where("type").is(types[1]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getStatus() != null) {  //工单状态
//            dbObject.put("status", ticketListModel.getStatus());
            String[] statuss = ticketListModel.getStatus().split(",");
//            System.out.println("=====" + statuss.length);
            if (statuss.length == 1) {//单个查询
                dbObject.put("status", statuss[0]);   //首页数量
            }
            if (statuss.length == 3) { //全部工单的筛选
                if ("0".equals(statuss[1])) {//待结单
//                    System.out.println("=====");
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS03"), Criteria.where("status").is("TS04"), Criteria.where("status").is("TS07"), Criteria.where("status").is("TS21"), Criteria.where("status").is("TS30"), Criteria.where("status").is("TS31"), Criteria.where("status").is("TS05"), Criteria.where("status").is("TS02"));
                    query.addCriteria(criteria);
                }
                if ("0".equals(statuss[0])) {//待派遣
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS01"), Criteria.where("status").is("TS00"));
                    query.addCriteria(criteria);
                }
                if ("0".equals(statuss[2])) {//已结单
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS09"), Criteria.where("status").is("TS11"), Criteria.where("status").is("TS19"), Criteria.where("status").is("TS20"));
                    query.addCriteria(criteria);
                }

            } else if (statuss.length == 4) { //工单首页
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("status").is(statuss[0]), Criteria.where("status").is(statuss[1]), Criteria.where("status").is(statuss[2]), Criteria.where("status").is(statuss[3]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getAbnormal() != null) {
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //000
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("arriveState").is("2"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //001
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("arriveState").is("2"));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //010
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //011
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //100
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("arriveState").is("2"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //101
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("arriveState").is("2"));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //110
                Criteria criteria = new Criteria();
                query.addCriteria(Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //111
            }
        }


//        if (ticketListModel.getAbnormal() != null && "0".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {   //超时接单
//            Criteria criteria = new Criteria();
//            criteria.orOperator(Criteria.where("status").is(""), Criteria.where("status").is(""));
//            criteria.or
//            query.addCriteria(criteria);
//            dbObject.put("pushState", "4");   //超时接单 已超时推送
//        }
//        if (ticketListModel.getAbnormal() != null && "0".equals(ticketListModel.getAbnormal().split(",")[1])) {   //超时上门
//
//            dbObject.put("arriveState", "2");   //超时上门 已超时推送
//        }
//        if (ticketListModel.getAbnormal() != null && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {   //差评单
////            dbObject.put("grade", BasicDBObjectBuilder.start("$gte", new Date(startYear - 1900, startMonth - 1, startDay)).add("$lt", new Date(endYear - 1900, endMonth - 1, endDay)).get());
////            dbObject.put("grade", new BasicDBObject("$lt", 3));
//            query.addCriteria(Criteria.where("grade").lt(3)); //gte: 大于等于  lt:小于
////            FindIterable<Document> iter1 = doc.find(new Document("age",new Document("$lt",22)));
////            dbObject.put("status", ticketListModel.getBad());   //差评单
//        }

        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        query.with(sort);
        List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");

//        return ticketModels;
        return sortForLevel(ticketModels);
    }

    @Override
    public List<TicketListModel> getNumByKeyValue(String user, String key, String value) {
        DBObject dbObject = new BasicDBObject();
        if (key != null) { //
            dbObject.put(key, value);   //
        }

        Query query = new BasicQuery(dbObject);
        if (user != null) { //自己管辖的区域  必填
            EngineerModel engineerModel = getEngineerByID(user);
            Criteria criteria = new Criteria("area");
            criteria.in(engineerModel.getArea());
            query.addCriteria(criteria);
        }

        List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");

        return ticketModels;
    }

    //工单录入的筛选
    @Override
    public List<TicketListModel> findForLu(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        DBObject dbObject = new BasicDBObject();
        Query query = new BasicQuery(dbObject);
        //录入id 不为空
        query.addCriteria(Criteria.where("enterId").ne("").ne(null));
        if (ticketListModel.getManagerId() != null) { //自己管辖的区域  必填
            EngineerModel engineerModel = getEngineerByID(ticketListModel.getManagerId());
            Criteria criteria = new Criteria("area");
            criteria.in(engineerModel.getArea());
            query.addCriteria(criteria);
        }
        if (ticketListModel.getNo() != null) {
//            dbObject.put("no", ticketListModel.getNo());   //工单单号
//            query.addCriteria(Criteria.where("no").regex(".*?\\" + ticketListModel.getNo() + ".*"));  //工单单号模糊查询
            query.addCriteria(Criteria.where("no").regex(".*?" + ticketListModel.getNo() + ".*"));
        }
        if (ticketListModel.getStartTime() != null && ticketListModel.getEndTime() != null) { //时间范围
            String[] starts = ticketListModel.getStartTime().split("-");
            String[] ends = ticketListModel.getEndTime().split("-");
            int startYear = Integer.parseInt(starts[0]), endYear = Integer.parseInt(ends[0]);
            int startMonth = Integer.parseInt(starts[1]), endMonth = Integer.parseInt(ends[1]);
            int startDay = Integer.parseInt(starts[2]), endDay = Integer.parseInt(ends[2]) + 1;
            int startHrs = Integer.parseInt(starts[3]), endHrs = Integer.parseInt(ends[3]);
            int startMin = Integer.parseInt(starts[4]), endMin = Integer.parseInt(ends[4]);
            dbObject.put("createAt", BasicDBObjectBuilder.start("$gte", new Date(startYear - 1900, startMonth - 1, startDay, startHrs, startMin)).add("$lt", new Date(endYear - 1900, endMonth - 1, endDay, endHrs, endMin)).get());
        }
        if (ticketListModel.getMain_engineer() != null) {   //工程师
            dbObject.put("main_engineer", ticketListModel.getMain_engineer());   //工程师
        }
        if (ticketListModel.getCheckState() != null) {
            String[] checks = ticketListModel.getCheckState().split(",");
            if (checks.length == 1) {
                dbObject.put("checkState", ticketListModel.getCheckState());   //审核状态
            }
            if (checks.length == 3) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2]));
                query.addCriteria(criteria);
            }
            if (checks.length == 4) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2]), Criteria.where("checkState").is(checks[3]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getType() != null) {  //工单类型
            String[] types = ticketListModel.getType().split(",");
            if (types.length == 1) {
                dbObject.put("type", ticketListModel.getType());
            } else if (types.length == 2) { //多选
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("type").is(types[0]), Criteria.where("type").is(types[1]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getStatus() != null) {  //工单状态
//            dbObject.put("status", ticketListModel.getStatus());
            String[] statuss = ticketListModel.getStatus().split(",");
//            System.out.println(statuss.length);
            if (statuss.length == 3) { //全部工单的筛选
                if ("0".equals(statuss[1])) {//待结单
//                    System.out.println("================");
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS03"), Criteria.where("status").is("TS04"), Criteria.where("status").is("TS07"), Criteria.where("status").is("TS21"), Criteria.where("status").is("TS30"), Criteria.where("status").is("TS31"), Criteria.where("status").is("TS05"), Criteria.where("status").is("TS02"));
                    query.addCriteria(criteria);
                }
                if ("0".equals(statuss[0])) {//待派遣
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS01"), Criteria.where("status").is("TS00"));
                    query.addCriteria(criteria);
                }
                if ("0".equals(statuss[2])) {//已结单
                    Criteria criteria = new Criteria();
                    criteria.orOperator(Criteria.where("status").is("TS09"), Criteria.where("status").is("TS11"), Criteria.where("status").is("TS19"), Criteria.where("status").is("TS20"));
                    query.addCriteria(criteria);
                }

            }
        }
        if (ticketListModel.getAbnormal() != null) {
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //000
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("arriveState").is("2"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //001
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("arriveState").is("2"));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //010
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("0".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //011
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("pushState").is("4"));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //100
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("arriveState").is("2"), Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "0".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //101
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("arriveState").is("2"));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //110
                Criteria criteria = new Criteria();
                query.addCriteria(Criteria.where("grade").lt(3));
                query.addCriteria(criteria);
            }
            if ("1".equals(ticketListModel.getAbnormal().split(",")[0]) && "1".equals(ticketListModel.getAbnormal().split(",")[1]) && "1".equals(ticketListModel.getAbnormal().split(",")[2])) {
                //111
            }
        }
//        if (ticketListModel.getAbnormal() != null && "0".equals(ticketListModel.getAbnormal().split(",")[2])) {   //差评单
//            query.addCriteria(Criteria.where("grade").lt(3)); //gte: 大于等于  lt:小于
//        }

        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        query.with(sort);
        List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");

        return sortForLevel(ticketModels);
//        return ticketModels;
    }

    @Override
    public List<TicketModel> ScanneTicket(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        DBObject dbObject = new BasicDBObject();
        Query query = new BasicQuery(dbObject);
        if (ticketListModel.getManagerId() != null) { //自己管辖的区域  必填
            EngineerModel engineerModel = getEngineerByID(ticketListModel.getManagerId());
            Criteria criteria = new Criteria("area");
            criteria.in(engineerModel.getArea());
            query.addCriteria(criteria);
        }
        if (ticketListModel.getNo() != null) {
            query.addCriteria(Criteria.where("no").regex(".*?" + ticketListModel.getNo() + ".*"));  //工单单号模糊查询
        }
        if (ticketListModel.getStartTime() != null && ticketListModel.getEndTime() != null) { //时间范围
            String[] starts = ticketListModel.getStartTime().split("-");
            String[] ends = ticketListModel.getEndTime().split("-");
            int startYear = Integer.parseInt(starts[0]), endYear = Integer.parseInt(ends[0]);
            int startMonth = Integer.parseInt(starts[1]), endMonth = Integer.parseInt(ends[1]);
            int startDay = Integer.parseInt(starts[2]), endDay = Integer.parseInt(ends[2]) + 1;
            dbObject.put("createAt", BasicDBObjectBuilder.start("$gte", new Date(startYear - 1900, startMonth - 1, startDay)).add("$lt", new Date(endYear - 1900, endMonth - 1, endDay)).get());
        }
        if (ticketListModel.getMain_engineer() != null) {

            dbObject.put("main_engineer", ticketListModel.getMain_engineer());   //工程师
        }
        if (ticketListModel.getCheckState() != null) {
            String[] checks = ticketListModel.getCheckState().split(",");
            if (checks.length == 1) {
                dbObject.put("checkState", ticketListModel.getCheckState());   //审核状态
            }
            if (checks.length == 3) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2])); //gte: 大于等于  lt:小于
                query.addCriteria(criteria);
            }
            if (checks.length == 4) {
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("checkState").is(checks[0]), Criteria.where("checkState").is(checks[1]), Criteria.where("checkState").is(checks[2]), Criteria.where("checkState").is(checks[3]));
                query.addCriteria(criteria);
            }
        }
        if (ticketListModel.getStatus() != null) {
//            dbObject.put("status", ticketListModel.getStatus());    //工单状态
            String[] statuss = ticketListModel.getStatus().split(",");
            if (statuss.length == 1) {
                dbObject.put("status", statuss[0]);
            } else if (statuss.length == 2) { //多选
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("status").is(statuss[0]), Criteria.where("status").is(statuss[1]));
                query.addCriteria(criteria);
            } else if (statuss.length == 3) { //多选
                Criteria criteria = new Criteria();
                criteria.orOperator(Criteria.where("status").is(statuss[0]), Criteria.where("status").is(statuss[1]), Criteria.where("status").is(statuss[2]));
                query.addCriteria(criteria);
            }
        }
        if (pageNo!=0 && pageSize!=0){
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            query.with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
            List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
            return sortByCheckState(sortByLevel(ticketModels));
        }else {
            List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
            return sortByCheckState(sortByLevel(ticketModels));
        }
    }


    /**
     * mongodb简单的分组查询
     *
     * @return
     */
    @Override
    public BasicDBList mongoGroup() {
        GroupBy groupBy = GroupBy.key("project").initialDocument("{count:0}").reduceFunction("function(doc,out){out.count++}")
                .finalizeFunction("function(out){return out;}");
        GroupByResults<TicketModel> res = mongoTemplate.group("Default.tickets", groupBy, TicketModel.class);
        DBObject obj = res.getRawResults();
        BasicDBList dbList = (BasicDBList) obj.get("retval");
        return dbList;
    }

    @Override
    public void saveData(DBObject obj) {
        mongoTemplate.save(obj, "Default.tickets");
    }

    /**
     * 根据客户姓名或手机号码搜索工单
     */
    @Override
    public List<TicketListModel> getListByNameOrPhone(String engineer, String nameAndPhone, Integer pageNo, Integer pageSize) {
        /*DBObject dbObject = new BasicDBObject();
        if (nameAndPhone.length() == 11) {
            dbObject.put("phone", nameAndPhone);
        } else {
            dbObject.put("customer", nameAndPhone);
        }
        Query query = new BasicQuery(dbObject);
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        query.with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
        List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModels;

        query.addCriteria(Criteria.where("phone").regex(".*?" + phone + ".*"));
        */


        EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Query query = new Query();
        if (isNumeric(nameAndPhone)) {
            query.addCriteria(Criteria.where("phone").regex(".*?" + nameAndPhone + ".*"));
        } else {
            query.addCriteria(Criteria.where("customer").regex(".*?" + nameAndPhone + ".*"));
        }
        if (pageNo != 0 && pageSize != 0) {
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            query.addCriteria(criteria).with(sort);
            List<TicketListModel> ticketModels = sortForLevel(mongoTemplate.find(query, TicketListModel.class, "Default.tickets"));
            Integer itemCount = ticketModels.size(); //总数
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            List<TicketListModel> result = new ArrayList<TicketListModel>();
            int pageCount = 0;  //总页数
            if (ticketModels != null && itemCount > 0) {
                pageCount = (itemCount + pageSize - 1) / pageSize;//总页数
//                if (pageNo >= pageCount) {
//                    pageNo = pageCount;
//                }
                int start = (pageNo - 1) * pageSize;
                int end = pageNo * pageSize;
                if (end >= itemCount) {
                    end = itemCount;
                }
                for (int i = start; i < end; i++) {
                    result.add(ticketModels.get(i));
                }
            }

            if (result != null) {
                for (TicketListModel ticket : result) {
                    TicketListModel ticketModel = new TicketListModel();
                    ticketModel.setObjectID(ticket.get_id().toString());
                    ticketModel.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime()));
                    ticketModel.setCustomer(ticket.getCustomer());
                    ticketModel.setNo(ticket.getNo());
                    ticketModel.setProject(ticket.getProject());
                    ticketModel.setContent(ticket.getContent());
                    ticketModel.setLevel(ticket.getLevel());
                    ticketModel.setGrade(ticket.getGrade());
                    ticketModel.setIs_vip(ticket.getIs_vip());
                    ticketModel.setFrom(ticket.getFrom());
                    ticketModel.setStatus(ticket.getStatus());
                    ticketModel.setFix_type(ticket.getFix_type());
                    StringBuilder address = new StringBuilder();
                    address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                            .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                            .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getName() : "" + "—")
                            .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                    ticketModel.setAddress(address.toString());
                    ticketModelList.add(ticketModel);
                }
            }
            return ticketModelList;
        } else {
            query.addCriteria(criteria);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            return ticketModels;
        }

    }

    @Override
    public void saveTickets(TicketModel ticketModel) {
        mongoTemplate.insert(ticketModel, "Default.tickets");
    }

    @Override
    public void saveSingleSupplement(SingleSupplementModel singleSupplementModel) {
        singleSupplementModel.setFrom("buDan");
        singleSupplementModel.setLevel("PL01");
        singleSupplementModel.setCheckState("0");
        singleSupplementModel.setStatus("TS09");
        singleSupplementModel.setCustomer("");
        singleSupplementModel.setContent("");
        singleSupplementModel.setAssign_time(new Date());
        singleSupplementModel.setGrade(3);
        singleSupplementModel.setVice_engineer(new ArrayList<String>());
        singleSupplementModel.setPhone("");
        singleSupplementModel.setHistory(new ArrayList<String>());
        singleSupplementModel.setGrade(3);
        singleSupplementModel.setValid(1);
        singleSupplementModel.setReorderState("0");
        singleSupplementModel.setIs_vip(false);
        singleSupplementModel.setCreateAt(new Date());
        singleSupplementModel.setUpdateAt(new Date());
        singleSupplementModel.setCreateBy(singleSupplementModel.getMain_engineer());
        mongoTemplate.insert(singleSupplementModel, "Default.tickets");
    }

    @Override
    public TicketModel cooperateToAdd(TicketModel ticketModel) {
        ticketModel.setCooperate(true);
        ticketModel.setRepair_detail("");
        ticketModel.setParts(new ArrayList<String>());
        ticketModel.setRepair_images(new ArrayList<String>());
        ticketModel.setHistory(new ArrayList<String>());
        ticketModel.setStatus("TS03");
        ticketModel.setCheckState("0");
        ticketModel.setObjectID("");
        ticketModel.set_id(null);
        ticketModel.setAssign_time(new Date());
        mongoTemplate.insert(ticketModel, "Default.tickets");
        return getTicketByNo(ticketModel.getNo()) != null ? getTicketByNo(ticketModel.getNo()) : null;
    }

    @Override
    public List<TicketModel> getListByFrom(String engineerManager, Integer pageNo, Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        DBObject dbObject = new BasicDBObject();
        dbObject.put("from", "buDan");
        dbObject.put("main_engineer", engineerManager);
        Criteria criteria = new Criteria("checkState");
        criteria.in("0","2","3");
        Query query = new BasicQuery(dbObject);
        query.addCriteria(criteria).with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
        if (pageNo != 0 && pageNo != 0) {
            List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
            for (TicketModel ticketModel : ticketModels) {
                ticketModel.setObjectID(ticketModel.get_id().toString());
                ticketModel.setCreateTime(ticketModel.getCreateAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketModel.getCreateAt().getTime()) : "");
                StringBuilder address = new StringBuilder();
                address.append(getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "") != null ? getGroupByID(ticketModel.getProject() != null ? ticketModel.getProject() : "").getName() : "" + "—")
                        .append(getGroupByID(ticketModel.getArea() != null ? ticketModel.getArea() : "") != null ? getGroupByID(ticketModel.getArea()).getName() : "" + "—")
                        .append(getGroupByID(ticketModel.getUnit() != null ? ticketModel.getUnit() : "") != null ? getGroupByID(ticketModel.getUnit()).getName() : "" + "—")
                        .append(ticketModel.getRoom() != null ? ticketModel.getRoom() : "");
                ticketModel.setAddress(address.toString());
                ticketModel.setEngineerName(getEngineerByID(ticketModel.getMain_engineer() != null ? ticketModel.getMain_engineer() : "").getName());
                ticketModel.set_id(null);
            }
            return sortCheckState(ticketModels);
        } else {
            List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
            return ticketModels;
        }


    }

    /**
     * 根据工单号更新工单的状态
     *
     * @param ticketNo
     * @return
     */
    @Override
    public List<TicketModel> auditTaskIime(String[] ticketNo, String[] checkState) {
        List<TicketModel> ticketsList = new ArrayList<TicketModel>();
        if (ticketNo.length != checkState.length) {
            return null;
        } else {
            for (int i = 0; i < ticketNo.length; i++) {
                String ticketsNo = ticketNo[i];
                String checkStates = checkState[i];
                List<TicketModel> ticketModels = null;
                try {
                    ticketModels = findByNo(ticketsNo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ticketModels.size() != 1) {
                    ticketsList = null;
                } else {
                    //设置修改条件
                    Query query = new Query();
                    Criteria criteria = new Criteria("no");
                    criteria.is(ticketsNo);
                    query.addCriteria(criteria);
                    //设置修改内容
                    Update updateStatus = Update.update("checkState", checkStates);//已核定
                    //参数：查询条件，更改结果，集合名
                    mongoTemplate.updateFirst(query, updateStatus, "Default.tickets");
                    List<TicketModel> ticketModele = findByNo(ticketsNo);
                    TicketModel tick = ticketModele.get(0);
                    ticketsList.add(tick);
                }
            }
            return ticketsList;
        }
    }

    @Override
    public TicketModel refuseApplication(String no, String checkState, String refuseReason) {
        Query query = new Query();
        Criteria criteria = new Criteria("no");
        criteria.is(no);
        query.addCriteria(criteria);
        //设置修改内容
        Update updateStatus = Update.update("checkState", checkState);//拒绝
        updateStatus.set("refuseReason", refuseReason);
        //参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, updateStatus, "Default.tickets");
        TicketModel ticketModel = mongoTemplate.findOne(query, TicketModel.class, "Default.tickets");
        return ticketModel;
    }

    @Override
    public List<TicketModel> findByNo(String ticketID) {
        Query query = new Query();
        Criteria criteria = new Criteria("no");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }


    //通过 工单状态 及 推送状态 查询
    @Override
    public List<TicketModel> findByStateAndPushState(String state, String pushState) {
        Query query = new Query();
        Criteria criteria = new Criteria("status");
        criteria.is(state);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("pushState");
        criteria2.is(pushState);
        query.addCriteria(criteria2);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }   //通过 工单状态 及 推送状态 查询

    @Override
    public List<TicketModel> findByStateAndArriveState(String state, String arriveState) {
        Query query = new Query();
        Criteria criteria = new Criteria("status");
        criteria.is(state);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("arriveState");
        criteria2.is(arriveState);
        query.addCriteria(criteria2);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }

    //通过 工单状态 及 推送状态 查询差评
    @Override
    public List<TicketModel> findByStateAndModState(String state, String modState) {
        Query query = new Query();
        Criteria criteria = new Criteria("status");
        criteria.is(state);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("modState");
        criteria2.is(modState);
        query.addCriteria(criteria2);
        query.addCriteria(Criteria.where("grade").lt(3)); //gte: 大于等于  lt:小于
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    } //通过 工单状态 及 推送状态 查询

    //通过 工单状态 及 推送状态 查询预约超时
    @Override
    public List<TicketModel> findByStateAndAppointmentState(String state, String appointmentState) {
        Query query = new Query();
        Criteria criteria = new Criteria("status");
        criteria.is(state);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("appointmentState");
        criteria2.is(appointmentState);
        query.addCriteria(criteria2);
//        query.addCriteria(Criteria.where("grade").lt(3)); //gte: 大于等于  lt:小于
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }

    //通过 工单来源 及 推送状态 查询补单提醒
    @Override
    public List<TicketModel> findByFromAndReorderState(String from, String reorderState) {
        Query query = new Query();
        Criteria criteria = new Criteria("from");
        criteria.is(from);
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("reorderState");
        criteria2.is(reorderState);
        query.addCriteria(criteria2);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }
    //通过 工单状态 及 推送状态 查询


    //通过 工单状态 及 推送状态 查询
    @Override
    public List<TicketModel> findByPushState(String pushState) {
        Query query = new Query();
        Criteria criteria = new Criteria("pushState");
        criteria.is(pushState);
        query.addCriteria(criteria);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }

    @Override
    public List<TicketModel> findByState(String status) {
        Query query = new Query();
        Criteria criteria = new Criteria("status");
        criteria.is(status);
        query.addCriteria(criteria);
        List<TicketModel> ticketList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketList;
    }

    /**
     * 根据工单号修改工单的核定工时
     *
     * @param ticketNo,checkTaskTime
     * @return
     */
    @Override
    public List<TicketModel> editEngineerTaskIime(String ticketNo, int[] checkTaskTime, String[] fixType) {
        List<TicketModel> ticketsList = new ArrayList<TicketModel>();
        if (checkTaskTime.length == 0) {
            return null;
        } else {
            List<TicketModel> ticketModels = null;
            try {
                ticketModels = findByNo(ticketNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ticketModels.size() != 1) {
                ticketsList = null;
            } else {
                //设置修改条件
                Query query = new Query();
                Criteria criteria = new Criteria("no");
                criteria.is(ticketNo);
                query.addCriteria(criteria);
                //设置修改内容
                Update updateFixType = Update.update("fix_type3", fixType);
                Update updateTime = Update.update("verificateTimeDetail", checkTaskTime);//修改核定时间
                Update updateStatus = Update.update("checkState", "3");//已核定
                //参数：查询条件，更改结果，集合名
                mongoTemplate.updateFirst(query, updateFixType, "Default.tickets");
                mongoTemplate.updateFirst(query, updateTime, "Default.tickets");
                mongoTemplate.updateFirst(query, updateStatus, "Default.tickets");
                List<TicketModel> ticketModele = findByNo(ticketNo);
                TicketModel tick = ticketModele.get(0);
                ticketsList.add(tick);
            }
            return ticketsList;
        }
    }

    public List<TicketModel> updateRatifyTaskTime(String ticketNo,int[] checkTaskTime,String[] fixType){
        List<TicketModel> ticketsList = new ArrayList<TicketModel>();
        if (checkTaskTime.length == 0) {
            return null;
        } else {
            List<TicketModel> ticketModels = null;
            try {
                ticketModels = findByNo(ticketNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ticketModels.size() != 1) {
                ticketsList = null;
            } else {
                //设置修改条件
                Query query = new Query();
                Criteria criteria = new Criteria("no");
                criteria.is(ticketNo);
                query.addCriteria(criteria);
                //设置修改内容
                Update updateFixType = Update.update("fix_type3", fixType);
                Update updateTime = Update.update("verificateTimeDetail", checkTaskTime);//修改核定时间
                Update updateStatus = Update.update("checkState", "2");//已核定
                //参数：查询条件，更改结果，集合名
                mongoTemplate.updateFirst(query, updateFixType, "Default.tickets");
                mongoTemplate.updateFirst(query, updateTime, "Default.tickets");
                mongoTemplate.updateFirst(query, updateStatus, "Default.tickets");
                List<TicketModel> ticketModele = findByNo(ticketNo);
                TicketModel tick = ticketModele.get(0);
                ticketsList.add(tick);
            }
            return ticketsList;
        }
    }

    /**
     * 更新工单 按字段
     *
     * @param ticketId
     * @param key
     * @param value
     */
    @Override
    public void updateTicketByKey(String ticketId, String key, String value) {
        //设置修改条件
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        //设置修改内容
        Update update = Update.update(key, value);//已通过
        //参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public void updateTicketByKeyDate(String ticketId, String key, Date value) {
        //设置修改条件
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketId);
        query.addCriteria(criteria);
        //设置修改内容
        Update update = Update.update(key, value);//已通过
        //参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
    }

    @Override
    public List<TicketModel> findTimeOutTickets(List<TicketModel> ticketModelList) {
        List<TicketModel> newTicketModelList = new ArrayList<TicketModel>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.get(i).getStatus().equals("TS04")) {//所有已接单的工单
                TicketModel ticketModel = ticketModelList.get(i);
                String project = ticketModel.getProject();
                Query query = new Query();
                Criteria criteria = new Criteria("_id");
                criteria.is(new ObjectId(project));
                query.addCriteria(criteria);
                try {
                    GroupModel groupModel = mongoTemplate.findOne(query, GroupModel.class, "Default.groups");
                    int responseTime = Integer.valueOf(groupModel.getResponse_time());//得到响应时间
                    Date assignTime = ticketModel.getAssign_time();//得到接单时间
                    if (((new Date()).getTime() - assignTime.getTime()) / (1000 * 60) > responseTime) {
                        newTicketModelList.add(ticketModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return newTicketModelList;
    }

    @Override
    public TicketServiceModel getServiceListByTicketID(String ticketID) {
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        Query query = new Query(criteria);
        List<TicketServiceModel> ticketServiceModelList = mongoTemplate.find(query, TicketServiceModel.class, "Default.tickets");
        TicketServiceModel ticketServiceModel = ticketServiceModelList.size() > 0 ? ticketServiceModelList.get(0) : null;
        if (ticketServiceModel != null) {
            if (ticketServiceModel.getFix_type3() != null && ticketServiceModel.getFix_type3().size() > 0) {
                List<String> name = new ArrayList<String>();
                List<String> fix_type3 = ticketServiceModel.getFix_type3();
                for (int i = 0; i < ticketServiceModel.getFix_type3().size(); i++) {
                    String fixType = fix_type3.get(i);
                    System.out.println(fixType);
                    Criteria getName = new Criteria("_id");
                    getName.is(fixType);
                    Query getNameQuery = new Query(getName);
                    List<FixTypeModel> fixTypeModelList = mongoTemplate.find(getNameQuery, FixTypeModel.class, "Default.categories");
                    FixTypeModel fixTypeModel = fixTypeModelList.size() > 0 ? fixTypeModelList.get(0) : null;
                    name.add(fixTypeModel != null ? fixTypeModel.getName() : "");
                }
                ticketServiceModel.setName(name);
            }
        }
        return ticketServiceModel != null ? ticketServiceModel : null;
    }

    @Override
    public List<TicketModel> timeTaskCheck(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        DBObject dbObject = new BasicDBObject();
        Query query = new BasicQuery(dbObject);
        //工单单号模糊查询
        if (ticketListModel.getNo() != null) {
            query.addCriteria(Criteria.where("no").regex(".*?" + ticketListModel.getNo() + ".*"));  //工单单号模糊查询
        }
        //时间段筛选
        if (ticketListModel.getStartTime() != null && ticketListModel.getEndTime() != null) { //时间范围
            String[] starts = ticketListModel.getStartTime().split("-");
            String[] ends = ticketListModel.getEndTime().split("-");
            int startYear = Integer.parseInt(starts[0]), endYear = Integer.parseInt(ends[0]);
            int startMonth = Integer.parseInt(starts[1]), endMonth = Integer.parseInt(ends[1]);
            int startDay = Integer.parseInt(starts[2]), endDay = Integer.parseInt(ends[2]) + 1;
            dbObject.put("createAt", BasicDBObjectBuilder.start("$gte", new Date(startYear - 1900, startMonth - 1, startDay)).add("$lt", new Date(endYear - 1900, endMonth - 1, endDay)).get());
        }
        //工程师筛选
        if (ticketListModel.getMain_engineer() != null) {
            dbObject.put("main_engineer", ticketListModel.getMain_engineer());   //工程师
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        query.with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
        List<TicketModel> ticketModels = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModels;
    }

    /**
     * 主管根据客户姓名或手机号码筛选自己录入的工单
     */
    @Override
    public List<TicketListModel> getListByEngineerAndNameOrPhone(String engineer, String nameAndPhone, String enterId, Integer pageNo, Integer pageSize) {
        EngineerModel engineerModel = getEngineerByID(engineer);
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        Query query = new Query();
        if (isNumeric(nameAndPhone)) {
            query.addCriteria(Criteria.where("phone").regex(".*?" + nameAndPhone + ".*"));
        } else {
            query.addCriteria(Criteria.where("customer").regex(".*?" + nameAndPhone + ".*"));
        }
        if (enterId != null) {
            Criteria criteria2 = new Criteria("enterId");
            criteria2.is(enterId);
            query.addCriteria(criteria2);
        }
        if (pageNo != 0 && pageSize != 0) {
            Sort sort = new Sort(Sort.Direction.DESC, "createAt");
            query.addCriteria(criteria).with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            List<TicketListModel> ticketModelList = new ArrayList<TicketListModel>();
            if (ticketModels != null) {
                for (TicketListModel ticket : ticketModels) {
                    TicketListModel ticketModel = new TicketListModel();
                    ticketModel.setObjectID(ticket.get_id().toString());
                    ticketModel.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getCreateAt().getTime()));
                    ticketModel.setCustomer(ticket.getCustomer());
                    ticketModel.setNo(ticket.getNo());
                    ticketModel.setProject(ticket.getProject());
                    ticketModel.setContent(ticket.getContent());
                    ticketModel.setLevel(ticket.getLevel());
                    ticketModel.setGrade(ticket.getGrade());
                    ticketModel.setIs_vip(ticket.getIs_vip());
                    ticketModel.setFrom(ticket.getFrom());
                    ticketModel.setStatus(ticket.getStatus());
                    ticketModel.setFix_type(ticket.getFix_type());
                    StringBuilder address = new StringBuilder();
                    address.append(getGroupByID(ticket.getProject() != null ? ticket.getProject() : "") != null ? getGroupByID(ticket.getProject() != null ? ticket.getProject() : "").getName() : "" + "—")
                            .append(getGroupByID(ticket.getArea() != null ? ticket.getArea() : "") != null ? getGroupByID(ticket.getArea()).getName() : "" + "—")
                            .append(getGroupByID(ticket.getUnit() != null ? ticket.getUnit() : "") != null ? getGroupByID(ticket.getUnit()).getName() : "" + "—")
                            .append(ticket.getRoom() != null ? ticket.getRoom() : "");
                    ticketModel.setAddress(address.toString());
                    ticketModelList.add(ticketModel);
                }
            }

            return ticketModelList;
        } else {
            query.addCriteria(criteria);
            List<TicketListModel> ticketModels = mongoTemplate.find(query, TicketListModel.class, "Default.tickets");
            return ticketModels;
        }

    }

    @Override
    public List<TicketModel> getTicketsLu(String enterId, Integer pageNo, Integer pageSize) {
        Criteria criteria = new Criteria("enterId");
        criteria.is(enterId);
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        query.addCriteria(criteria).with(sort).skip((pageNo - 1) * pageSize).limit(pageSize);
        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModelList.size() > 0 ? ticketModelList : null;
    }

    @Override
    public Model getIdsJob(String loginManId) {
        Criteria criteria = new Criteria("_id");
        criteria.is(new ObjectId(loginManId));
        Query query = new Query(criteria);
        EngineerModel engineerModel = mongoTemplate.findOne(query, EngineerModel.class, "Default.users");
        if (engineerModel != null) {
            String engin = engineerModel.getPosition();
            Criteria criterial = new Criteria("value");
            criterial.is(engin);
            Query queryr = new Query(criterial);
            Model categoryModel = mongoTemplate.findOne(queryr, Model.class, "Default.categories");
            return categoryModel;
        } else {
            return null;
        }

    }

    @Override
    public TicketModel saveAppointmentTicket(String ticketID, HangUp hangUp, HistoryModel historyModel) {
        HangUpModel hangUpModel = new HangUpModel();
        try {
            // String a= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hangUpModel.getAppointmentTime());
            //String b=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hangUpModel.getRestart_time());
            //hangUpModel.setAppointmentTime(new SimpleDateFormat().parse(a));
            //hangUpModel.setRestart_time(new SimpleDateFormat().parse(b));

            hangUpModel.setRestart_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(hangUp.getRestart_time()));
            hangUpModel.setDate(hangUp.getDate());
            hangUpModel.setReason(hangUp.getReason());
        } catch (Exception e) {

        }
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(ticketID);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("hang_up", hangUpModel);
        update.push("history", historyModel);
        update.set("status", "TS21");
        update.set("appoint_time", hangUpModel.getRestart_time());
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "Default.tickets");
        return getByID(ticketID) != null ? getByID(ticketID) : null;
    }

    @Override
    public List<TicketModel> isOrderproces(String engineerId) {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("main_engineer").is(engineerId), Criteria.where("vice_engineer").is(engineerId));
        Query query = new Query();
        query.addCriteria(criteria);
        List<TicketModel> ticketModelList = mongoTemplate.find(query, TicketModel.class, "Default.tickets");
        return ticketModelList.size() > 0 ? ticketModelList : null;

    }


    /**
     * 匹配是否包含数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    /*public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }*/

    /**
     * 按level排序
     */
    public List<TicketListModel> sortForLevel(List<TicketListModel> ticketModelList) {
        List<TicketListModel> listLevelThree = new ArrayList<TicketListModel>();
        List<TicketListModel> listLevelTwo = new ArrayList<TicketListModel>();
        List<TicketListModel> listLevelOne = new ArrayList<TicketListModel>();
        List<List<TicketListModel>> newList = new ArrayList<List<TicketListModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.get(i).getLevel() != null && ticketModelList.get(i).getLevel().equals("PL03")) {//危机
                listLevelThree.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getLevel() != null && ticketModelList.get(i).getLevel().equals("PL02")) {//紧急
                listLevelTwo.add(ticketModelList.get(i));
            } else {//一般
                listLevelOne.add(ticketModelList.get(i));
            }
        }
        newList.add(sortForStatus(listLevelThree));
        newList.add(sortForStatus(listLevelTwo));
        newList.add(sortForStatus(listLevelOne));
        return listForNew(newList);
    }

    /**
     * 按state排序
     */
    public List<TicketListModel> sortForStatus(List<TicketListModel> ticketModelList) {
        // 待派遣TS01
        List<TicketListModel> listStatusOne = new ArrayList<TicketListModel>();
        // 抢单中TS00
        List<TicketListModel> listStatusTwo = new ArrayList<TicketListModel>();
        // 已派遣TS03
        List<TicketListModel> listStatusThree = new ArrayList<TicketListModel>();
        // 已报价TS30
        List<TicketListModel> listStatusFour = new ArrayList<TicketListModel>();
        // 已接单TS04
        List<TicketListModel> listStatusFive = new ArrayList<TicketListModel>();
        // 已预约TS02
        List<TicketListModel> listStatusSix = new ArrayList<TicketListModel>();
        // 已挂单TS21
        List<TicketListModel> listStatusSeven = new ArrayList<TicketListModel>();
        // 已上门TS07
        List<TicketListModel> listStatusEight = new ArrayList<TicketListModel>();
        // 已确认报价TS31
        List<TicketListModel> listStatusNine = new ArrayList<TicketListModel>();
        // 已结单TS09
        List<TicketListModel> listStatusTen = new ArrayList<TicketListModel>();
        // 电话完成TS19
        List<TicketListModel> listStatusEleven = new ArrayList<TicketListModel>();
        // 已评价
        List<TicketListModel> listStatusTwelve = new ArrayList<TicketListModel>();
        // 已回访TS11
        List<TicketListModel> listStatusThreeteen = new ArrayList<TicketListModel>();
        // 取消工单TS20
        List<TicketListModel> listStatusFourteen = new ArrayList<TicketListModel>();
        //其他
        List<TicketListModel> listStatusFiveteen = new ArrayList<TicketListModel>();
        List<List<TicketListModel>> list = new ArrayList<List<TicketListModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS01")) {// 待派遣TS01
                listStatusOne.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS00")) {// 抢单中TS00
                listStatusTwo.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS03")) {// 已派遣TS03
                listStatusThree.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS30")) {// 已报价TS30
                listStatusFour.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS04")) {// 已接单TS04
                listStatusFive.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS02")) {// 已预约TS02
                listStatusSix.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS21")) {// 已挂单TS21
                listStatusSeven.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS07")) {// 已上门TS07
                listStatusEight.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS31")) {// 已确认报价TS31
                listStatusNine.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS09")) {// 已结单TS09
                listStatusTen.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS19")) {// 电话完成TS19
                listStatusEleven.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS08")) {// 已评价
                listStatusTwelve.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS11")) {// 已回访TS11
                listStatusThreeteen.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS20")) {// 取消工单TS20
                listStatusFourteen.add(ticketModelList.get(i));
            } else {//其他
                listStatusFiveteen.add(ticketModelList.get(i));
            }
        }
        list.add(listStatusOne);
        list.add(listStatusTwo);
        list.add(listStatusThree);
        list.add(listStatusFour);
        list.add(listStatusFive);
        list.add(listStatusSix);
        list.add(listStatusSeven);
        list.add(listStatusEight);
        list.add(listStatusNine);
        list.add(listStatusTen);
        list.add(listStatusEleven);
        list.add(listStatusTwelve);
        list.add(listStatusThreeteen);
        list.add(listStatusFourteen);
        list.add(listStatusFiveteen);
        return listForNew(list);
    }

    //组合
    public List<TicketListModel> listForNew(List<List<TicketListModel>> list) {
        List<TicketListModel> newList = new ArrayList<TicketListModel>();
        for (int i = 0; i < list.size(); i++) {
            List<TicketListModel> ticketModels = list.get(i);
            for (int j = 0; j < ticketModels.size(); j++) {
                newList.add(ticketModels.get(j));
            }
        }
        return newList;
    }

    /**
     * 根据审核状态排序
     *
     * @param ticketModelList
     * @return
     */
    public List<TicketModel> sortByCheckState(List<TicketModel> ticketModelList) {
        List<TicketModel> listCheckStateOne = new ArrayList<TicketModel>();
        List<TicketModel> listCheckStateZero = new ArrayList<TicketModel>();
        List<List<TicketModel>> newList = new ArrayList<List<TicketModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.size() > 0 && ticketModelList.get(i).getCheckState() != null && ticketModelList.get(i).getCheckState().equals("0")) {//未审核
                listCheckStateZero.add(ticketModelList.get(i));
            } else if (ticketModelList.size() > 0 && ticketModelList.get(i).getCheckState() != null && ticketModelList.get(i).getCheckState().equals("1")) {//审核
                listCheckStateOne.add(ticketModelList.get(i));
            }
        }
        newList.add(sortByStatus(listCheckStateZero));
        newList.add(sortByStatus(listCheckStateOne));
        return listByNew(newList);
    }

    /**
     * 根据审核状态排序
     *
     * @param ticketModelList
     * @return
     */
    public List<TicketModel> sortCheckState(List<TicketModel> ticketModelList) {
        List<TicketModel> listCheckStateThree = new ArrayList<TicketModel>();
        List<TicketModel> listCheckStateZero = new ArrayList<TicketModel>();
        List<TicketModel> listCheckStateTwo = new ArrayList<TicketModel>();
        List<List<TicketModel>> newList = new ArrayList<List<TicketModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.size() > 0 && ticketModelList.get(i).getCheckState() != null && ticketModelList.get(i).getCheckState().equals("0")) {//未审核
                listCheckStateZero.add(ticketModelList.get(i));
            } else if (ticketModelList.size() > 0 && ticketModelList.get(i).getCheckState() != null && ticketModelList.get(i).getCheckState().equals("2")) {//已核定
                listCheckStateTwo.add(ticketModelList.get(i));
            }else if (ticketModelList.size() > 0 && ticketModelList.get(i).getCheckState() != null && ticketModelList.get(i).getCheckState().equals("3")) {//已拒绝
                listCheckStateThree.add(ticketModelList.get(i));
            }
        }
        newList.add(sortByStatus(listCheckStateZero));
        newList.add(sortByStatus(listCheckStateTwo));
        newList.add(sortByStatus(listCheckStateThree));
        return listByNew(newList);
    }


    /**
     * 按level排序TicketModel
     */
    public List<TicketModel> sortByLevel(List<TicketModel> ticketModelList) {
        List<TicketModel> listLevelThree = new ArrayList<TicketModel>();
        List<TicketModel> listLevelTwo = new ArrayList<TicketModel>();
        List<TicketModel> listLevelOne = new ArrayList<TicketModel>();
        List<List<TicketModel>> newList = new ArrayList<List<TicketModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.get(i).getLevel() != null && ticketModelList.get(i).getLevel().equals("PL03")) {//危机
                listLevelThree.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getLevel() != null && ticketModelList.get(i).getLevel().equals("PL02")) {//紧急
                listLevelTwo.add(ticketModelList.get(i));
            } else {//一般
                listLevelOne.add(ticketModelList.get(i));
            }
        }
        newList.add(sortByStatus(listLevelThree));
        newList.add(sortByStatus(listLevelTwo));
        newList.add(sortByStatus(listLevelOne));
        return listByNew(newList);
    }

    /**
     * 按state排序TicketModel
     */
    public List<TicketModel> sortByStatus(List<TicketModel> ticketModelList) {
        // 待派遣TS01
        List<TicketModel> listStatusOne = new ArrayList<TicketModel>();
        // 抢单中TS00
        List<TicketModel> listStatusTwo = new ArrayList<TicketModel>();
        // 已派遣TS03
        List<TicketModel> listStatusThree = new ArrayList<TicketModel>();
        // 已报价TS30
        List<TicketModel> listStatusFour = new ArrayList<TicketModel>();
        // 已接单TS04
        List<TicketModel> listStatusFive = new ArrayList<TicketModel>();
        // 已预约TS02
        List<TicketModel> listStatusSix = new ArrayList<TicketModel>();
        // 已挂单TS21
        List<TicketModel> listStatusSeven = new ArrayList<TicketModel>();
        // 已上门TS07
        List<TicketModel> listStatusEight = new ArrayList<TicketModel>();
        // 已确认报价TS31
        List<TicketModel> listStatusNine = new ArrayList<TicketModel>();
        // 已结单TS09
        List<TicketModel> listStatusTen = new ArrayList<TicketModel>();
        // 电话完成TS19
        List<TicketModel> listStatusEleven = new ArrayList<TicketModel>();
        // 已评价
        List<TicketModel> listStatusTwelve = new ArrayList<TicketModel>();
        // 已回访TS11
        List<TicketModel> listStatusThreeteen = new ArrayList<TicketModel>();
        // 取消工单TS20
        List<TicketModel> listStatusFourteen = new ArrayList<TicketModel>();
        //其他
        List<TicketModel> listStatusFiveteen = new ArrayList<TicketModel>();
        List<List<TicketModel>> list = new ArrayList<List<TicketModel>>();
        for (int i = 0; i < ticketModelList.size(); i++) {
            if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS01")) {// 待派遣TS01
                listStatusOne.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS00")) {// 抢单中TS00
                listStatusTwo.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS03")) {// 已派遣TS03
                listStatusThree.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS30")) {// 已报价TS30
                listStatusFour.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS04")) {// 已接单TS04
                listStatusFive.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS02")) {// 已预约TS02
                listStatusSix.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS21")) {// 已挂单TS21
                listStatusSeven.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS07")) {// 已上门TS07
                listStatusEight.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS31")) {// 已确认报价TS31
                listStatusNine.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS09")) {// 已结单TS09
                listStatusTen.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS19")) {// 电话完成TS19
                listStatusEleven.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS08")) {// 已评价
                listStatusTwelve.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS11")) {// 已回访TS11
                listStatusThreeteen.add(ticketModelList.get(i));
            } else if (ticketModelList.get(i).getStatus() != null && ticketModelList.get(i).getStatus().equals("TS20")) {// 取消工单TS20
                listStatusFourteen.add(ticketModelList.get(i));
            } else {//其他
                listStatusFiveteen.add(ticketModelList.get(i));
            }
        }
        list.add(listStatusOne);
        list.add(listStatusTwo);
        list.add(listStatusThree);
        list.add(listStatusFour);
        list.add(listStatusFive);
        list.add(listStatusSix);
        list.add(listStatusSeven);
        list.add(listStatusEight);
        list.add(listStatusNine);
        list.add(listStatusTen);
        list.add(listStatusEleven);
        list.add(listStatusTwelve);
        list.add(listStatusThreeteen);
        list.add(listStatusFourteen);
        list.add(listStatusFiveteen);
        return listByNew(list);
    }


    //组合TicketModel
    public List<TicketModel> listByNew(List<List<TicketModel>> list) {
        List<TicketModel> newList = new ArrayList<TicketModel>();
        for (int i = 0; i < list.size(); i++) {
            List<TicketModel> ticketModels = list.get(i);
            for (int j = 0; j < ticketModels.size(); j++) {
                newList.add(ticketModels.get(j));
            }
        }
        return newList;
    }


}

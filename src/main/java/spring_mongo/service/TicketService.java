package spring_mongo.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import spring_mongo.model.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guxiaowei on 2017/7/3.
 */
public interface TicketService {
    /**
     * 查询工单
     *
     * @return
     */
    public List<TicketListModel> findAll(Integer pageNo, Integer pageSize);

    //通过工单id 查找工单
    public List<TicketModel> findTicketById(String ticketId);

    //通过 参数 查到工程师
    public List<UserListModel> findUserByTicket(String projeect, String area, String type);
   //抢单派遣
    public List<UserListModel> findUserByTicketAndOther( String area, String fix, String state, String type);

    public TicketModel updateCheckState(String ticketId, String checkState);

    public void updatePushState(String ticketId, String pushState);

    public void updateArriveState(String ticketId, String arriveState);

    public void updateModState(String ticketId, String modState);

    public void updateAppointmentState(String ticketId, String appointmentState);
    public void updateReorderState(String ticketId, String reorderState);


    /**
     * 新增工单
     *
     * @param ticket
     */
    public void insertTicket(TicketModel ticket);

    /**
     * 协同单录入
     * @param ticketModel
     */
    public TicketModel cooperateToAdd(TicketModel ticketModel);

    /**
     * 删除工单
     *
     * @param ticketName
     */
    public void removeTicket(String ticketName);

    public List<TicketModel> getListByEngineer(String engineer);

    /**
     * 首页聚合测试接口
     * @param
     * @return
     */
    public Integer getTS01Ticket(List<String> area,String status);

 public Integer getAllStatusTicket(List<String> area);
 public Integer getAllFinishTicket(List<String> area);
 public Integer getAllBadTicket(List<String> area);
    public Integer getAllDoingTicket(String engineer);
    public TicketModel updateTicketByYuYue(String ticketID);
    public List<TicketModel> findBycooperateState(String state,String status);

    /**
     * 根据主键获取工程师
     *
     * @param engineer
     * @return
     */
    public EngineerModel getEngineerByID(String engineer);

    public TicketModel updateTicket(String id, String engineer);

    public TicketModel getByID(String id);

    public TicketModel findDocumentById(String id);

    /**
     * 根据工程主管的项目获取相关的工单
     *
     * @return
     */
    public List<TicketListModel> getListByManagerID(String engineerManager, Integer pageNo, Integer pageSize);

    /**
     * 根据工单号查询相关工程师
     *
     * @return
     */
    public List<EngineerModel> getEngineerByTicketID(String ticketID);

    public TicketModel updateTicketByPart(String ticketID, PartModel partModel);

    public TicketModel getTicketByNo(String no);

    public TicketListModel getTicketByTicketNo(String no);

    /**
     * 根据工程主管查询其管辖区域内的所有工程师
     *
     * @return
     */
    public List<EngineerModel> getEngineerByManagerID(String engineerManager);

    /**
     * 按条件查询
     *
     * @param ticketName
     * @return
     */
    public List<TicketListModel> findForRequery(String engineer, String ticketName, Integer pageNo, Integer pageSize);

    //筛选
    public List<TicketListModel> findForFind(TicketModel ticketListModel);
    public List<TicketListModel> getNumByKeyValue( String user ,String key ,String value );

    public List<TicketListModel> findForLu(TicketModel ticketListModel, Integer pageNo, Integer pageSize);

    public List<TicketModel> ScanneTicket(TicketModel ticketListModel, Integer pageNo, Integer pageSize);


    /**
     * mongodb简单的分组查询
     *
     * @return
     */
    public BasicDBList mongoGroup();

    public void saveData(DBObject obj);

    public List<TicketListModel> getListByNameOrPhone(String engineer, String nameAndPhone, Integer pageNo, Integer pageSize);

    List<TicketModelList> findByPhone(String phone, String engineerManager);


    void saveTickets(TicketModel ticketModel);

    void saveSingleSupplement(SingleSupplementModel singleSupplementModel);

    List<TicketModel> getPhoneList(String phone, String engineerManager);

    /**
     * 补单
     *
     * @return
     */
    public TicketModel updateTicketByReminder(String ticketID, HistoryModel historyModel);
    /**
     * 改派遣
     * @return
     */
    public TicketModel updateTicketByReverse(String ticketID,HistoryModel historyModel);

    /**
     * 改协同
     * @return
     */
    public TicketModel updateTicketByCooperate(String ticketID,HistoryModel historyModel,List<String> viceManagerID,List<String> vice_no,List<String> vice_id);

    /**
     * 补单列表
     *
     * @param engineerManager
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<TicketModel> getListByFrom(String engineerManager, Integer pageNo, Integer pageSize);


    /**
     * 查询所有父类物料
     *
     * @return
     */
    List<MaterielModel> getMaterielParent();

    /**
     * 查询子类物料集合
     *
     * @param parentID
     * @return
     */
    List<MaterielModel> getMaterielListByParentID(String parentID);


    /**
     * 根据工单号更新工单的状态
     *
     * @param ticketID,checkState
     * @return
     */
    public List<TicketModel> auditTaskIime(String[] ticketID, String[] checkState);

    /**
     * 根据工单号查询工单
     *
     * @param ticketID
     * @return
     */
    public List<TicketModel> findByNo(String ticketID);

    public List<TicketModel> findByStateAndPushState(String state, String pushState);

    public List<TicketModel> findByStateAndArriveState(String state, String arriveState);

    /**
     * 根据工单号查询服务细则
     *
     * @return
     */
    public TicketServiceModel getServiceListByTicketID(String ticketID);

    public List<TicketModel> findByStateAndModState(String state, String modState);

    public List<TicketModel> findByStateAndAppointmentState(String state, String appointmentState);

    public List<TicketModel> findByFromAndReorderState(String state, String reorderState);

    public List<TicketModel> findByState(String state);

    public List<TicketModel> findByPushState(String pushState);


    /**
     * 根据工单号修改工单的核定工时
     *
     * @param
     * @return
     */
    public List<TicketModel> editEngineerTaskIime(String ticketNo, int[] checkTaskTime, String[] fixType);
    public List<TicketModel> updateRatifyTaskTime(String ticketNo,int[] checkTaskTime,String[] fixType);

    /**
     * 更新工单  按字段
     *
     * @param ticketId
     * @param key
     * @param value
     */
    public void updateTicketByKey(String ticketId, String key, String value);
    public void updateTicketByKeyDate(String ticketId, String key, Date value);

    public List<TicketModel> findTimeOutTickets(List<TicketModel> ticketModelList);

    public List<TicketListModel> getListByEngineerAndNameOrPhone(String engineer, String nameAndPhone, String enterId, Integer pageNo, Integer pageSize);

    //工时审核筛选
    public List<TicketModel> timeTaskCheck(TicketModel ticketListModel, Integer pageNo, Integer pageSize);

    List<TicketModel> getTicketsLu(String enterId, Integer pageNo, Integer pageSize);

    public Model getIdsJob(String loginManId);

    TicketModel saveAppointmentTicket(String ticketID, HangUp hangUp, HistoryModel historyModel);

    List<TicketModel> isOrderproces(String engineerId);

    TicketModel refuseApplication(String no,String checkState,String refuseReason);
}

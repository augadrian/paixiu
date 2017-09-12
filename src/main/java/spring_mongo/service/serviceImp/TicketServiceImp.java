package spring_mongo.service.serviceImp;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.TicketDao;
import spring_mongo.model.*;
import spring_mongo.service.TicketService;

import java.util.Date;
import java.util.List;

/**
 * Created by guxiaowei on 2017/7/3.
 */
@Service
@Transactional
public class TicketServiceImp implements TicketService {
    @Autowired
    private TicketDao ticketDao;

    @Override
    public List<TicketListModel> findAll(Integer pageNo, Integer pageSize) {
        return ticketDao.findAll(pageNo, pageSize);
    }

    @Override
    public void insertTicket(TicketModel ticket) {

    }

    @Override
    public TicketModel cooperateToAdd(TicketModel ticketModel) {
        return ticketDao.cooperateToAdd(ticketModel);
    }

    @Override
    public void removeTicket(String ticketName) {

    }

    @Override
    public List<TicketModel> getListByEngineer(String engineer) {
        return ticketDao.getListByEngineer(engineer);
    }

    @Override
    public Integer getTS01Ticket(List<String> area,String status) {
        return ticketDao.getTS01Ticket(area,status);
    }

    public Integer getAllStatusTicket(List<String> area){
        return ticketDao.getAllStatusTicket(area);
    }
    public Integer getAllFinishTicket(List<String> area){
        return ticketDao.getAllFinishTicket(area);
    }
    public Integer getAllBadTicket(List<String> area){
        return ticketDao.getAllBadTicket(area);
    }
    public Integer getAllDoingTicket(String engineer){
        return ticketDao.getAllDoingTicket(engineer);
    }
    public TicketModel updateTicketByYuYue(String ticketID){
        return ticketDao.updateTicketByYuYue(ticketID);
    }
    public List<TicketModel> findBycooperateState(String state,String status){
        return ticketDao.findBycooperateState(state,status);
    }

    @Override
    public EngineerModel getEngineerByID(String engineer) {
        return ticketDao.getEngineerByID(engineer);
    }

    @Override
    public TicketModel updateTicket(String id, String engineer) {
        return ticketDao.updateTicket(id, engineer);
    }

    @Override
    public TicketModel getByID(String id) {
        return ticketDao.getByID(id);
    }


    @Override
    public TicketModel findDocumentById(String id) {
        return ticketDao.findDocumentById(id);
    }

    @Override
    public List<TicketListModel> getListByManagerID(String engineerManager, Integer pageNo, Integer pageSize) {
        return ticketDao.getListByManagerID(engineerManager, pageNo, pageSize);
    }

    @Override
    public List<EngineerModel> getEngineerByTicketID(String ticketID) {
        return ticketDao.getEngineerByTicketID(ticketID);
    }

    @Override
    public TicketModel updateTicketByPart(String ticketID, PartModel partModel) {
        return ticketDao.updateTicketByPart(ticketID, partModel);
    }

    @Override
    public TicketModel getTicketByNo(String no) {
        return ticketDao.getTicketByNo(no);
    }

    @Override
    public TicketListModel getTicketByTicketNo(String no) {
        return ticketDao.getTicketByTicketNo(no);
    }


    @Override
    public List<EngineerModel> getEngineerByManagerID(String engineerManager) {
        return ticketDao.getEngineerByManagerID(engineerManager);
    }

    @Override
    public List<TicketListModel> findForRequery(String engineer, String ticketName, Integer pageNo, Integer pageSize) {
        return ticketDao.findForRequery(engineer, ticketName, pageNo, pageSize);
    }

    @Override
    public List<TicketListModel> findForFind(TicketModel ticketListModel) {
        return ticketDao.findForFind(ticketListModel);
    }
    @Override
    public List<TicketListModel> getNumByKeyValue( String user ,String key ,String value ){
        return ticketDao.getNumByKeyValue(user,key,value);
    }

    @Override
    public List<TicketListModel> findForLu(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        return ticketDao.findForLu(ticketListModel, pageNo, pageSize);
    }

    @Override
    public List<TicketModel> ScanneTicket(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        return ticketDao.ScanneTicket(ticketListModel, pageNo, pageSize);
    }

    @Override
    public BasicDBList mongoGroup() {
        return null;
    }

    @Override
    public void saveData(DBObject obj) {

    }

    @Override
    public List<UserListModel> findUserByTicket(String project, String area, String type) {
        return ticketDao.findUserByTicket(project, area, type);
    }

    @Override
    public List<UserListModel> findUserByTicketAndOther(String area, String fix, String state, String type) {
        return ticketDao.findUserByTicketAndOther(area, fix, state, type);
    }

    @Override
    public TicketModel updateCheckState(String ticketId, String checkState) {
        return ticketDao.updateCheckState(ticketId, checkState);
    }

    @Override
    public void updatePushState(String ticketId, String pushState) {
        ticketDao.updatePushState(ticketId, pushState);
    }

    @Override
    public void updateArriveState(String ticketId, String arriveState) {
        ticketDao.updateArriveState(ticketId, arriveState);
    }

    @Override
    public void updateModState(String ticketId, String modState) {
        ticketDao.updateModState(ticketId, modState);
    }

    @Override
    public void updateAppointmentState(String ticketId, String appointmentState) {
        ticketDao.updateAppointmentState(ticketId, appointmentState);
    }@Override
    public void updateReorderState(String ticketId, String reorderState) {
        ticketDao.updateReorderState(ticketId, reorderState);
    }


    @Override
    public List<TicketModel> findTicketById(String ticketId) {
        return ticketDao.findTicketById(ticketId);
    }

    @Override
    public List<TicketListModel> getListByNameOrPhone(String engineer, String nameAndPhone, Integer pageNo, Integer pageSize) {
        return ticketDao.getListByNameOrPhone(engineer, nameAndPhone, pageNo, pageSize);
    }

    @Override
    public List<TicketModelList> findByPhone(String phone, String engineerManager) {
        return ticketDao.findByPhone(phone, engineerManager);
    }


    @Override
    public void saveTickets(TicketModel ticketModel) {
        ticketDao.saveTickets(ticketModel);
    }

    @Override
    public void saveSingleSupplement(SingleSupplementModel singleSupplementModel) {
        ticketDao.saveSingleSupplement(singleSupplementModel);
    }

    @Override
    public List<TicketModel> getPhoneList(String phone, String engineerManager) {
        return ticketDao.getPhoneList(phone, engineerManager);
    }

    @Override
    public TicketModel updateTicketByReminder(String ticketID, HistoryModel historyModel) {
        return ticketDao.updateTicketByReminder(ticketID, historyModel);
    }

    /**
     * 改派遣
     * @return
     */
    public TicketModel updateTicketByReverse(String ticketID,HistoryModel historyModel){
        return ticketDao.updateTicketByReverse(ticketID, historyModel);
    }

    /**
     * 改协同
     * @return
     */
    public TicketModel updateTicketByCooperate(String ticketID,HistoryModel historyModel,List<String> viceManagerID,List<String> vice_no,List<String> vice_id){
        return ticketDao.updateTicketByCooperate(ticketID, historyModel,viceManagerID,vice_no,vice_id);
    }

    @Override
    public List<TicketModel> getListByFrom(String engineerManager, Integer pageNo, Integer pageSize) {
        return ticketDao.getListByFrom(engineerManager, pageNo, pageSize);
    }

    @Override
    public List<MaterielModel> getMaterielParent() {
        return ticketDao.getMaterielParent();
    }

    @Override
    public List<MaterielModel> getMaterielListByParentID(String parentID) {
        return ticketDao.getMaterielListByParentID(parentID);
    }

    /**
     * 根据工单号更新工单的状态
     *
     * @param ticketID,checkState
     * @return
     */
    @Override
    public List<TicketModel> auditTaskIime(String[] ticketID, String[] checkState) {
        return ticketDao.auditTaskIime(ticketID, checkState);
    }

    /**
     * 根据工单号查询工单
     *
     * @param ticketID
     * @return
     */
    @Override
    public List<TicketModel> findByNo(String ticketID) {
        return ticketDao.findByNo(ticketID);
    }

    @Override
    public List<TicketModel> findByStateAndPushState(String state, String pushState) {
        return ticketDao.findByStateAndPushState(state, pushState);
    }

    @Override
    public List<TicketModel> findByStateAndArriveState(String state, String arriveState) {
        return ticketDao.findByStateAndArriveState(state, arriveState);
    }

    @Override
    public TicketServiceModel getServiceListByTicketID(String ticketID) {
        return ticketDao.getServiceListByTicketID(ticketID);
    }

    @Override
    public List<TicketModel> findByStateAndModState(String state, String modState) {
        return ticketDao.findByStateAndModState(state, modState);
    }

    @Override
    public List<TicketModel> findByStateAndAppointmentState(String state, String modState) {
        return ticketDao.findByStateAndAppointmentState(state, modState);
    }

    public List<TicketModel> findByFromAndReorderState(String from, String reorderState) {
        return ticketDao.findByFromAndReorderState(from, reorderState);
    }

    @Override
    public List<TicketModel> findByState(String state) {
        return ticketDao.findByState(state);
    }

    @Override
    public List<TicketModel> findByPushState(String pushState) {
        return ticketDao.findByPushState(pushState);
    }


    /**
     * 根据工单号修改工单的核定工时
     *
     * @param ticketNo,checkTaskTime
     * @return
     */
    @Override
    public List<TicketModel> editEngineerTaskIime(String ticketNo, int[] checkTaskTime, String[] fixType) {
        return ticketDao.editEngineerTaskIime(ticketNo, checkTaskTime, fixType);
    }
    public List<TicketModel> updateRatifyTaskTime(String ticketNo,int[] checkTaskTime,String[] fixType){
        return ticketDao.updateRatifyTaskTime(ticketNo, checkTaskTime, fixType);
    }

    @Override
    public void updateTicketByKey(String ticketId, String key, String value) {
        ticketDao.updateTicketByKey(ticketId, key, value);
    }@Override
    public void updateTicketByKeyDate(String ticketId, String key, Date value) {
        ticketDao.updateTicketByKeyDate(ticketId, key, value);
    }

    @Override
    public List<TicketModel> findTimeOutTickets(List<TicketModel> ticketModelList) {
        return ticketDao.findTimeOutTickets(ticketModelList);
    }

    @Override
    public List<TicketListModel> getListByEngineerAndNameOrPhone(String engineer, String nameAndPhone, String enterId, Integer pageNo, Integer pageSize) {
        return ticketDao.getListByEngineerAndNameOrPhone(engineer, nameAndPhone, enterId, pageNo, pageSize);
    }

    @Override
    public List<TicketModel> timeTaskCheck(TicketModel ticketListModel, Integer pageNo, Integer pageSize) {
        return ticketDao.timeTaskCheck(ticketListModel, pageNo, pageSize);
    }

    @Override
    public List<TicketModel> getTicketsLu(String enterId, Integer pageNo, Integer pageSize) {
        return ticketDao.getTicketsLu(enterId, pageNo, pageSize);
    }

    @Override
    public Model getIdsJob(String loginManId) {
        return ticketDao.getIdsJob(loginManId);
    }

    @Override
    public TicketModel saveAppointmentTicket(String ticketID, HangUp hangUp, HistoryModel historyModel) {
        return ticketDao.saveAppointmentTicket(ticketID, hangUp, historyModel);
    }

    @Override
    public List<TicketModel> isOrderproces(String engineerId) {
        return ticketDao.isOrderproces(engineerId);
    }

    @Override
    public TicketModel refuseApplication(String no, String checkState, String refuseReason) {
        return ticketDao.refuseApplication(no, checkState, refuseReason);
    }


}

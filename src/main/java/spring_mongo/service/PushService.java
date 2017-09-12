package spring_mongo.service;

import spring_mongo.model.PushModel;
import spring_mongo.model.TicketModel;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public interface PushService {
    //通过工单id 查找记录
    public List<PushModel> findPushByUserId(String userId);

    //通过工单id 查找记录
    public PushModel getByUserId(String userId);

    //新增记录
    public void insertPush(PushModel pushModel);

    public PushModel updatePush(String userId, String name, String value);
}

package spring_mongo.dao;

import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.PushModel;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
@Transactional
public interface PushDao {
    //通过用户id  找到设置记录
    public List<PushModel> findPushByUserId(String userId);

    //插入
    public void insertPush(PushModel pushModel);

    //更新
    public PushModel updatePush(String userId, String name, String value);

    //通过用户id  找到设置记录
    public PushModel getByUserId(String userId);
}

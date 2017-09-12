package spring_mongo.service;

import spring_mongo.model.NoticeModel;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */
public interface NoticeService {

    public List<NoticeModel> findNoticeByUserId(String userId);
    public List<NoticeModel> findNoticeByUserIdNo(String userId);
    public List<NoticeModel> findNoticeByUserId(String userId,Integer  pageNo, Integer pageSize);
    public void insert(NoticeModel noticeModel);
    public void updateState(String noticeId,String state);

}

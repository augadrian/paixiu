package spring_mongo.service;

import spring_mongo.model.SysUserModel;

/**
 * Created by guxiaowei on 2017/8/30.
 */
public interface SysUserService {
    public SysUserModel getByID(String id);
}

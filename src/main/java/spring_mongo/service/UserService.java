package spring_mongo.service;

import spring_mongo.model.UserModel;

import java.util.List;

/**
 * Created by qwer on 2017/7/10.
 */
public interface UserService {
    public UserModel updateUser(String id);
    public UserModel getUserById(String id);

}

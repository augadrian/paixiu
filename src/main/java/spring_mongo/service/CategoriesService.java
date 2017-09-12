package spring_mongo.service;

import spring_mongo.model.CategoriesModel;

import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
public interface CategoriesService {

    List<CategoriesModel> getFixTtypeGroup(String engineerManager);

    List<CategoriesModel> getFixTtype(String objectID);
}

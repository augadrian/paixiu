package spring_mongo.model;

import java.io.Serializable;

/**
 * Created by guxiaowei on 2017/7/3.
 */
public class BoardsModel implements Serializable{
    private static final long serialVersionUID = 1L;
    private String action;
    private String api;

    public BoardsModel(String action, String api) {
        super();
        this.action = action;
        this.api = api;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @Override
    public String toString(){
        return "BoardsModel [action=" + action + ", api=" + api + "]";
    }
}

package spring_mongo.model;

import java.io.Serializable;
import java.util.List;

/**
 * 服务细则列表
 * Created by guxiaowei on 2017/7/20.
 */


public class TicketServiceModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int verificate_time;//核定工时
    private int standard_time;//标准工时
    private List<String> verificateTimeDetail;//核定工时数组列表
    private List<String> standardTimeDetail;//标准工时数组列表
    private List<String> name;//服务细则名称
    private List<String> fix_type3;//服务细则记录

    public TicketServiceModel() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getVerificateTimeDetail() {
        return verificateTimeDetail;
    }

    public void setVerificateTimeDetail(List<String> verificateTimeDetail) {
        this.verificateTimeDetail = verificateTimeDetail;
    }

    public List<String> getStandardTimeDetail() {
        return standardTimeDetail;
    }

    public void setStandardTimeDetail(List<String> standardTimeDetail) {
        this.standardTimeDetail = standardTimeDetail;
    }

    public int getVerificate_time() {
        return verificate_time;
    }

    public void setVerificate_time(int verificate_time) {
        this.verificate_time = verificate_time;
    }

    public int getStandard_time() {
        return standard_time;
    }

    public void setStandard_time(int standard_time) {
        this.standard_time = standard_time;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getFix_type3() {
        return fix_type3;
    }

    public void setFix_type3(List<String> fix_type3) {
        this.fix_type3 = fix_type3;
    }
}

package spring_mongo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by guxiaowei on 2017/7/19.
 */
public class SingleSupplementModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String project;//关联项目
    private String area;//关联区域
    private String unit;//关联单元
    private String room;//房间号
    private String customer;//客户名
    private String phone;//客户电话
    private String fix_type_group;//维修类型一级
    private String fix_type;//维修类型二级
    private String content;//维修内容
    private String reorderState;//补单推送状态，0未推送，1已推送
    private String repair_detail;//处理详情
    private String main_engineer;//工程师
    private String status;//工单状态 0 已派遣 1 已接单 2已上门 3已结单 4未接单 5未派遣
    private Date createAt;//创建时间
    private Date assign_time;//派单时间
    private Date accept_time;//接单时间
    private Date arrive_time;//上门时间
    private Date finish_time;//结单时间
    private String grade_memo;//评价内容
    private String from;//下单来源
    private String level;//维修优先级
    private String type;//公共单 客户单 	客户单{TICKET_TYPE_1},公共单{TICKET_TYPE_0},
    private String createTime;//派单时间
    private List<String> vice_engineer;
    private String no;//工单编号
    private Date updateAt;//更新时间
    private Integer valid;//有效标识
    private String createBy;//创建者ID
    private String updateBy;//更新者ID
    private String hang_up;//挂单
    private String revisit;//回访
    private Boolean reverse_flag;//可否直接转单
    private Boolean cooperate_flag;//可否直接协同
    private Boolean is_vip;//是否vip工单
    private String point;//位置
    private Integer payment;//
    private List<String> verificateTimeDetail;//核定工时数组列表
    private List<String> standardTimeDetail;//标准工时数组列表
    private List<String> repair_images;//上传图片列表
    private List<Integer> fix_type3_num;//服务细则数量
    private List<String> parts;//材料列表
    private String name;//材料名称
    private String count;//材料数量
    private List<String> history;//工单操作记录
    private List<String> fix_type3;//服务细则记录
    private int verificate_time;//核定工时
    private int standard_time;//标准工时
    private int service_time;//服务时长
    private String checkState;  //审核状态 筛选时 插入  多条件 ， 拼接  0--待审核  1--审核通过 2--审核未通过
    private String startTime; //开始时间
    private String endTime; // 结束时间
    private String userId;  //我的工单 筛选
    private int grade;  //星级
    private String  abnormal;  // 超时接单 超时上门 差评  ，拼接  0--不查询  1--查询
    private int overTime;   //超时
    private  String managerId;//工程师ID


    public SingleSupplementModel() {
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getReorderState() {
        return reorderState;
    }

    public void setReorderState(String reorderState) {
        this.reorderState = reorderState;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getFix_type3() {
        return fix_type3;
    }

    public void setFix_type3(List<String> fix_type3) {
        this.fix_type3 = fix_type3;
    }

    public List<String> getVice_engineer() {
        return vice_engineer;
    }

    public void setVice_engineer(List<String> vice_engineer) {
        this.vice_engineer = vice_engineer;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRoom() {
        return room;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getRepair_images() {
        return repair_images;
    }

    public void setRepair_images(List<String> repair_images) {
        this.repair_images = repair_images;
    }

    public List<Integer> getFix_type3_num() {
        return fix_type3_num;
    }

    public void setFix_type3_num(List<Integer> fix_type3_num) {
        this.fix_type3_num = fix_type3_num;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFix_type_group() {
        return fix_type_group;
    }

    public void setFix_type_group(String fix_type_group) {
        this.fix_type_group = fix_type_group;
    }

    public String getFix_type() {
        return fix_type;
    }

    public void setFix_type(String fix_type) {
        this.fix_type = fix_type;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRepair_detail() {
        return repair_detail;
    }

    public void setRepair_detail(String repair_detail) {
        this.repair_detail = repair_detail;
    }

    public String getMain_engineer() {
        return main_engineer;
    }

    public void setMain_engineer(String main_engineer) {
        this.main_engineer = main_engineer;
    }


    public String getStatus() {
        return status;

    }
    public void setStatus(String status) {
        this.status = status;}



    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getAssign_time() {
        return assign_time;
    }

    public void setAssign_time(Date assign_time) {
        this.assign_time = assign_time;
    }

    public Date getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(Date accept_time) {
        this.accept_time = accept_time;
    }

    public Date getArrive_time() {
        return arrive_time;
    }

    public void setArrive_time(Date arrive_time) {
        this.arrive_time = arrive_time;
    }

    public Date getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(Date finish_time) {
        this.finish_time = finish_time;
    }

    public String getGrade_memo() {
        return grade_memo;
    }

    public void setGrade_memo(String grade_memo) {
        this.grade_memo = grade_memo;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getHang_up() {
        return hang_up;
    }

    public void setHang_up(String hang_up) {
        this.hang_up = hang_up;
    }

    public String getRevisit() {
        return revisit;
    }

    public void setRevisit(String revisit) {
        this.revisit = revisit;
    }

    public Boolean getReverse_flag() {
        return reverse_flag;
    }

    public void setReverse_flag(Boolean reverse_flag) {
        this.reverse_flag = reverse_flag;
    }

    public Boolean getCooperate_flag() {
        return cooperate_flag;
    }

    public void setCooperate_flag(Boolean cooperate_flag) {
        this.cooperate_flag = cooperate_flag;
    }

    public Boolean getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(Boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public int getVerificate_time() {return verificate_time;}

    public void setVerificate_time(int verificate_time) {this.verificate_time = verificate_time;}

    public int getStandard_time() {return standard_time;}

    public void setStandard_time(int standard_time) {this.standard_time = standard_time;}

    public String getCheckState() {return checkState;}

    public void setCheckState(String checkState) {this.checkState = checkState;}

    public int getService_time() {return service_time;}

    public void setService_time(int service_time) {this.service_time = service_time;}




    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(String abnormal) {
        this.abnormal = abnormal;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOverTime(int overTime) {
        this.overTime = overTime;
    }
    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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

}

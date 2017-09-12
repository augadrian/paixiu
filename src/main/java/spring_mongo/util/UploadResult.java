package spring_mongo.util;

/**
 * User: zhangbin.
 * Date: 2016/11/3.
 * Time: 9:43.
 * desc:
 */
public class UploadResult {
    private Integer status;
    private String statusInfo;
    private String finalFileName;

    public UploadResult() {
    }

    public UploadResult(Integer status, String statusInfo, String finalFileName) {
        this.status = status;
        this.statusInfo = statusInfo;
        this.finalFileName = finalFileName;
    }
    public UploadResult(Integer status, String statusInfo) {
        this.status = status;
        this.statusInfo = statusInfo;
        this.finalFileName = finalFileName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public String getFinalFileName() {
        return finalFileName;
    }

    public void setFinalFileName(String finalFileName) {
        this.finalFileName = finalFileName;
    }
}

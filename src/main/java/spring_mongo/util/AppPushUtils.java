package spring_mongo.util;

/**
 * Created by Administrator on 2017/7/8.
 */

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 个推工具包
 * (peerslee, 2017.2.12)
 */
public class AppPushUtils {
    //定义常量, appId、appKey、masterSecret  在"个推控制台"中获得的应用配置
    // 由IGetui管理页面生成，是您的应用与SDK通信的标识之一，每个应用都对应一个唯一的AppID
    private static String appId = "CVgJ2CCP2Q7hqbxghxEuP5";
    // 预先分配的第三方应用对应的Key，是您的应用与SDK通信的标识之一。
    private static String appKey = "HwQk4a2FbvATQUoM5qyN3A";
    // 个推服务端API鉴权码，用于验证调用方合法性。在调用个推服务端API时需要提供。（请妥善保管，避免通道被盗用）
    private static String masterSecret = "Aej83dCXEd7pmh9YM4jsJ7";
    // 构造器
    public AppPushUtils(String appId, String appKey, String masterSecret) {
        // 初始化类
        this.appId = appId;
        this.appKey = appKey;
        this.masterSecret = masterSecret;
    }
    // 设置通知消息模板
    /*
     * 1. appId
     * 2. appKey
     * 3. 要传送到客户端的 msg
     * 3.1 标题栏：key = title,
     * 3.2 通知栏内容： key = titleText,
     * 3.3 穿透内容：key = transText
     */
    private static NotificationTemplate getNotifacationTemplate(String appId, String appKey, Map<String, String> msg){
        // 在通知栏显示一条含图标、标题等的通知，用户点击后激活您的应用
        NotificationTemplate template = new NotificationTemplate();
        // 设置appid，appkey
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 穿透消息设置为，1 强制启动应用
        template.setTransmissionType(1);
        // 设置穿透内容
        System.out.println(msg.get("title") + "::" + msg.get("titleText") + "::" + msg.get("transText"));
        template.setTransmissionContent(msg.get("transText"));
        // 设置style
        Style0 style = new Style0();
        // 设置通知栏标题和内容
        style.setTitle(msg.get("title"));
        style.setText(msg.get("titleText"));
        // 设置通知，响铃、震动、可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        // 设置
        template.setStyle(style);

        return template;
    }
    // 对单个用户推送消息
    /*
     * 1. cid
     * 2. 要传到客户端的 msg
     * 2.1 标题栏：key = title,
     * 2.2 通知栏内容： key = titleText,
     * 2.3 穿透内容：key = transText
     */
    public IPushResult pushMsgToSingle(String cid, Map<String, String> msg,int time) {
        // 代表在个推注册的一个 app，调用该类实例的方法来执行对个推的请求
        IGtPush push = new IGtPush(appKey, masterSecret);
        // 创建信息模板
        NotificationTemplate template = getNotifacationTemplate(appId, appKey, msg);
        //定义消息推送方式为，单推
        SingleMessage message = new SingleMessage();
        // 设置推送消息的内容
        message.setData(template);

        //有效时间
        message.setOfflineExpireTime(1000 * 60 * time);  //有效时间
        // 设置推送目标
        Target target = new Target();
        target.setAppId(appId);
        // 设置cid
//        target.setClientId(cid);
        target.setAlias(cid);
        // 获得推送结果
        IPushResult result = push.pushMessageToSingle(message, target);
        /*
         * 1. 失败：{result=sign_error}
         * 2. 成功：{result=ok, taskId=OSS-0212_1b7578259b74972b2bba556bb12a9f9a, status=successed_online}
         * 3. 异常
         */
        return result;
    }
    public static void sendGeTui(String title, String titleText, String transText, List<String> cids, int time) {
        Map<String, String> msg = new HashMap<>();
        msg.put("title",title);
//        msg.put("titleText", "O(∩_∩)O~,");
        msg.put("titleText", titleText);
        msg.put("transText", transText); //穿透内容

        AppPushUtils pushUtils = new AppPushUtils(appId, appKey, masterSecret);
        for(String cid : cids) {
            System.out.println("正在发送消息...");
            IPushResult ret =  pushUtils.pushMsgToSingle(cid, msg,time);
            System.out.println(ret.getResponse().toString());
        }

    }

    public static void main(String[] args) {

        Map<String, String> msg = new HashMap<>();
        msg.put("title", "新年快乐, 新年快乐！");
        msg.put("titleText", "O(∩_∩)O~,");
        msg.put("transText", "");

        String []cids = {"5934b5f93ea5e30700161ff8"};

        AppPushUtils pushUtils = new AppPushUtils(appId, appKey, masterSecret);
        for(String cid : cids) {
            System.out.println("正在发送消息...");
            IPushResult ret =  pushUtils.pushMsgToSingle(cid, msg,5);
            System.out.println(ret.getResponse().toString());
        }
    }
}

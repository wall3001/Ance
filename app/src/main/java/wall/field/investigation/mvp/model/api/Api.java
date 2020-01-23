package wall.field.investigation.mvp.model.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.ScoreDetail;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.model.entity.Version;

/**
 * ================================================
 * 存放一些与 API 有关的东西,如请求地址,请求码等
 * <p>
 * Created by MVPArmsTemplate
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface Api {


    // String APP_DOMAIN = "http://172.17.13.68";
   //  String APP_DOMAIN = "http://192.168.1.3:80/";
   // String APP_DOMAIN = "http://120.76.123.164:8080/";
//test
    //String APP_DOMAIN = "http://120.76.123.164:8081/";

    //test 2
  //  String APP_DOMAIN = "http://47.92.237.123:8003/";

    //release
    String APP_DOMAIN = "http://47.92.237.123:8081/";

    String RequestSuccess = "1";


    //登录接口
    @FormUrlEncoded
    @POST("start/login")
    Observable<BaseJson<User>> login(@Field("userName") String username, @Field("password") String password);


    //修改密码
    @FormUrlEncoded
    @POST("start/changePassword")
    Observable<BaseJson<Object>> changePassword(@Field("uid") String uid, @Field("token") String token, @Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword);

    @Multipart
    @POST("start/changePortrait")
    Observable<BaseJson<String>> changePortrait(@PartMap Map<String, RequestBody> files);

    //获取任务清单
    @FormUrlEncoded
    @POST("task/taskList")
    Observable<BaseJson<List<Task>>> getTaskList(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("pageNum") int pageNum, @Field("page") int page, @Field("complete") int complete,@Field("orderType")int orderType,@Field("taskName")String taskName);


    @FormUrlEncoded
    @POST("task/getTaskBaseInfo")
    Observable<BaseJson<TaskBaseInfo>> getTaskBaseInfo(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("taskId") String taskID);

    @FormUrlEncoded
    @POST("task/getScoreList")
    Observable<BaseJson<List<ScoreItem>>> getScoreList(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("taskId") String taskID, @Field("page") int page, @Field("pageNum") int pageNum);

    @FormUrlEncoded
    @POST("start/checkVersion")
    Observable<BaseJson<Version>> checkVersion(@Field("curVersion") String currentVersion);

    @FormUrlEncoded
    @POST("task/deleteScore")
    Observable<BaseJson<Object>> deleteScoreItem(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("taskId") String taskId, @Field("scoreId") String scoreId);

    @FormUrlEncoded
    @POST("task/deleteImage")
    Observable<BaseJson<Object>> deleteImage(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("taskId") String taskId, @Field("scoreId") String scoreId, @Field("imgId") String imgId);

    @FormUrlEncoded
    @POST("task/getScoreDetail")
    Observable<BaseJson<ScoreDetail>> getScoreDetail(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("taskId") String taskId, @Field("scoreId") String scoreId);

    @FormUrlEncoded
    @POST("task/getTemplateDetail")
    Observable<BaseJson<List<TemplateDetail>>> getTemplateDetail(@Field("uid") String currentUid, @Field("token") String currentToken, @Field("templateId") String templateId);

    @Multipart
    @POST("task/submitScore")
    Observable<BaseJson<Object>> submitScore(@PartMap Map<String, RequestBody> bodyMap);

    @Multipart
    @POST("task/updateScoreDetail")
    Observable<BaseJson<Object>> updateScoreDetail(@PartMap Map<String, RequestBody> bodyMap);


    /**
     *  二期新增接口
     */
    @FormUrlEncoded
    @POST("task/updateCheckState")
    Observable<BaseJson<Object>> updateCheckState(@Field("uid") String currentUid, @Field("token") String currentToken,@Field("scoreId")String scoredId,@Field("checkStatus")String checkStatus);


    @FormUrlEncoded
    @POST("task/copyTaskAndMain")
    Observable<BaseJson<Object>> copyTaskAndMain(@Field("uid") String currentUid, @Field("token") String currentToken,@Field("taskId")String taskId,@Field("copyRemark")String copyRemark,@Field("copyLocation")String copyLocation);

    @FormUrlEncoded
    @POST("task/updateCopyTaskRemark")
    Observable<BaseJson<Object>> updateCopyTaskRemark(@Field("uid") String currentUid, @Field("token") String currentToken,@Field("taskId")String taskId,@Field("copyRemark")String copyRemark);


    @Multipart
    @POST("task/submitScoreByMultiple")
    Observable<BaseJson<Object>> submitScoreByMultiple(@PartMap Map<String, RequestBody> bodyMap);

    @Multipart
    @POST("task/updateScoreByMultiple")
    Observable<BaseJson<Object>> updateScoreByMultiple(LinkedHashMap<String,RequestBody> bodyMap);
}

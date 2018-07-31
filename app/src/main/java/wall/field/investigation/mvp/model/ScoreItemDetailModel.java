package wall.field.investigation.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;
import wall.field.investigation.app.utils.JsonHelp;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.mvp.contract.ScoreItemDetailContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.Address;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.LocalImage;
import wall.field.investigation.mvp.model.entity.ScoreDetail;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.model.entity.User;


@ActivityScope
public class ScoreItemDetailModel extends BaseModel implements ScoreItemDetailContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    List<TemplateDetail> templateDetailList;

    @Inject
    public ScoreItemDetailModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseJson<TaskBaseInfo>> getTaskBaseInfo(String taskId) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getTaskBaseInfo(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication), taskId);
    }

    @Override
    public Observable<BaseJson<Object>> deleteScore(String taskId, String scoreId) {
        return mRepositoryManager.obtainRetrofitService(Api.class).deleteScoreItem(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication), taskId, scoreId);
    }

    @Override
    public Observable<BaseJson<Object>> deleteImage(String taskId, String scoreId, String url) {
        return mRepositoryManager.obtainRetrofitService(Api.class).deleteImage(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication), taskId, scoreId, url);
    }

    @Override
    public Observable<BaseJson<ScoreDetail>> getScoreDetail(String taskId, String scoreId) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getScoreDeatail(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication), taskId, scoreId);
    }

    @Override
    public Observable<BaseJson<List<TemplateDetail>>> getTemplateDetail(String templateId) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getTemplateDetail(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication), templateId);
    }

    @Override
    public void saveTemplate(List<TemplateDetail> data) {
        templateDetailList.clear();
        templateDetailList.addAll(data);
    }

    @Override
    public Observable<BaseJson<Object>> submitScore(String taskId, String itemId, String standardId, String deductId, String deductNum, List<LocalImage> data,String address, String location) {
        LinkedHashMap<String, RequestBody> bodyMap = new LinkedHashMap<>();
        //添加基础参数
        //直接传json格式过去
        JSONObject json = new JSONObject();
        JSONArray data2 = new JsonHelp<LocalImage>(LocalImage.class).list2JsonArray(data);
        try {
            json.put("uid", UserUtils.getCurrentUid(mApplication));
            json.put("token", UserUtils.getCurrentToken(mApplication));
            json.put("taskId", taskId);
            json.put("itemId", itemId);
            json.put("standardId", standardId);
            json.put("deductId", deductId);
            json.put("deductNum", deductNum);
            json.put("location", location);
            json.put("address", address);
            json.put("imgList", data2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        bodyMap.put("json", body);
        int j = data.size();
        for (int i = 0; i < j; i++) {
            LocalImage file = data.get(i);
            File uf = new File(file.imgUrl);
            Timber.e("uf == " + uf.getName());
            bodyMap.put("img" + i + "\"; filename=\"" + uf.getName(), RequestBody.create(MediaType.parse("image/*"), uf));
            //  bodyMap.put("audio" + i + "\"; filename=\"" + uf.getName(), RequestBody.create(MediaType.parse("audio/*"), uf));
        }
        return mRepositoryManager.obtainRetrofitService(Api.class).submitScore(bodyMap);
    }

    @Override
    public Observable<BaseJson<Object>> updateScoreDetail(String taskId, String scoreId, String itemId, String standardId, String deductId, String deductNum, List<LocalImage> data,String address,String location) {


        LinkedHashMap<String, RequestBody> bodyMap = new LinkedHashMap<>();
        //添加基础参数
        //直接传json格式过去
        JSONObject json = new JSONObject();
        List<LocalImage> localImages = new ArrayList<>();
        int j = data.size();
        for (int i = 0; i < j; i++) {
            LocalImage image = data.get(i);
            if (!image.isDownLoad) {
                localImages.add(image);
            }
        }
        JSONArray data2 = new JsonHelp<LocalImage>(LocalImage.class).list2JsonArray(localImages);
        try {
            json.put("uid", UserUtils.getCurrentUid(mApplication));
            json.put("token", UserUtils.getCurrentToken(mApplication));
            json.put("taskId", taskId);
            json.put("scoreId", scoreId);
            json.put("itemId", itemId);
            json.put("standardId", standardId);
            json.put("deductId", deductId);
            json.put("deductNum", deductNum);
            json.put("location", location);
            json.put("address",address);
            json.put("imgAddList", data2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        bodyMap.put("json", body);
        int k = localImages.size();
        for (int i = 0; i < k; i++) {
            LocalImage file = localImages.get(i);
            File uf = new File(file.imgUrl);
            Timber.e("uf == " + uf.getName());
            bodyMap.put("img" + i + "\"; filename=\"" + uf.getName(), RequestBody.create(MediaType.parse("image/*"), uf));
            //  bodyMap.put("audio" + i + "\"; filename=\"" + uf.getName(), RequestBody.create(MediaType.parse("audio/*"), uf));
        }
        return mRepositoryManager.obtainRetrofitService(Api.class).updateScoreDetail(bodyMap);
    }

    @Override
    public void saveAddress(String address, String location) {
        Address add = new Address();
        add.address = address;
        add.location = location;
        new StorageUtils<Address>(Address.class,mApplication).save(add);
    }


}
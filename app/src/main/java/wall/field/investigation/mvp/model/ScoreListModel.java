package wall.field.investigation.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.mvp.contract.ScoreListContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;


@ActivityScope
public class ScoreListModel extends BaseModel implements ScoreListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public ScoreListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseJson<TaskBaseInfo>> getTaskBaseInfo(String taskID) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getTaskBaseInfo(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),taskID);
    }

    @Override
    public Observable<BaseJson<List<ScoreItem>>> getScoreList(String taskID, int page, int pageNum) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getScoreList(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),taskID,page,pageNum);
    }

    @Override
    public Observable<BaseJson<Object>> deleteScoreItem(String taskId, String scoreId) {
        return mRepositoryManager.obtainRetrofitService(Api.class).deleteScoreItem(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),taskId,scoreId);
    }

    @Override
    public Observable<BaseJson<Object>> updateCopyTaskRemark(String taskId, String copyRemark) {
        return mRepositoryManager.obtainRetrofitService(Api.class).updateCopyTaskRemark(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),taskId,copyRemark);
    }
}
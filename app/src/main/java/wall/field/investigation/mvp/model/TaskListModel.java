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
import wall.field.investigation.mvp.contract.TaskListContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.Task;


@ActivityScope
public class TaskListModel extends BaseModel implements TaskListContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public TaskListModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseJson<List<Task>>> requestTaskList(boolean pullToRefresh, int pageNum, int page,int complete,int orderType,String taskName) {
        return mRepositoryManager.obtainRetrofitService(Api.class).getTaskList(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),pageNum,page,complete,orderType,taskName);
    }

    @Override
    public Observable<BaseJson<Object>> copyTaskAndMain(String taskId, String copyRemark, String copyLocation) {
        return mRepositoryManager.obtainRetrofitService(Api.class).copyTaskAndMain(UserUtils.getCurrentUid(mApplication),UserUtils.getCurrentToken(mApplication),taskId,copyRemark,copyLocation);
    }
}
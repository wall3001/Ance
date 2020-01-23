package wall.field.investigation.mvp.contract;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;


public interface ScoreListContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void updateBaseInfo(TaskBaseInfo data);
        void startLoadMore();
        void endLoadMore();
        Activity getActivity();
        RecyclerView getRecyclerView();

        void disMissRemarkCpw();
        //申请权限
     //   RxPermissions getRxPermissions();

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<BaseJson<TaskBaseInfo>> getTaskBaseInfo(String taskID);

        Observable<BaseJson<List<ScoreItem>>> getScoreList(String taskID,int page,int pageNum);


        Observable<BaseJson<Object>> deleteScoreItem(String taskId, String scoreId);

        Observable<BaseJson<Object>> updateCopyTaskRemark(String taskId, String copyRemark);
    }
}

package wall.field.investigation.mvp.contract;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.Task;


public interface TaskListContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void startLoadMore();
        void endLoadMore();
        Activity getActivity();
        RecyclerView getRecylerView();
        //申请权限
        RxPermissions getRxPermissions();

        void disMissCopyCpw();
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<BaseJson<List<Task>>> requestTaskList(boolean pullToRefresh,int pageNum,int page,int complete,int orderType,String taskName);

        Observable<BaseJson<Object>> copyTaskAndMain(String taskId, String copyRemark, String copyLocation);
    }
}

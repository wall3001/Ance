package wall.field.investigation.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.Name;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.model.entity.Version;


public interface LoginContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void toTaskListActivity(User user);

        void showNewVersion(Version version);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<BaseJson<User>> login(String name, String password);

        void save(User user);

        Observable<BaseJson<Version>> checkNewVersion();

        void saveName(Name saveName);
    }
}

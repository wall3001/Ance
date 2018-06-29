package wall.field.investigation.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import wall.field.investigation.mvp.contract.ChangePasswordContract;
import wall.field.investigation.mvp.model.ChangePasswordModel;


@Module
public class ChangePasswordModule {
    private ChangePasswordContract.View view;

    /**
     * 构建ChangePasswordModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ChangePasswordModule(ChangePasswordContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ChangePasswordContract.View provideChangePasswordView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ChangePasswordContract.Model provideChangePasswordModel(ChangePasswordModel model) {
        return model;
    }
}
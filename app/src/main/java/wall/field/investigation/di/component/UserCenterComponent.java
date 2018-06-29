package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.UserCenterModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.UserCenterActivity;

@ActivityScope
@Component(modules = UserCenterModule.class, dependencies = AppComponent.class)
public interface UserCenterComponent {
    void inject(UserCenterActivity activity);
}
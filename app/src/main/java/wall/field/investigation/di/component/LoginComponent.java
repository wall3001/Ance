package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.LoginModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.LoginActivity;

@ActivityScope
@Component(modules = LoginModule.class, dependencies = AppComponent.class)
public interface LoginComponent {
    void inject(LoginActivity activity);
}
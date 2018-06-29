package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.ChangePasswordModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.ChangePasswordActivity;

@ActivityScope
@Component(modules = ChangePasswordModule.class, dependencies = AppComponent.class)
public interface ChangePasswordComponent {
    void inject(ChangePasswordActivity activity);
}
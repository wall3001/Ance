package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.WelcomeModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.WelcomeActivity;

@ActivityScope
@Component(modules = WelcomeModule.class, dependencies = AppComponent.class)
public interface WelcomeComponent {
    void inject(WelcomeActivity activity);
}
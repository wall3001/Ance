package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.ScoreListModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.ScoreListActivity;

@ActivityScope
@Component(modules = ScoreListModule.class, dependencies = AppComponent.class)
public interface ScoreListComponent {
    void inject(ScoreListActivity activity);
}
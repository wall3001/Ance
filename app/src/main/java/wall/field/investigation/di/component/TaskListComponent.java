package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.TaskListModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.TaskListActivity;

@ActivityScope
@Component(modules = TaskListModule.class, dependencies = AppComponent.class)
public interface TaskListComponent {
    void inject(TaskListActivity activity);
}
package wall.field.investigation.app;

import org.simple.eventbus.EventBus;

/**
 * ================================================
 * 放置 {@link EventBus} 的 Tag, 便于检索
 *
 * @see <a href="https://github.com/JessYanCoding/MVPArms/wiki#3.5">EventBusTags wiki 官方文档</a>
 * Created by MVPArmsTemplate
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface EventBusTags {


    String TASKID = "TASKID";

    String SCOREID = "SCOREID";

    String ISADD = "ISADD";

    String TEMPLATEID = "TEMPLATEID";

    String SCORERECORDITEMNAME = "SCORERECORDITEMNAME";

    String ADDRESS ="ADDRESS";

    String SAVEUSER = "SAVEUSER";
}

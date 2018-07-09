package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.mvp.model.entity.Task;

/**
 * Created by wall on 2018/6/18 11:01
 * w_ll@winning.com.cn
 */
public class TaskListAdapter extends BaseQuickAdapter<Task, BaseViewHolder> {

    public TaskListAdapter(@Nullable List<Task> data) {
        super(R.layout.item_task_list, data);
    }


    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        super.bindToRecyclerView(recyclerView);
        setEmptyView(R.layout.task_list_empty,recyclerView);
    }

    @Override
    protected void convert(BaseViewHolder helper, Task item) {
        helper.setText(R.id.tv_index, helper.getAdapterPosition() + 1+"")
                .setText(R.id.tv_address, item.address)
                .setText(R.id.tv_name, item.name)
                .setText(R.id.tv_score,getScore(item.totalScore))
                .setText(R.id.tv_time, getPlanTime(item.timeStamp))
                .setText(R.id.tv_state, getState(item));
    }

    private String getScore(String totalScore) {
        if(TextUtils.isEmpty(totalScore)){
            return "";
        }
        if("0".equals(totalScore)){
            return "";
        }
        return  "-"+totalScore;
    }

    private CharSequence getState(Task item) {
        String state = "";
        if (!TextUtils.isEmpty(item.complete)) {
            switch (item.complete) {
                case "0":
                    state = "未开始";
                    break;
                case "1":
                    state = "进行中";
                    break;
                case "2":
                    state = "已提交" + item.scoreNum + "个评分";
                    break;
                default:
                    break;
            }
        }
        return state;
    }

    private String getPlanTime(String timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月dd日 hh:mm");
        return sdf.format(new Date(Long.valueOf(timeStamp)));
    }


}

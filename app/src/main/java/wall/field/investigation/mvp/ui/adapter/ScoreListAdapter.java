package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.ScoreItem;

/**
 * 评分项列表适配器
 * Created by wall on 2018/6/20 10:31
 * w_ll@winning.com.cn
 */
public class ScoreListAdapter extends BaseQuickAdapter<ScoreItem, BaseViewHolder> {


    public ScoreListAdapter(@Nullable List<ScoreItem> data) {
        super(R.layout.item_score_item, data);
    }

    @Override
    public void bindToRecyclerView(RecyclerView recyclerView) {
        super.bindToRecyclerView(recyclerView);
        setEmptyView(R.layout.score_empty,recyclerView);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScoreItem item) {
        helper.setText(R.id.tv_name, item.scoreName)
                .setText(R.id.tv_summary, item.scoreSummary)
                .setText(R.id.tv_score, item.scoreValue)
                .setTextColor(R.id.tv_state,mContext.getResources().getColor(getColor(item.scoreState)))
                .setText(R.id.tv_state, getState(item.scoreState));
    }

    private int getColor(String scoreState) {
       int  color = R.color.white;
        switch (scoreState) {
            case "0":
                color = R.color.txt_sub;
                break;
            case "1":
                color = R.color.txt_sub;
                break;
            case "2":
                color = R.color.green_past;
                break;
            case "3":
                color = R.color.red_btn_bg;
                break;
            default:
                break;
        }
        return  color;
    }

    private String getState(String scoreState) {
        String state ;
        switch (scoreState) {
            case "0":
                state = "未提交";
                break;
            case "1":
                state = "待审核";
                break;
            case "2":
                state = "通过";
                break;
            case "3":
                state = "需修改";
                break;
            default:
                state = "";
                break;
        }
        return state;

    }

}

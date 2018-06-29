package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Deduct;

/**
 * 扣分标准
 * Created by wall on 2018/6/22 15:39
 * w_ll@winning.com.cn
 */
public class DeductAdapter extends BaseQuickAdapter<Deduct,BaseViewHolder> {


    public Deduct getmDeduct() {
        return mDeduct;
    }

    public void setmDeduct(Deduct mDeduct) {
        this.mDeduct = mDeduct;
    }

    private Deduct mDeduct;


    public DeductAdapter(@Nullable List<Deduct> data) {
        super(R.layout.item_item, data);
        if (data != null) {
            int j = data.size();
            for (int i = 0; i < j; i++) {
                if (data.get(i).isSelect) {
                    mDeduct = data.get(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, Deduct item) {
        helper.setText(R.id.content,item.deductName)
                .setVisible(R.id.img,item.isSelect);
    }


}

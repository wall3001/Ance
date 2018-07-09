package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Deduct;
import wall.field.investigation.mvp.model.entity.Standard;

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
                .setTextColor(R.id.content,mContext.getResources().getColor(getColor(item)))
                .setVisible(R.id.img,item.isSelect);
    }

    private Integer getColor(Deduct item) {
        if (item != null && !TextUtils.isEmpty(item.identification) && "1".equals(item.identification)) {
            return R.color.txt_sub;
        }
        return R.color.txt_main;
    }

}

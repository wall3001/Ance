package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.TemplateDetail;

/**
 * 考核项目
 * Created by wall on 2018/6/22 09:55
 * w_ll@winning.com.cn
 */
public class ItemAdapter extends BaseQuickAdapter<TemplateDetail, BaseViewHolder> {


    public TemplateDetail getTemplateDetail() {
        return select;
    }

    public void setSelect(TemplateDetail select) {
        this.select = select;
    }

    private TemplateDetail select;

    public ItemAdapter(@Nullable List<TemplateDetail> data) {
        super(R.layout.item_item, data);
        if (data != null) {
            int j = data.size();
            for (int i = 0; i < j; i++) {
                if (data.get(i).isSelect) {
                    select = data.get(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, TemplateDetail item) {
        helper.setText(R.id.content, item.itemName)
                .setVisible(R.id.img, item.isSelect);
    }


}

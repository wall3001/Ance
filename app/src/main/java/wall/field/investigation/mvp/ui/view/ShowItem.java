package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.widget.CustomPopupWindow;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.model.entity.Version;
import wall.field.investigation.mvp.ui.adapter.ItemAdapter;

/**
 * 显示考核项目
 * Created by wall on 2018/6/20 14:34
 * w_ll@winning.com.cn
 */
public class ShowItem {

    private static ShowItem instance;
    private CustomPopupWindow customPopupWindow;

    private ShowItem() {
    }

    public void release() {
        if (customPopupWindow != null) {
            customPopupWindow.dismiss();
            customPopupWindow = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    public static ShowItem getInstance() {
        if (instance == null) {
            instance = new ShowItem();
        }
        return instance;
    }

    public interface CallBack {
        void updateItem(TemplateDetail templateDetail);
    }


    public void ShowItem(Context ctx, List<TemplateDetail> list, View parentView, ShowItem.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_show_item, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    RecyclerView mRecyclerView = contentView.findViewById(R.id.mRecyclerView);
                    Button save = contentView.findViewById(R.id.btn_save);
                    ItemAdapter adapter = new ItemAdapter(list);
                    ImageView quit = contentView.findViewById(R.id.quit);
                    quit.setOnClickListener(v -> {
                        popupWindow.dismiss();
                    });
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        TemplateDetail detail = (TemplateDetail) adapter1.getItem(position);
                        if (detail != null && !detail.isSelect) {
                            int j = list.size();
                            for (int i = 0; i < j; i++) {
                                TemplateDetail d = list.get(i);
                                d.isSelect = false;
                            }
                            detail.isSelect = true;
                            adapter.setSelect(detail);
                        }
                        adapter.notifyDataSetChanged();
                    });
                    save.setOnClickListener(v -> {
                        if (callBack != null) {
                            TemplateDetail detail = adapter.getTemplateDetail();
                            if(detail!=null){
                                callBack.updateItem(detail);
                                popupWindow.dismiss();
                            }else{
                                ArmsUtils.snackbarText("请选择一条考核项目");
                            }
                        }

                    });
                }).build();
        customPopupWindow.show();

    }


}

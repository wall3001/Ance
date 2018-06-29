package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.widget.CustomPopupWindow;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Standard;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.ui.adapter.ItemAdapter;
import wall.field.investigation.mvp.ui.adapter.StandardAdapter;

/**
 * 显示考核标准
 * Created by wall on 2018/6/20 14:34
 * w_ll@winning.com.cn
 */
public class ShowStandard {

    private static ShowStandard instance;
    private CustomPopupWindow customPopupWindow;

    private ShowStandard() {
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

    public static ShowStandard getInstance() {
        if (instance == null) {
            instance = new ShowStandard();
        }
        return instance;
    }

    public interface CallBack {
        void updateStandard(Standard standard);
    }


    public void ShowStandard(Context ctx, List<Standard> list, View parentView, ShowStandard.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_show_item, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    RecyclerView mRecyclerView = contentView.findViewById(R.id.mRecyclerView);
                    Button save = contentView.findViewById(R.id.btn_save);
                    StandardAdapter adapter = new StandardAdapter(list);
                    TextView title = contentView.findViewById(R.id.title);
                    ImageView quit = contentView.findViewById(R.id.quit);
                    title.setText(ctx.getString(R.string.standard_title));
                    quit.setOnClickListener(v -> {
                        popupWindow.dismiss();
                    });
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        Standard detail = (Standard) adapter1.getItem(position);
                        if (detail != null && !detail.isSelect) {
                            int j = list.size();
                            for (int i = 0; i < j; i++) {
                                Standard d = list.get(i);
                                d.isSelect = false;
                            }
                            detail.isSelect = true;
                            adapter.setmStandard(detail);
                        }
                        adapter.notifyDataSetChanged();
                    });
                    save.setOnClickListener(v -> {
                        if (callBack != null) {
                            callBack.updateStandard(adapter.getmStandard());
                        }
                        popupWindow.dismiss();
                    });
                }).build();
        customPopupWindow.show();

    }


}

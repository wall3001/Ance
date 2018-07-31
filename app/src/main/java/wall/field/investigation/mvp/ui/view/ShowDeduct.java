package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.widget.CustomPopupWindow;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Deduct;
import wall.field.investigation.mvp.model.entity.Standard;
import wall.field.investigation.mvp.ui.adapter.DeductAdapter;
import wall.field.investigation.mvp.ui.adapter.StandardAdapter;

/**
 * 显示扣分标准
 * Created by wall on 2018/6/20 14:34
 * w_ll@winning.com.cn
 */
public class ShowDeduct {

    private static ShowDeduct instance;
    private CustomPopupWindow customPopupWindow;

    private ShowDeduct() {
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

    public static ShowDeduct getInstance() {
        if (instance == null) {
            instance = new ShowDeduct();
        }
        return instance;
    }

    public interface CallBack {
        void updateDeduct(Deduct deduct);
    }


    public void ShowDeduct(Context ctx, List<Deduct> list, View parentView, ShowDeduct.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_show_item, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    RecyclerView mRecyclerView = contentView.findViewById(R.id.mRecyclerView);
                    Button save = contentView.findViewById(R.id.btn_save);
                    DeductAdapter adapter = new DeductAdapter(list);
                    TextView title = contentView.findViewById(R.id.title);
                    ImageView quit = contentView.findViewById(R.id.quit);
                    title.setText(ctx.getString(R.string.deduct_title));
                    quit.setOnClickListener(v -> popupWindow.dismiss());
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        Deduct detail = (Deduct) adapter1.getItem(position);
                        if (detail != null && !detail.isSelect) {
                            int j = list.size();
                            for (int i = 0; i < j; i++) {
                                Deduct d = list.get(i);
                                d.isSelect = false;
                            }
                            detail.isSelect = true;
                            adapter.setmDeduct(detail);
                        }
                        adapter.notifyDataSetChanged();
                    });
                    save.setOnClickListener(v -> {
                        if (callBack != null) {
                            Deduct deduct = adapter.getmDeduct();
                            if(deduct!=null){
                                callBack.updateDeduct(deduct);
                                popupWindow.dismiss();
                            }else{
                                ArmsUtils.snackbarText("请选择一条扣分标准");
                            }
                        }
                    });
                }).build();
        customPopupWindow.show();

    }


}

package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jess.arms.widget.CustomPopupWindow;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.model.entity.Version;

/**
 * Created by wall on 2018/6/20 14:34
 * w_ll@winning.com.cn
 */
public class ShowDelete {

    private static ShowDelete instance;
    private CustomPopupWindow customPopupWindow;

    private ShowDelete() {
    }

    public void release() {
        if (customPopupWindow != null) {
            customPopupWindow.dismiss();
            customPopupWindow = null;
        }
        if(instance!=null){
            instance = null;
        }
    }

    public static ShowDelete getInstance() {
        if (instance == null) {
            instance = new ShowDelete();
        }
        return instance;
    }

    public interface CallBack {
        void delete( );
    }


    public void ShowDelete(Context ctx, View parentView,String topTitle, ShowDelete.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_delete, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    TextView title = contentView.findViewById(R.id.tv_title);
                    TextView content = contentView.findViewById(R.id.tv_content);
                    TextView cancel = contentView.findViewById(R.id.tv_cancel);
                    TextView sure = contentView.findViewById(R.id.tv_sure);
                    title.setText(topTitle);
                    content.setText(ctx.getString(R.string.delete_score_tips));
                    cancel.setOnClickListener(v -> popupWindow.dismiss());
                    sure.setOnClickListener(v -> {
                        if (callBack != null) {
                            callBack.delete();
                            popupWindow.dismiss();
                        }
                    });
                }).build();
        customPopupWindow.show();

    }


}

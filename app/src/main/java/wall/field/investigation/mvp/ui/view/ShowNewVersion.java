package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jess.arms.widget.CustomPopupWindow;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.Version;

/**
 * Created by wall on 2018/6/20 14:34
 * w_ll@winning.com.cn
 */
public class ShowNewVersion {

    private static ShowNewVersion instance;
    private CustomPopupWindow customPopupWindow;

    private ShowNewVersion() {
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

    public static ShowNewVersion getInstance() {
        if (instance == null) {
            instance = new ShowNewVersion();
        }
        return instance;
    }

    public interface CallBack {
        void startLoad();
    }


    public void ShowNewVersion(Context ctx, Version version, View parentView, ShowNewVersion.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_show_new, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    TextView title = contentView.findViewById(R.id.tv_title);
                    TextView content = contentView.findViewById(R.id.tv_content);
                    TextView cancel = contentView.findViewById(R.id.tv_cancel);
                    TextView sure = contentView.findViewById(R.id.tv_sure);
                    title.setText(String.format(ctx.getString(R.string.format_find_new_version), version.version));
                    content.setText(version.info);
                    cancel.setOnClickListener(v -> popupWindow.dismiss());
                    sure.setOnClickListener(v -> {
                        if (callBack != null) {
                            callBack.startLoad();
                            popupWindow.dismiss();
                        }
                    });
                }).build();
        customPopupWindow.show();

    }


}

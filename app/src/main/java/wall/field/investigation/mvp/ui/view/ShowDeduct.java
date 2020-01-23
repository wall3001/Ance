package wall.field.investigation.mvp.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.widget.CustomPopupWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

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

        void updateDeductMultiple(List<Deduct> list);

        void hideSoftKeyboard();

    }


    public void ShowDeduct(Context ctx, boolean isAdd, String isMultipleThird, List<Deduct> list, View parentView, ShowDeduct.CallBack callBack) {
        customPopupWindow = CustomPopupWindow.builder().contentView(LayoutInflater.from(ctx).inflate(R.layout.ppw_show_item, null))
                .isWrap(false)
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(parentView)
                .customListener((contentView, popupWindow) -> {
                    RecyclerView mRecyclerView = contentView.findViewById(R.id.mRecyclerView);
                    Button save = contentView.findViewById(R.id.btn_save);
                    DeductAdapter adapter = new DeductAdapter(list, isMultipleThird);
                    TextView title = contentView.findViewById(R.id.title);
                    ImageView quit = contentView.findViewById(R.id.quit);
                    title.setText(ctx.getString(R.string.deduct_title));
                    boolean isMultiple = !TextUtils.isEmpty(isMultipleThird) && isMultipleThird.equals("1") && isAdd;
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                    mRecyclerView.setAdapter(adapter);
                    View foot = LayoutInflater.from(ctx).inflate(R.layout.foot_deduct, null);
                    EditText editName = foot.findViewById(R.id.edit_name);
                    EditText editScore = foot.findViewById(R.id.edit_score);
                    TextView tvAdd = foot.findViewById(R.id.tv_add);
                    quit.setOnClickListener(v -> {
                        popupWindow.dismiss();
                    });
                    tvAdd.setOnClickListener(v -> {
                        if (editName.getText().toString().trim().length() > 0 && editScore.getText().toString().trim().length() > 0) {
                            Deduct deduct = new Deduct();
                            deduct.deductId = "0";
                            deduct.deductName = editName.getText().toString();
                            deduct.score = editScore.getText().toString();
                            adapter.addData(deduct);
                            adapter.notifyDataSetChanged();
                            ArmsUtils.snackbarText("添加成功");
                            editName.setText(null);
                            editScore.setText(null);
                        } else {
                            ArmsUtils.snackbarText("请输入扣分标准/扣分分值");
                        }
                    });
                    adapter.addFooterView(foot);
                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                        Deduct detail = (Deduct) adapter1.getItem(position);
                        if (isMultiple) {
                            //多选
                            if (detail != null) {
                                detail.isSelect = !detail.isSelect;
                                adapter.setDeduct(detail);
                            }
                        } else {
                            //单选
                            if (detail != null && !detail.isSelect) {
                                int j = list.size();
                                for (int i = 0; i < j; i++) {
                                    Deduct d = list.get(i);
                                    d.isSelect = false;
                                }
                                detail.isSelect = true;
                                adapter.setDeduct(detail);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    });
                    save.setOnClickListener(v -> {
                        if (callBack != null) {
                            Deduct deduct = adapter.getDeduct();
                            if (deduct != null) {
                                if (isMultiple) {
                                    //多选
                                    List<Deduct> selectedList = new ArrayList<>();
                                    for (Deduct d : list) {
                                        if (d.isSelect) {
                                            selectedList.add(d);
                                        }
                                    }
                                    callBack.updateDeductMultiple(selectedList);
                                } else {
                                    //单选
                                    callBack.updateDeduct(deduct);
                                }
                                popupWindow.dismiss();
                            } else {
                                ArmsUtils.snackbarText("请选择一条扣分标准");
                            }
                        }
                    });
                    popupWindow.setOnDismissListener(() -> {
                        if(callBack!=null){
                            callBack.hideSoftKeyboard();
                        }
                    });
                }).build();
        customPopupWindow.show();

    }


}

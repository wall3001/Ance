package wall.field.investigation.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.utils.ArmsUtils;

import java.util.List;

import wall.field.investigation.R;
import wall.field.investigation.mvp.model.entity.LocalImage;

/**
 * Created by wall on 2018/6/21 14:57
 * w_ll@winning.com.cn
 */
public class ImageAdapter extends BaseQuickAdapter<LocalImage, BaseViewHolder> {

    private AppComponent mAppComponent;
    private ImageLoader mImageLoader;
    private ImageView imageView;

    public ImageAdapter(@Nullable List<LocalImage> data) {
        super(R.layout.item_img, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalImage item) {
        if (mAppComponent == null) {
            //可以在任何可以拿到 Context 的地方,拿到 AppComponent,从而得到用 Dagger 管理的单例对象
            mAppComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
            mImageLoader = mAppComponent.imageLoader();
        }
        //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
        imageView = helper.getView(R.id.img);
        helper.addOnClickListener(R.id.delete)
                .setVisible(R.id.delete, item.showDelete);
        mImageLoader.loadImage(mContext,
                ImageConfigImpl
                        .builder()
                        .url(item.imgUrl)
                        .imageView(imageView)
                        .build());
        helper.itemView.setOnLongClickListener(v -> {
            item.showDelete = !item.showDelete;
            notifyItemChanged(helper.getPosition());
            return true;
        });
    }

    public void onRelease() {
        if (mImageLoader != null && mAppComponent != null && imageView != null) {
            mImageLoader.clear(mAppComponent.application(), ImageConfigImpl.builder()
                    .imageViews(imageView)
                    .build());
        }
    }

}

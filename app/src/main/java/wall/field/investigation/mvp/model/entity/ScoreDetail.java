package wall.field.investigation.mvp.model.entity;

import java.util.List;

/**
 * 评分记录项详情
 * Created by wall on 2018/6/21 16:53
 * w_ll@winning.com.cn
 */
public class ScoreDetail implements Cloneable {

    public String scoreName;
    //	0：未提交1：待审核2:通过 3：需修改
    public String scoreState;
    public String itemId;
    public String itemName;
    public String standardId;
    public String standardName;
    public String deductId;
    public String deductName;
    public String deductNum;
    public String deductValue;
    public String percent; //上浮百分比
    public String scoreLimit; //扣分上限
    public String deductOnce;//一次扣分值
    public List<ImageBean> imgList;
    public String location;//定位地址
    public String address;//简单地址
    public String longitude = "0";
    public String latitude = "0";
    public String curLocation;
    public String curLongitude = "0";
    public String curLatitude = "0";


    /**
     * 二期新增
     */
    public String checkStatus;//首查=0, 复查=1, 复查_已整改=2, 复查_未整改=3

    public String isMultipleThird;//多选三级扣分项目 1：可以，0不可以

    public List<Deduct> deductList;


    @Override
    public Object clone() {
        ScoreDetail o = null;
        try {
            o = (ScoreDetail) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;

    }
}

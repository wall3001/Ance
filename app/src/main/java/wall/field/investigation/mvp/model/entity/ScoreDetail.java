package wall.field.investigation.mvp.model.entity;

import java.util.List;

/**
 * 评分记录项详情
 * Created by wall on 2018/6/21 16:53
 * w_ll@winning.com.cn
 */
public class ScoreDetail {

    public String scoreName;
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
    public String location;

}

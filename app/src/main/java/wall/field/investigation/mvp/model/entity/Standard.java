package wall.field.investigation.mvp.model.entity;

import java.util.List;

/**
 * 考核标准
 * Created by wall on 2018/6/22 14:40
 * w_ll@winning.com.cn
 */
public class Standard implements Cloneable {

    public boolean isSelect;
    public String standardId;
    public String standardName;
    public String scoreLimit;
    public String percent;
    public String identification;
    public List<Deduct> deductList;
    /**
     *  二期新增:多选三级扣分项目 1：可以，0不可以
     *
     */
    public String isMultipleThird;

    @Override
    public Object clone() {
        Standard o = null;
        try {
            o = (Standard) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}

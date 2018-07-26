package wall.field.investigation.mvp.model.entity;

/**
 * 扣分标准
 * Created by wall on 2018/6/22 14:42
 * w_ll@winning.com.cn
 */
public class Deduct implements Cloneable{

    public boolean isSelect;
    public String deductId;
    public String deductName;
    public String score;
    public String identification;

    @Override
    public Object clone()  {
        Deduct o = null;
        try {
            o = (Deduct) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;

    }
}

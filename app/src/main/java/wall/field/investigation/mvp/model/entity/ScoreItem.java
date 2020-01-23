package wall.field.investigation.mvp.model.entity;

/**
 * 评分项
 * Created by wall on 2018/6/20 10:16
 * w_ll@winning.com.cn
 */
public class ScoreItem {

    public String scoreId;
    public String scoreName;
    public String scoreValue;
    public String scoreSummary;
    public String scoreState; //0:通过1：需修改2：待审核
    public String checkStatus;//首查=0, 复查=1, 复查_已整改=2, 复查_未整改=3

}

package per.qiang.spider.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
public class DailyTradeStock implements Serializable {

    private static final long serialVersionUID = -6400847328453979300L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private Double todayClose;

    private Double todayOpen;

    private Double highPrice;

    private Double updownRate;

    private Double updownPrice;

    private Double lowPrice;

    private Double yestodayClose;

    private Double turnoverRate;

    private Double amountRate;

    private Long turnoverAmount;

    private Double shockRate;

    private Double turnoverVolume;

    private Double entrustRate;

    private Long entrustDifferent;

    private Double peDynamic;

    private Double peRoll;

    private Double earningsPerShare;

    private Double retainedProfits;

    private Double salesRevenue;

    private Double tradedMarketValue;

    private Double totalMarketValue;

    private Date tradeDate;

    private String market;

}
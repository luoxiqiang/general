package per.qiang.spider.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
@Getter @Setter
public class HistoryTradeStock implements Serializable {

    private static final long serialVersionUID = -6167172864695707885L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Excel(name = "股票代码")
    private String code;

    @Excel(name = "名称")
    private String name;

    @Excel(name = "收盘价")
    private Double todayClose;

    @Excel(name = "最高价")
    private Double highPrice;

    @Excel(name = "最低价")
    private Double lowPrice;

    @Excel(name = "开盘价")
    private Double todayOpen;

    @Excel(name = "前收盘")
    private Double yestodyClose;

    @Excel(name = "涨跌额")
    private Double updownPrice;

    @Excel(name = "涨跌幅")
    private Double updownRate;

    @Excel(name = "换手率")
    private Double turnoverRate;

    @Excel(name = "成交量")
    private Long turnoverAmount;

    @Excel(name = "成交金额")
    private Double turnoverVolume;

    @Excel(name = "总市值")
    private Double totalMarketValue;

    @Excel(name = "流通市值",replace = "0_None")
    private Double tradedMarketValue;

    @Excel(name = "日期",format = "yyyy-MM-dd")
    private Date tradeDate;

}
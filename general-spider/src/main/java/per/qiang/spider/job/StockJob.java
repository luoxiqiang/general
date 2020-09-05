package per.qiang.spider.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import per.qiang.common.core.util.DateUtil;
import per.qiang.spider.entity.DailyTradeStock;
import per.qiang.spider.service.DailyTradeService;
import per.qiang.spider.service.HistoryTradeStockService;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StockJob {

    private final DailyTradeService dailyTradeService;

    private final HistoryTradeStockService historyTradeStockService;

    @XxlJob("dailyTrade")
    public ReturnT<String> dailyTrade(String param) {
        dailyTradeService.queryTodayData();
        return ReturnT.SUCCESS;
    }

    @XxlJob("historyTrade")
    public ReturnT<String> historyTrade(String param) {
        List<DailyTradeStock> all = dailyTradeService.findAllByLastDate();
        if (all == null || all.isEmpty()) return ReturnT.SUCCESS;
        all.forEach(stock -> {
            try {
                Thread.sleep(3_000);
                Date date = historyTradeStockService.queryLastDateByCode(stock.getCode());
                historyTradeStockService.queryHistoryData(stock.getMarket() + stock.getCode(), date == null ? null : DateUtil.formatDate(date, "yyyy-MM-dd"), null);
            } catch (InterruptedException ignored) {
            }
        });
        return ReturnT.SUCCESS;
    }

    @XxlJob("test")
    public ReturnT<String> test(String param){
        return new ReturnT<>(200,DateUtil.formatDate(historyTradeStockService.queryLastDateByCode("600236"), "yyyy-MM-dd"));
    }

}

package per.qiang.spider.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.util.DateUtil;
import per.qiang.spider.entity.DailyTradeStock;
import per.qiang.spider.jpa.DailyTradeStockRepository;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class DailyTradeProcessor implements PageProcessor {

    public static final String START_URL = "http://quotes.money.163.com/hs/service/diyrank.php?page=0&query=STYPE%3AEQA&fields=" +
            "TIME%2CPRICE%2CPERCENT%2CUPDOWN%2COPEN%2CYESTCLOSE%2CHIGH%2CLOW%2CVOLUME%2CTURNOVER%2CHS%2C" +
            "LB%2CWB%2CZF%2CPE%2CMCAP%2CTCAP%2CMFSUM%2CMFRATIO.MFRATIO2%2CMFRATIO.MFRATIO10%2CNAME%2CCODE&count=99999&type=query";

    private final DailyTradeStockRepository repository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(Page page) {
        if (!page.isDownloadSuccess()) return;
        List<String> list = page.getJson().jsonPath("list").all();
        if (list == null || list.isEmpty()) return;
        Date date = null;
        try {
            int i = 0;
            while (date == null && i < list.size())
                date = DateUtil.parse("yyyy/MM/dd", new Json(list.get(i++)).jsonPath("TIME").get());
            if (date == null) date = DateUtil.parse("yyyy/MM/dd", new Date());
        } catch (ParseException e) {
            log.error("时间转化失败 {}", e.getMessage());
        }
        if (repository.existsByTradeDate(date)) return;
        for (String s : list) {
            Json stock = new Json(s);
            DailyTradeStock dailyTrade = new DailyTradeStock();
            String code = stock.jsonPath("CODE").get();
            if (StringUtils.isBlank(code)) continue;
            dailyTrade.setCode(code.substring(1));
            dailyTrade.setName(stock.jsonPath("NAME").get());
            dailyTrade.setTodayClose(converter(stock, "PRICE", Double.class));
            dailyTrade.setTodayOpen(converter(stock, "OPEN", Double.class));
            dailyTrade.setHighPrice(converter(stock, "HIGH", Double.class));
            dailyTrade.setUpdownRate(converter(stock, "PERCENT", Double.class));
            dailyTrade.setUpdownPrice(converter(stock, "UPDOWN", Double.class));
            dailyTrade.setLowPrice(converter(stock, "LOW", Double.class));
            dailyTrade.setYestodayClose(converter(stock, "YESTCLOSE", Double.class));
            dailyTrade.setTurnoverRate(converter(stock, "HS", Double.class));
            dailyTrade.setAmountRate(converter(stock, "LB", Double.class));
            dailyTrade.setTurnoverAmount(converter(stock, "VOLUME", Long.class));
            dailyTrade.setShockRate(converter(stock, "ZF", Double.class));
            dailyTrade.setTurnoverVolume(converter(stock, "TURNOVER", Double.class));
            dailyTrade.setEntrustRate(converter(stock, "WB", Double.class));
            dailyTrade.setPeRoll(converter(stock, "PE", Double.class));
            dailyTrade.setEarningsPerShare(converter(stock, "MFSUM", Double.class));
            dailyTrade.setRetainedProfits(converter(stock, "MFRATIO.MFRATIO2", Double.class));
            dailyTrade.setSalesRevenue(converter(stock, "MFRATIO.MFRATIO10", Double.class));
            dailyTrade.setTradedMarketValue(converter(stock, "MCAP", Double.class));
            dailyTrade.setTotalMarketValue(converter(stock, "TCAP", Double.class));
            dailyTrade.setMarket(code.substring(0, 1));
            dailyTrade.setTradeDate(date);
            repository.save(dailyTrade);
        }
    }

    private <T> T converter(Json json, String key, Class<T> clazz) {
        try {
            String data = json.jsonPath(key).get();
            if (StringUtils.isBlank(data) || !Pattern.matches("^-?[0-9]+([.]{1}[0-9Ee]+){0,1}$", data)) return null;
            Constructor<T> constructor = clazz.getConstructor(String.class);
            String className = clazz.getSimpleName();
            double _data = Double.parseDouble(data);
            switch (className) {
                case "Double":
                    return constructor.newInstance(Double.toString(_data));
                case "Long":
                    return constructor.newInstance(Long.toString((long) _data));
            }
        } catch (Exception e) {
            log.error("数据转化失败 {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setDomain("quotes.money.163.com")
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .setCharset("UTF-8")
                .addHeader("Referer", "http://quotes.money.163.com/old/")
                .setRetryTimes(3).setSleepTime(3000);
    }

}


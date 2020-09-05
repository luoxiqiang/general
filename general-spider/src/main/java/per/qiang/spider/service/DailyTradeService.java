package per.qiang.spider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.spider.entity.DailyTradeStock;
import per.qiang.spider.jpa.DailyTradeStockRepository;
import per.qiang.spider.processor.DailyTradeProcessor;
import us.codecraft.webmagic.Spider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyTradeService {

    private final DailyTradeProcessor dailyTradeProcessor;

    private final DailyTradeStockRepository repository;

    public void queryTodayData(){
        Spider.create(dailyTradeProcessor)
                .addUrl(DailyTradeProcessor.START_URL)
                .run();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DailyTradeStock> findAllByLastDate(){
        return repository.findByTradeDateLast();
    }

}

package per.qiang.spider.service;

import cn.afterturn.easypoi.csv.entity.CsvImportParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.util.PoiUtil;
import per.qiang.spider.entity.HistoryTradeStock;
import per.qiang.spider.jpa.HistoryTradeStockRepository;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientGenerator;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryTradeStockService {

    private static final String BASE_URL = "http://quotes.money.163.com/service/chddata.html";

    private final HistoryTradeStockRepository historyTradeStockRepository;

    /**
     * @param code     e: 1000001
     * @param fromDate e: 2020-08-22
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void queryHistoryData(String code, String fromDate, String toDate) {

        Site site = Site.me().setDomain("quotes.money.163.com")
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36")
                .setRetryTimes(3).setSleepTime(3000);
        HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
        try (CloseableHttpClient client = httpClientGenerator.getClient(site)) {
            String url = BASE_URL + "?code=" + code;
            if (historyTradeStockRepository.existsByCode(code.substring(1)) && StringUtils.isNotBlank(fromDate))
                url += "&start=" + fromDate;
            if (StringUtils.isNotBlank(toDate)) url += "&end=" + toDate;
            CloseableHttpResponse response = client.execute(new HttpGet(url));
            List<HistoryTradeStock> list = PoiUtil.importCsv(response.getEntity().getContent(), HistoryTradeStock.class, new CsvImportParams(CsvImportParams.GBK));
            Pattern pattern = Pattern.compile("[^0-9]");
            list.forEach(stock -> {
                stock.setCode(pattern.matcher(stock.getCode()).replaceAll("").trim());
                if (!historyTradeStockRepository.existsByCodeAndTradeDate(stock.getCode(), stock.getTradeDate()))
                    historyTradeStockRepository.save(stock);
            });
        } catch (Exception e) {
            log.error("获取[{}]历史数据异常:{}", code, e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Date queryLastDateByCode(String code) {
        return historyTradeStockRepository.queryLastDateByCode(code);
    }
}

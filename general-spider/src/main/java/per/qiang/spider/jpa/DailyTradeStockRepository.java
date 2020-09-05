package per.qiang.spider.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import per.qiang.spider.entity.DailyTradeStock;

import java.util.Date;
import java.util.List;

public interface DailyTradeStockRepository extends JpaRepository<DailyTradeStock, Long>, JpaSpecificationExecutor<DailyTradeStock> {
    boolean existsByTradeDate(Date date);

    @Query(value = "SELECT * from daily_trade_stock where trade_date=(SELECT max(trade_date) from daily_trade_stock)", nativeQuery = true)
    List<DailyTradeStock> findByTradeDateLast();
}

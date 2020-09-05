package per.qiang.spider.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import per.qiang.spider.entity.HistoryTradeStock;

import java.util.Date;

public interface HistoryTradeStockRepository extends JpaRepository<HistoryTradeStock, Long>, JpaSpecificationExecutor<HistoryTradeStock> {

    boolean existsByCodeAndTradeDate(String code, Date tradeDate);

    boolean existsByCode(String code);

    @Query(value = "SELECT max(trade_date) from history_trade_stock where code= ?1", nativeQuery = true)
    Date queryLastDateByCode(String code);
}

package per.qiang.spider.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class SHStock {

    @Excel(name = "公司代码")
    private String companyCode;
    @Excel(name="公司简称")
    private String companyName;
    @Excel(name = "上市日期" ,format = "yyyy-MM-dd")
    private Date marketDate;

}

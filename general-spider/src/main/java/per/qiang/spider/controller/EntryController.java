package per.qiang.spider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class EntryController {

    @GetMapping("index")
    public void index(HttpServletResponse response) throws IOException {


    }

}

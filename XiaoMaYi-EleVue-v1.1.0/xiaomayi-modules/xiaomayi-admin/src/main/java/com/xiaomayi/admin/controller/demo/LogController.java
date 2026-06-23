package com.xiaomayi.admin.controller.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
@AllArgsConstructor
public class LogController {

    @GetMapping("/log")
    public String logSomething() {
        log.trace("This is a TRACE log");
        log.debug("This is a DEBUG log");
        log.info("This is an INFO log");
        log.warn("This is a WARN log");
        log.error("This is an ERROR log");

        return "Check your logs and Kibana!";
    }

}

package com.weds.demo.export.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/*
    临时文件存储位置
 */
@Component
public class ExporPathConfig {
    
    //此处是配置的临时缓存路径在当前相对目录下tmp文件夹中
	private String Path = System.getProperty("user.dir");

	private final Logger log = LoggerFactory.getLogger(ExporPathConfig.class);
	
	@PostConstruct
    public void init() {
        try {
            //设置临时文件存放路径
            System.setProperty("java.io.tmpdir", Path);
        } catch (Exception ex) {
            log.error(ex.getMessage(),"获取消息导出临时缓存路径失败");
        }
    }
}
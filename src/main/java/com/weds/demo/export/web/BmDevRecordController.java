package com.weds.demo.export.web;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.weds.demo.export.entity.DemoExportEntity;
import com.weds.demo.export.service.BmDevRecordService;
import com.weds.demo.export.service.socket.WebSocketServer;
import com.weds.demo.export.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class BmDevRecordController {
    //批次页码大小
    private final static Integer flagCount = 100000;
    @Autowired
    private BmDevRecordService bmDevRecordService;

    /**
     * @Description: 多批次导出大数据量，由于该方法在磁盘中处理数据，
     *      建议只在数据量超过100W的条件下执行；低数据量情况下保留单批次内存执行
     * @Param: [response]
     * @return: void
     * @Author: tjy
     * @Date: 2019/12/18
     */
    @GetMapping("/export/InstituteData/{sid}")
    public void exportInstituteData(@PathVariable("sid") String sid, HttpServletResponse response){
        //查询导出的总数量（或由前台传递）
        Integer count = bmDevRecordService.testGetBigDataCount();
        //页数计算
        Integer resultCount = count % flagCount == 0 ? (count / flagCount):((count / flagCount) + 1);
        System.out.println("导出数据总行："+count+", 批次数量："+resultCount);
        Workbook workbook = null;
        try {
            Date start = new Date();
            //配置sheet页中标题名称
            ExportParams params = new ExportParams("大数据测试", "测试");
            //循环分页次数
            for (int i = 1; i <= resultCount; i++) {

                //判断当前连接是否已经断开
                boolean flag = WebSocketServer.existSid(sid);

                //如果该id连接中断则跳出该循环
                if(!flag){
                    throw new Exception("不存在该用户或该用户连接已经断开！");
                }
                Date begin = new Date();
                //分页读取源数据
                List<DemoExportEntity> ls = bmDevRecordService.testGetBigData(i,flagCount);
                System.out.println("第"+i+"批查库耗时：" + (System.currentTimeMillis() - begin.getTime())+"毫秒");
                Date secondBegin = new Date();
                //进行拼装数据
                /*
                 1.这里注意，因为数据组装过程在磁盘中进行 所有会产生临时文件 默认临时文件在C:\Users\Administrator\AppData\Local\Temp（win7系统）
                 2.这里将临时文件路径指定到当前项目地址下 详细可以见 config 包的配置方法
                 3.这里补充说明一下，如需打包时，使用maven工具打包 TEST 时 会报错，因为 TEST 时没有 servlet 容器 需在TEST入口类中加入
                 @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
                 */
                workbook = ExcelExportUtil.exportBigExcel(params, DemoExportEntity.class, ls);
                System.out.println("第"+i+"批拼装Excel耗时：" + (System.currentTimeMillis() - secondBegin.getTime())+"毫秒");
                //这里是webSocket与前台对话方法（sid静态为20）
                WebSocketServer.sendInfo(i + "/" + resultCount,sid);
            }
            //拼装数据成功后进行下载
            //这里使用xlsx格式 防止某些用户还使用 2003(2003的默认最大数 65536) WPS可以无视这里。
            ExcelUtil.downLoadExcel("测试大数据.xlsx",response,workbook);
            System.out.println("响应总耗时："+(System.currentTimeMillis() - start.getTime())+"毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //清理磁盘临时文件
            ExcelUtil.deleteSXSSFTempFiles((SXSSFWorkbook) workbook);
            //手动关闭webSocket连接
            WebSocketServer.closeBySid(sid);
        }
    }

}

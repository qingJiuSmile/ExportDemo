package com.weds.demo.export.util;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
//Excel工具类
public class ExcelUtil {

    static Logger logger = LogManager.getLogger("elkLog");

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response){
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, ByteArrayOutputStream os){
        defaultExport(list, pojoClass, fileName, os, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, ByteArrayOutputStream os, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if (workbook != null);
        downLoadExcel(fileName, os, workbook);
    }

    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void downLoadExcel(String fileName, ByteArrayOutputStream os, Workbook workbook) {
        try {
            workbook.write(os);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null);
        downLoadExcel(fileName, response, workbook);
    }

    public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        }catch (NoSuchElementException e){
            logger.error("excel模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return list;
    }
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass){
        if (file == null){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        }catch (NoSuchElementException e){
            logger.error("excel文件不能为空");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return list;
    }
    /**
     * Returns a private attribute of a class
     * @param containingClass The class that contains the private attribute to retrieve
     * @param fieldToGet The name of the attribute to get
     * @return The private attribute
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getPrivateAttribute(Object containingClass, String fieldToGet) throws NoSuchFieldException, IllegalAccessException {
        //get the field of the containingClass instance
        Field declaredField = containingClass.getClass().getDeclaredField(fieldToGet);
        //set it as accessible
        declaredField.setAccessible(true);
        //access it
        Object get = declaredField.get(containingClass);
        //return it!
        return get;
    }

    /**
     * Deletes all temporary files of the SXSSFWorkbook instance
     * @param workbook
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void deleteSXSSFTempFiles(SXSSFWorkbook workbook) {
       try {
           int numberOfSheets = workbook.getNumberOfSheets();

           //iterate through all sheets (each sheet as a temp file)
           for (int i = 0; i < numberOfSheets; i++) {
               Sheet sheetAt = workbook.getSheetAt(i);

               //delete only if the sheet is written by stream
               if (sheetAt instanceof SXSSFSheet) {
                   SheetDataWriter sdw = (SheetDataWriter) getPrivateAttribute(sheetAt, "_writer");
                   File f = (File) getPrivateAttribute(sdw, "_fd");

                   try {
                       //关闭流 在异常失败时 不关闭流无法正常删除文件
                       workbook.close();
                       if(!f.delete()){
                           logger.error("临时文件清除失败!");
                       }

                   } catch (Exception ex) {
                       //could not delete the file
                   }
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
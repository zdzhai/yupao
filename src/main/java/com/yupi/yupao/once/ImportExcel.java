package com.yupi.yupao.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhai Zhidong
 * @version 1.0
 * @Date 2023/1/24 23:07
 * 导入Excel数据到数据库
 */
@Slf4j
public class ImportExcel {
    public static void main(String[] args) {
        String fileName = "D:\\SoftWare\\Code\\yupao\\用户信息.xlsx";
        listenerRead(fileName);
        synchronousRead(fileName);
        defaultListenerRead(fileName);
    }
    public static void listenerRead(String fileName){
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, XingQiuTabUserInfo.class,
                new XingQiuTabUserInfoListener()).sheet().doRead();

    }
    public static void synchronousRead(String fileName){
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTabUserInfo> userInfoList = EasyExcel.read(fileName, XingQiuTabUserInfo.class,
                new XingQiuTabUserInfoListener()).sheet().doReadSync();
        for (XingQiuTabUserInfo data : userInfoList) {
            System.out.println(data);
        }
        Map<String,List<XingQiuTabUserInfo>> listMap = userInfoList.stream()
                .filter(userInfo -> StringUtils.isNotBlank(userInfo.getUsername()))
                .collect(Collectors.groupingBy(XingQiuTabUserInfo::getUsername));
        System.out.println(listMap.keySet());

    }
    public static void defaultListenerRead(String fileName){
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName,XingQiuTabUserInfo.class,
                new PageReadListener<XingQiuTabUserInfo>(list -> {
                    for (XingQiuTabUserInfo xingQiuTabUserInfo : list){
                        log.info("读取到一条数据{}" + xingQiuTabUserInfo);
                    }
                })).sheet().doRead();
    }
}

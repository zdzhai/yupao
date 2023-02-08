package com.yupi.yupao.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/1/24 22:54
 */
// 有个很重要的点 XingQiuTabUserInfoListener 不能被spring管理，
// 要每次读取excel都要new,然后里面用到spring可以构造方法传进去
@Slf4j
public class XingQiuTabUserInfoListener implements ReadListener<XingQiuTabUserInfo> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<XingQiuTabUserInfo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private XingQiuTabUserInfo xingQiuTabUserInfo;

    public XingQiuTabUserInfoListener() {
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        xingQiuTabUserInfo = new XingQiuTabUserInfo();
    }

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param demoDAO
     */
    public XingQiuTabUserInfoListener(XingQiuTabUserInfo demoDAO) {
        this.xingQiuTabUserInfo = demoDAO;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(XingQiuTabUserInfo data, AnalysisContext context) {
        log.info("解析到一条数据:{}" + data);
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        log.info("所有数据解析完成！");
    }
}

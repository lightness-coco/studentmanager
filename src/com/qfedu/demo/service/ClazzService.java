package com.qfedu.demo.service;

import com.qfedu.demo.dao.ClazzDao;
import com.qfedu.demo.model.Clazz;
import com.qfedu.demo.model.ClazzDTO;
import com.qfedu.demo.model.RespPageBean;
import com.qfedu.demo.utils.CommonsUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * 1 10
 * 2 10
 * 3 10
 * <p>
 * select * from clazz limit 0,10
 * select * from clazz limit 10,10
 * select * from clazz limit 20,10
 */
public class ClazzService {
    ClazzDao clazzDao = new ClazzDao();

    /**
     * 如果没有传递分页参数，那么就查询所有数据
     * 如果没有传递排序参数，那么默认按照 cid 顺序查询
     *
     * @param gid
     * @param page
     * @param rows
     * @param sort
     * @param order
     * @return
     */
    public RespPageBean getClazzByPage(String gid, String page, String rows, String sort, String order) {

        if (page == null || "".equals(page)) {
            //如果没有传递 page，默认 page 为 1
            page = "1";
        }

        if (rows == null || "".equals(rows)) {
            rows = Integer.MAX_VALUE + "";
        }
        if (sort == null || "".equals(sort)) {
            sort = "cid";
        }
        if (order == null || "".equals(order)) {
            order = "asc";
        }

        try {
            //在当前查询条件下，总记录数
            Long total = clazzDao.getTotal(gid);
            int offset = Integer.parseInt(page);
            int size = Integer.parseInt(rows);
            //按照条件进行查询
            List<ClazzDTO> list = clazzDao.getClazzByPage(gid, (offset - 1) * size, size, sort, order);
            return new RespPageBean(total, list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer addClazz(String clazzName, int gid) {
        Integer r = null;
        try {
            Clazz c = clazzDao.getClazzByClazzName(clazzName);
            if (c != null) {
                return CommonsUtils.REPEATABLE_VALUE;
            }
            r = clazzDao.addClazz(clazzName, gid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r == 1 ? CommonsUtils.INSERT_SUCCESS : CommonsUtils.OTHER_EXCEPTION;
    }
}

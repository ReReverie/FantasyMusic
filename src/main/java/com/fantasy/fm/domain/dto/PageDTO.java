package com.fantasy.fm.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    private Long total; // 总记录数
    private Long pages; // 总页数
    private List<T> list; // 当前页数据列表

    /**
     * 返回空分页结果
     *
     * @param p    MybatisPlus的分页结果
     * @param <VO> 目标VO类型
     * @param <PO> 原始PO类型
     * @return VO的分页对象
     */
    public static <VO, PO> PageDTO<VO> empty(Page<PO> p) {
        return new PageDTO<>(p.getTotal(), p.getPages(), Collections.emptyList());
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果
     *
     * @param p     MybatisPlus的分页结果
     * @param clazz 目标VO类型的字节码
     * @param <VO>  目标VO类型
     * @param <PO>  原始PO类型
     * @return VO的分页对象
     */
    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Class<VO> clazz) {
        //封装VO
        PageDTO<VO> pageDTO = new PageDTO<>();
        //总的记录数
        pageDTO.setTotal(p.getTotal());
        pageDTO.setPages(p.getPages());
        //当前页数据
        List<PO> records = p.getRecords();
        //如果records没有数据，直接返回
        if (records.isEmpty()) {
            return pageDTO;
        }
        List<VO> userVOS = BeanUtil.copyToList(records, clazz);
        pageDTO.setList(userVOS);
        //返回分页结果
        return pageDTO;
    }

    /**
     * 将MybatisPlus分页结果转为 VO分页结果，允许用户自定义PO到VO的转换方式
     *
     * @param p         MybatisPlus的分页结果
     * @param convertor PO到VO的转换函数
     * @param <VO>      目标VO类型
     * @param <PO>      原始PO类型
     * @return VO的分页对象
     */
    public static <PO, VO> PageDTO<VO> of(Page<PO> p, Function<PO, VO> convertor) { //Function传的是用户自己转换的VO对象
        // 1.非空校验
        List<PO> records = p.getRecords();
        if (records == null || records.isEmpty()) {
            // 无数据，返回空结果
            return empty(p);
        }
        // 2.数据转换
        List<VO> vos = records.stream().map(convertor).collect(Collectors.toList());
        // 3.封装返回
        return new PageDTO<>(p.getTotal(), p.getPages(), vos);
    }
}

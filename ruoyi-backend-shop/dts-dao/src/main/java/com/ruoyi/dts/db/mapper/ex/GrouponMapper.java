package com.ruoyi.dts.db.mapper.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.dts.db.domain.DtsGroupon;
import com.ruoyi.dts.db.domain.DtsGrouponRules;

/**
 * 团购管理
 * @author suichj
 * @since 1.0.0
 */
public interface GrouponMapper {

	/**
	 * 按入驻店铺查询归属的团购规则信息
	 * @param goodsId
	 * @param orderBySql
	 * @param brandIdsSql
	 * @return
	 */
	List<DtsGrouponRules> queryBrandGrouponRules(@Param("goodsId") String goodsId, @Param("orderBySql") String orderBySql, @Param("brandIdsSql") String brandIdsSql);

	/**
	 * 按入驻店铺查询归属的团购记录信息
	 * @param goodsId
	 * @param orderBySql
	 * @param brandIdsSql
	 * @return
	 */
	List<DtsGroupon> queryBrandGroupons(@Param("rulesId") String rulesId, @Param("orderBySql") String orderBySql, @Param("brandIdsSql") String brandIdsSql);

}

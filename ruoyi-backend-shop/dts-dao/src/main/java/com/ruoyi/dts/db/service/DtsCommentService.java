package com.ruoyi.dts.db.service;

import com.github.pagehelper.PageHelper;
import com.ruoyi.dts.db.mapper.DtsCommentMapper;
import com.ruoyi.dts.db.mapper.ex.CommentMapper;
import com.ruoyi.dts.db.domain.DtsComment;
import com.ruoyi.dts.db.domain.DtsCommentExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DtsCommentService {
	@Resource
	private DtsCommentMapper dtsCommentMapper;
	
	@Resource
	private CommentMapper commentMapper;

	public List<DtsComment> queryGoodsByGid(Integer id, int offset, int limit) {
		DtsCommentExample example = new DtsCommentExample();
		example.setOrderByClause(DtsComment.Column.addTime.desc());
		example.or().andValueIdEqualTo(id).andTypeEqualTo((byte) 0).andDeletedEqualTo(false);
		PageHelper.startPage(offset, limit);
		return dtsCommentMapper.selectByExample(example);
	}

	public List<DtsComment> query(Byte type, Integer valueId, Integer showType, Integer offset, Integer limit) {
		DtsCommentExample example = new DtsCommentExample();
		example.setOrderByClause(DtsComment.Column.addTime.desc());
		if (showType == 0) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
		} else if (showType == 1) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true)
					.andDeletedEqualTo(false);
		} else {
			throw new RuntimeException("showType不支持");
		}
		PageHelper.startPage(offset, limit);
		return dtsCommentMapper.selectByExample(example);
	}

	public int count(Byte type, Integer valueId, Integer showType) {
		DtsCommentExample example = new DtsCommentExample();
		if (showType == 0) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
		} else if (showType == 1) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true)
					.andDeletedEqualTo(false);
		} else {
			throw new RuntimeException("showType不支持");
		}
		return (int) dtsCommentMapper.countByExample(example);
	}

	public int save(DtsComment comment) {
		comment.setAddTime(LocalDateTime.now());
		comment.setUpdateTime(LocalDateTime.now());
		return dtsCommentMapper.insertSelective(comment);
	}

	public List<DtsComment> querySelective(String userId, String valueId) {
		DtsCommentExample example = new DtsCommentExample();
		DtsCommentExample.Criteria criteria = example.createCriteria();

		// type=2 是订单商品回复，这里过滤
		criteria.andTypeNotEqualTo((byte) 2);

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(valueId)) {
			criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeEqualTo((byte) 0);
		}
		criteria.andDeletedEqualTo(false);
		return dtsCommentMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		dtsCommentMapper.logicalDeleteByPrimaryKey(id);
	}

	public String queryReply(Integer id) {
		DtsCommentExample example = new DtsCommentExample();
		example.or().andTypeEqualTo((byte) 2).andValueIdEqualTo(id);
		List<DtsComment> commentReply = dtsCommentMapper.selectByExampleSelective(example, DtsComment.Column.content);
		// 目前业务只支持回复一次
		if (commentReply.size() == 1) {
			return commentReply.get(0).getContent();
		}
		return null;
	}

	public DtsComment findById(Integer id) {
		return dtsCommentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 入驻店铺对应商品的评价
	 * @param brandIds
	 * @param userId
	 * @param valueId
	 * @param page
	 * @param limit
	 * @param sort
	 * @param order
	 * @return
	 */
	public List<DtsComment> queryBrandCommentSelective(List<Integer> brandIds, String userId, String valueId) {
		

		String brandIdsSql = null;
		if (brandIds != null) {
			brandIdsSql = "";
			for (Integer brandId : brandIds) {
				brandIdsSql += "," + brandId;
			}
			brandIdsSql = " and g.brand_id in (" + brandIdsSql.substring(1) + ") ";
		}

		Byte type = (byte) 0;//品牌入驻管理员限定只查商品评论
		return commentMapper.queryBrandComment(type,userId,valueId,"",brandIdsSql);
	}
}

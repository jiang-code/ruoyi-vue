package com.ruoyi.dts.wx.service;

import com.ruoyi.dts.db.domain.DtsRegion;
import com.ruoyi.dts.db.service.DtsRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ruoyi
 * @since 1.0.0
 **/
@Component
public class GetRegionService {

	@Autowired
	private DtsRegionService regionService;

	private static List<DtsRegion> DtsRegions;

	protected List<DtsRegion> getDtsRegions() {
		if (DtsRegions == null) {
			createRegion();
		}
		return DtsRegions;
	}

	private synchronized void createRegion() {
		if (DtsRegions == null) {
			DtsRegions = regionService.getAll();
		}
	}
}

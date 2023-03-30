package com.umjari.server.domain.region.service

import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
import org.springframework.stereotype.Service

@Service
class RegionService(
    private val regionRepository: RegionRepository,
) {
    fun getOrCreateRegion(regionParent: String, regionChild: String): Region {
        val region = regionRepository.findByParentAndChild(regionParent, regionChild) ?: run {
            val obj = Region(regionParent, regionChild)
            regionRepository.save(obj)
        }
        return region
    }
}

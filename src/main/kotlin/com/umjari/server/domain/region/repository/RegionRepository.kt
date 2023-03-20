package com.umjari.server.domain.region.repository

import com.umjari.server.domain.region.model.Region
import org.springframework.data.jpa.repository.JpaRepository

interface RegionRepository: JpaRepository<Region, Long?> {
    fun findByParentAndChild(parent: String, child: String): Region?
}

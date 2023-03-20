package com.umjari.server.domain.group.repository

import com.umjari.server.domain.group.model.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long?>

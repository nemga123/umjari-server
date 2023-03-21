package com.umjari.server.domain.group.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class GroupIdNotFoundException(groupId: Long) :
    DataNotFoundException(ErrorType.GROUP_ID_NOT_FOUND, "groupId = $groupId is not found.")

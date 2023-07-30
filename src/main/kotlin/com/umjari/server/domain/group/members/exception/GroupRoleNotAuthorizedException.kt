package com.umjari.server.domain.group.members.exception

import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class GroupRoleNotAuthorizedException(expectedRole: GroupMember.MemberRole) :
    NotAllowedException(ErrorType.GROUP_ROLE_NOT_AUTHORIZED, "Expected authority is ${expectedRole.name} for this api.")

package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class FilterTypeNotFoundException(filterType: String) :
    DataNotFoundException(ErrorType.FILTER_TYPE_NOT_FOUND, "$filterType is not in Filter Type.")

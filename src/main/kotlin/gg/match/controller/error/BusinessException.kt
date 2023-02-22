package gg.match.controller.error

open class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException()
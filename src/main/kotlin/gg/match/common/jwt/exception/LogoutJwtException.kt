package gg.match.common.jwt.exception

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode

class LogoutJwtException : BusinessException(ErrorCode.LOGOUT_TOKEN)
package gg.match.controller.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생하였습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    USER_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "권한이 없는 회원의 접근입니다."),
    OAUTH2_FAIL_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "로그아웃한 회원입니다.")
}
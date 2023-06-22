package gg.match.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
@ComponentScan(basePackages = ["gg.match.domain.chat", "gg.match.controller", "gg.match.domain.user", "gg.match.common.jwt", "gg.match.common.config", "gg.match.domain.board"])
class DomainApplication

@PostConstruct
fun started() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
}

fun main(args: Array<String>) {
    runApplication<DomainApplication>(*args)
}

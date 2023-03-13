package gg.match.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["gg.match.controller", "gg.match.domain.user", "gg.match.common.jwt", "gg.match.common.config", "gg.match.domain.board"])
class DomainApplication

fun main(args: Array<String>) {
    runApplication<DomainApplication>(*args)
}

package gg.match.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["gg.match.controller", "gg.match.domain.user"])
class DomainApplication

fun main(args: Array<String>) {
    runApplication<DomainApplication>(*args)
}

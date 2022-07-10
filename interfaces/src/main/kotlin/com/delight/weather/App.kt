package com.delight.weather

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication(
//    exclude = [DataSourceAutoConfiguration::class],
)
//@EnableTransactionManagement
@EnableCaching
class App {

    @PostConstruct
    fun started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}

fun main() {
    runApplication<App>()
}

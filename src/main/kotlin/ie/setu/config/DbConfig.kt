package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig{

    private val logger = KotlinLogging.logger {}
    fun getDbConnection() :Database{

        logger.info{"Starting DB Connection..."}

        val dbConfig = Database.connect(
            "jdbc:postgresql://ec2-54-173-237-110.compute-1.amazonaws.com:5432/db312aubut6p30?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "kfollkuorwkkma",
            password = "070dff8fb882b22fc9ab0146e9d57435b7c1e3627f08f2a47adaf018e52b3f49")

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}
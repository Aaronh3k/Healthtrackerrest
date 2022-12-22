package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig{

    private val logger = KotlinLogging.logger {}
    fun getDbConnection() :Database{

        val logger = KotlinLogging.logger {}
        logger.info{"Starting DB Connection..."}

        val PGUSER = "rrgjmhqr"
        val PGPASSWORD = "HTN7uaaQ2vyS8M8cXHBkYdzxT4IjpXdv"
        val PGHOST = "mel.db.elephantsql.com"
        val PGPORT = "5432"
        val PGDATABASE = "rrgjmhqr"

        //url format should be jdbc:postgresql://host:port/database
        val url = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        val dbConfig = Database.connect(url,
            driver="org.postgresql.Driver",
            user = PGUSER,
            password = PGPASSWORD
        )

        logger.info{"db url - connection: " + dbConfig.url}

        return dbConfig
    }
}
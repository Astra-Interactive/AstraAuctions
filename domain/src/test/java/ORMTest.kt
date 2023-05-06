import kotlinx.coroutines.runBlocking
import ru.astrainteractive.astralibs.orm.DBConnection
import ru.astrainteractive.astralibs.orm.Database
import ru.astrainteractive.astralibs.orm.exception.DatabaseException
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class ORMTest(
    val builder: () -> Database
) {

    protected var database: Database? = null
    fun assertConnected(): Database {
        return database ?: throw DatabaseException.DatabaseNotConnectedException
    }

    @BeforeTest
    open fun setup(): Unit = runBlocking {
        database = builder()
        database?.openConnection()
    }

    @AfterTest
    fun destroy(): Unit = runBlocking {
        database?.closeConnection()
        (database?.dbConnection as? DBConnection.SQLite)?.let {
            File(it.dbName).delete()
        }
        database = null
    }
}

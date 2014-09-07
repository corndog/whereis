package whereis.db

import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.memory.TransientMemoryStorage
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.postgresqlDialect
import java.sql._

object DBContext extends ActivateContext {

  // if db already exists dont bother thinking about migrations
  override protected val runMigrationAtStartup = false

  val storage = new PooledJdbcRelationalStorage {
    val jdbcDriver = "org.postgresql.Driver"
    val user = Some("postgres")
    val password = Some("0")
    val url = "jdbc:postgresql:postgis20"
    val dialect = postgresqlDialect
  }

  def getNextIdForTable(name: String): Long = {
    var conn: Connection = null
    var stmt: Statement = null
    var rs: ResultSet = null
    try {
      conn = storage.directAccess //java.sql.Connection
      stmt = conn.createStatement()
      rs = stmt.executeQuery(s"""SELECT nextval(pg_get_serial_sequence('$name', 'id'))""")
      if (rs.next())
        rs.getLong(1)
      else
        throw new Exception(s"exception finding next id for table $name")
    } finally {
      if (rs != null) rs.close
      if (stmt != null) stmt.close
      if (conn != null) conn.close
    }
  }
}

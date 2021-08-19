package com.bankin.tools.dbmanager.adaptors.ghost

import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.executor.Context
import org.flywaydb.core.api.executor.MigrationExecutor
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.flywaydb.core.internal.jdbc.JdbcTemplate
import org.flywaydb.core.internal.jdbc.Results
import org.flywaydb.core.internal.sqlscript.FlywaySqlScriptException
import org.flywaydb.core.internal.sqlscript.SqlScript
import org.flywaydb.core.internal.sqlscript.SqlStatement
import java.io.IOException
import java.net.URI
import java.util.ArrayList
import java.util.regex.Pattern

// tag::main[]
class GhostMigrationExecutor : MigrationExecutor {

    override fun execute(context: Context) {
        val jdbcTemplate = JdbcTemplate(context.connection)
        for (statement in sqlScript.sqlStatements) {
            if (isAlterTable(statement.sql)) {
                executeWithGhost(context, statement)
            } else {
                executeSql(jdbcTemplate, statement)
            }
        }
    }
    ...
// end::main[]
// tag::sql[]
    private fun executeSql(jdbc: Jdbc, statement: Statement) {
        val results = jdbc.executeStatement(statement.sql)
        ...
    }
// end::sql[]

// tag::ghost[]
    private fun executeWithGhost(table: String, alter: String) {
        val cmds: MutableList<String> = ArrayList()
        cmds.add("gh-ost")
        cmds.add("--user=$user")
        cmds.add("--password=$password")
        cmds.add("--host=${connexion.host}")
        ...
        cmds.add("--table=$table")
        cmds.add("--alter=$alter")
        cmds.add("--chunk-size=10000")
        cmds.add("--cut-over=atomic")
        cmds.add("--execute")

        ProcessBuilder().inheritIO().command(cmds).start()

        ...

        try {
            val p = processBuilder.start()
            if (p.waitFor() != 0) {
                throw FlywayException("Migration failed !")
            }
        } catch (e: IOException) {
            throw FlywayException("Migration failed !", e)
        } catch (e: InterruptedException) {
            throw FlywayException("Migration failed !", e)
        }
    }
// end::ghost[]
    override fun canExecuteInTransaction(): Boolean {
        return false
    }

    companion object {
        private val ALTER_REGEX = Pattern.compile(" *ALTER *TABLE *`?([\\S&&[^`]]*)`? *(.*)", Pattern.CASE_INSENSITIVE)
        private fun handleException(results: Results, sqlScript: SqlScript, sqlStatement: SqlStatement) {
            throw FlywaySqlScriptException(sqlScript.resource, sqlStatement, results.exception)
        }

        private fun printWarnings(results: Results) {
            for (warning in results.warnings) {
                if ("00000" === warning.state) {
                    System.out.printf("DB: " + warning.message)
                } else {
                    System.out.printf("DB: " + warning.message + " (SQL State: " + warning.state + " - Error Code: " + warning.code + ")")
                }
            }
        }

        private fun extractDriverConnection(dataSource: DriverDataSource): GhostConnection {
            val url = dataSource.url
            val cleanURI = url.substring(5)
            val uri = URI.create(cleanURI)
            val host = uri.host
            val port = uri.port.toString()
            val db = uri.path.substring(1)
            val user = dataSource.user
            val password = dataSource.password
            return GhostConnection(host, port, db, user, password)
        }
    }
}

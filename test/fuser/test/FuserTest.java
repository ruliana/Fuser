package fuser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fuser.Fuser;

/**
 *
 * @author SFPISA
 */
public class FuserTest {

    @Test
    public void basicSelection() {
        Fuser fuser = new Fuser("Select * from tabela");

        assertEquals("ORACLE", "Select * from tabela", fuser.toOracle());
        assertEquals("MYSQL", "Select * from tabela", fuser.toMySQL());
        assertEquals("MSSQL", "Select * from tabela", fuser.toSQLServer());
        assertEquals("FIREBIRD", "Select * from tabela", fuser.toFirebird());
        assertEquals("POSTGRES", "Select * from tabela", fuser.toPostgreSQL());
    }

    @Test
    public void concatAndSubstring() {
        Fuser fuser = new Fuser("Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela");

        assertEquals("ORACLE", "Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela", fuser.toOracle());
        assertEquals("MSSQL", "Select tabela.campo1 + SUBSTRING(tabela.campo2 + tabela.campo3, 2, 3) + campo4 from tabela", fuser.toSQLServer());
        assertEquals("MYSQL", "Select CONCAT( tabela.campo1, SUBSTR( CONCAT(tabela.campo2, tabela.campo3), 2, 3), campo4) from tabela", fuser.toMySQL());
        assertEquals("FIREBIRD", "Select tabela.campo1 || SUBSTRING(tabela.campo2 || tabela.campo3 FROM  2 FOR  3) || campo4 from tabela", fuser.toFirebird());
        assertEquals("POSTGRES", "Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela", fuser.toPostgreSQL());
    }

    @Test
    public void positionAndCharLength() {
        Fuser fuser = new Fuser("SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela");

        assertEquals("ORACLE", "SELECT substr(campo1, INSTR(campo1, 'B'), LENGTH(campo1) - 1) FROM tabela", fuser.toOracle());
        assertEquals("MSSQL", "SELECT SUBSTRING(campo1, CHARINDEX('B', campo1), LEN(campo1) - 1) FROM tabela", fuser.toSQLServer());
        assertEquals("MYSQL", "SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toMySQL());
        assertEquals("FIREBIRD", "SELECT SUBSTRING(campo1 FROM  POSITION('B' IN campo1) FOR  CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toFirebird()); 
        assertEquals("POSTGRES", "SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toPostgreSQL());
    }

    @Test
    public void switchCase() {
        Fuser fuser = new Fuser("SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela");

        assertEquals("ORACLE", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toOracle());
        assertEquals("MSSQL", "SELECT CASE WHEN (campo1 + campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toSQLServer());
        assertEquals("MYSQL", "SELECT CASE WHEN ( CONCAT(campo1, campo2)) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toMySQL()); 
        assertEquals("FIREBIRD", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toFirebird());
        assertEquals("POSTGRES", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toPostgreSQL());
    }

    @Test
    public void trim(){
        Fuser fuser = new Fuser("SELECT trim(campo) FROM table1");

        assertEquals("MSSQL", "SELECT LTRIM(RTRIM(campo)) FROM table1", fuser.toSQLServer() );
        assertEquals("ORACLE", "SELECT trim(campo) FROM table1", fuser.toOracle() );

        fuser = new Fuser("SELECT TRIM( campo || LOWER(campo2)) FROM Tabela");
        assertEquals("MSSQL", "SELECT LTRIM(RTRIM( campo + LOWER(campo2))) FROM Tabela", fuser.toSQLServer() );
        assertEquals("MYSQL", "SELECT TRIM( CONCAT( campo, LOWER(campo2))) FROM Tabela", fuser.toMySQL() );
    }

    @Test
    public void testParam(){
        Fuser fuser = new Fuser("SELECT * from tabela where id = :id AND nome =:nome");

        assertEquals("Param", "SELECT * from tabela where id = NULL AND nome =NULL", fuser.changeParam() );
    }

    @Test
    public void testLimit(){
        Fuser fuser = new Fuser("SELECT b FROM Tb limit 10");
        assertEquals("MSSQL", "SELECT TOP 10 b FROM Tb", fuser.toSQLServer());
        assertEquals("ORACLE", "SELECT b FROM Tb WHERE ROWNUM <= 10", fuser.toOracle());
        assertEquals("FIREBIRD", "SELECT FIRST 10 b FROM Tb", fuser.toFirebird());
        assertEquals("MYSQL", "SELECT b FROM Tb limit 10", fuser.toMySQL());
        assertEquals("POSTGRES", "SELECT b FROM Tb limit 10", fuser.toPostgreSQL());
    }

    @Test
    public void testLimitWhere(){
        Fuser fuser = new Fuser("SELECT DISTINCT b FROM Tb WHERE campo = 3 OR id >= 0 ORDER BY 2 limit 20");
        assertEquals("MSSQL", "SELECT DISTINCT TOP 20 b FROM Tb WHERE campo = 3 OR id >= 0 ORDER BY 2", fuser.toSQLServer());
        assertEquals("ORACLE", "SELECT DISTINCT b FROM Tb WHERE campo = 3 OR id >= 0 AND ROWNUM <= 20 ORDER BY 2", fuser.toOracle());
        assertEquals("FIREBIRD", "SELECT DISTINCT FIRST 20 b FROM Tb WHERE campo = 3 OR id >= 0 ORDER BY 2", fuser.toFirebird());
        assertEquals("MYSQL", "SELECT DISTINCT b FROM Tb WHERE campo = 3 OR id >= 0 ORDER BY 2 limit 20", fuser.toMySQL());
        assertEquals("POSTGRES", "SELECT DISTINCT b FROM Tb WHERE campo = 3 OR id >= 0 ORDER BY 2 limit 20", fuser.toPostgreSQL());
    }

     @Test
    public void testLimitUnion(){
        Fuser fuser = new Fuser("SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id LIMIT 50");
        assertEquals("MSSQL", "SELECT TOP 50 * FROM(SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id) AS SUBQUERY", fuser.toSQLServer());
        assertEquals("ORACLE", "SELECT * FROM(SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id) WHERE ROWNUM <= 50", fuser.toOracle());
        assertEquals("FIREBIRD", "SELECT FIRST 50 * FROM(SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id) AS SUBQUERY", fuser.toFirebird());
        assertEquals("MYSQL", "SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id LIMIT 50", fuser.toMySQL());
        assertEquals("POSTGRES", "SELECT id1 AS id FROM Tb WHERE campo = 3 UNION SELECT campo2 as id FROM tb2 ORDER BY id LIMIT 50", fuser.toPostgreSQL());
    }
}

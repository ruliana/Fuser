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

        assertEquals("ORACLE", "Select * from tabela", fuser.toORACLE());
        assertEquals("MYSQL", "Select * from tabela", fuser.toMYSQL());
        assertEquals("MSSQL", "Select * from tabela", fuser.toMSSQL());
        assertEquals("FIREBIRD", "Select * from tabela", fuser.toFIREBIRD());
        assertEquals("POSTGRES", "Select * from tabela", fuser.toPOSTGRES());
    }

    @Test
    public void concatAndSubstring() {
        Fuser fuser = new Fuser("Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela");

        assertEquals("ORACLE", "Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela", fuser.toORACLE());
        assertEquals("MSSQL", "Select tabela.campo1 + SUBSTRING(tabela.campo2 + tabela.campo3, 2, 3) + campo4 from tabela", fuser.toMSSQL());
        assertEquals("MYSQL", "Select CONCAT( tabela.campo1, SUBSTR( CONCAT(tabela.campo2, tabela.campo3), 2, 3), campo4) from tabela", fuser.toMYSQL());
        assertEquals("FIREBIRD", "Select tabela.campo1 || SUBSTRING(tabela.campo2 || tabela.campo3 FROM  2 FOR  3) || campo4 from tabela", fuser.toFIREBIRD());
        assertEquals("POSTGRES", "Select tabela.campo1 || SUBSTR(tabela.campo2 || tabela.campo3, 2, 3) || campo4 from tabela", fuser.toPOSTGRES());
    }

    @Test
    public void positionAndCharLength() {
        Fuser fuser = new Fuser("SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela");

        assertEquals("ORACLE", "SELECT substr(campo1, INSTR(campo1, 'B'), LENGTH(campo1) - 1) FROM tabela", fuser.toORACLE());
        assertEquals("MSSQL", "SELECT SUBSTRING(campo1, CHARINDEX('B', campo1), LEN(campo1) - 1) FROM tabela", fuser.toMSSQL());
        assertEquals("MYSQL", "SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toMYSQL());
        assertEquals("FIREBIRD", "SELECT SUBSTRING(campo1 FROM  POSITION('B' IN campo1) FOR  CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toFIREBIRD()); // TODO: Não achei o "POSITION para Firebird (Ronie)
        assertEquals("POSTGRES", "SELECT substr(campo1, POSITION('B' IN campo1), CHAR_LENGTH(campo1) - 1) FROM tabela", fuser.toPOSTGRES());
    }

    @Test
    public void switchCase() {
        Fuser fuser = new Fuser("SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela");

        assertEquals("ORACLE", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toORACLE());
        assertEquals("MSSQL", "SELECT CASE WHEN (campo1 + campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toMSSQL());
        assertEquals("MYSQL", "SELECT CASE WHEN ( CONCAT(campo1, campo2)) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toMYSQL()); // TODO: End Case funcionou q é uma beleza! Boa Samuel!!!
        assertEquals("FIREBIRD", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toFIREBIRD());
        assertEquals("POSTGRES", "SELECT CASE WHEN (campo1 || campo2) = 'YEAH!' THEN 1 ELSE 0 END FROM tabela", fuser.toPOSTGRES());
    }

    @Test
    public void trim(){
        Fuser fuser = new Fuser("SELECT trim(campo) FROM table1");

        assertEquals("MSSQL", "SELECT LTRIM(RTRIM(campo)) FROM table1", fuser.toMSSQL() );
        assertEquals("ORACLE", "SELECT trim(campo) FROM table1", fuser.toORACLE() );

        fuser = new Fuser("SELECT TRIM( campo || LOWER(campo2)) FROM Tabela");
        assertEquals("MSSQL", "SELECT LTRIM(RTRIM( campo + LOWER(campo2))) FROM Tabela", fuser.toMSSQL() );
        assertEquals("MYSQL", "SELECT TRIM( CONCAT( campo, LOWER(campo2))) FROM Tabela", fuser.toMYSQL() );
    }

}

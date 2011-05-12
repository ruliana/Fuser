package fuser.test;

import fuser.config.ParserConfig;
import fuser.config.SqlPrinter;
import static org.junit.Assert.assertEquals;
import static tekai.Expression.e;


import org.junit.Test;

import tekai.Expression;
import tekai.Parser;
import tekai.Printer;

public class PrinterTest {

    Parser p ;
    String sql;

    public PrinterTest(){
        p  = new Parser();
        ParserConfig.configureParser(p);
        sql = "";
    }

    @Test
    public void basicPrint() throws Exception {
        assertEquals("1 + 2", print(e(" +", "ARITHMETIC", e("1", "NUMBER"), e(" 2", "NUMBER"))));
        assertEquals("ABC(A, B)", print(e("ABC", "FUNCTION", e("A", "IDENTIFIER"), e(" B", "IDENTIFIER"))));
        assertEquals("ABC(A, DEF(1, 2, 3))",
         print(e("ABC", "FUNCTION", e("A", "IDENTIFIER"), e(" DEF", "FUNCTION", e("1", "NUMBER"), e(" 2", "NUMBER"), e(" 3", "NUMBER")))));
    }

    @Test
    public void testFrom(){
        sql = "SELECT campo FROM tabela";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT tabela.xx10 AS campo1, campo2,  campo3  FROM  tabela as tb1, tabela3";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT DISTINCT campo FROM tabela";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void functions(){
        sql = "SELECT getdate(), campo, campo2,  campo3  FROM  tabela, tabela3";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT POSITION('abc' IN campo2) FROM  tabela";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT cast(campo3 as VARCHAR) FROM  tabela";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testWhere(){
        sql = "SELECT campo FROM tabela where campo = 2";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT trim(campo), campo FROM tabela, tabela2 WHERE tabela.campo = 2 AND tabela.id = 3";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testConcat(){
        sql = "SELECT campo1 || 'string' || abc(campo3, campo4) FROM tabela";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testOrder(){
        sql = "SELECT  * FROM tabela WHERE campo = 2 AND campo2 = campo1 ORDER BY campo2, campo3, campo4";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT  * FROM tabela WHERE campo = 2 AND campo2 = campo1 ORDER BY campo2, campo3 DESC, campo4 DESC";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testLimit(){
        sql = "SELECT  * FROM tabela WHERE campo = 2  LIMIT 10";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT  * FROM tabela WHERE campo = :id ORDER BY campo2 LIMIT 10 OFFSET 0";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testGroup(){
        sql = "SELECT ax050.idinterno, ax050.descricao,    "
            + "                        'sistema' = RTRIM(ax050.idinterno) || ' - ' || RTRIM(ax050.descricao)    "
            + "           FROM AXT05000 AS ax050    "
            + "            WHERE ax050.Descricao like 'SERVIÇOS DE TI%'  "
            + " GROUP BY campo1, campo2";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testCase(){
        sql = "SELECT  CASE campo"
            + " WHEN 1  THEN  'Aguarda DistribuiÃ§Ã£o'"
            + " WHEN 2  THEN  'Em AnÃ¡lise'"
            + " ELSE ''  END "
            + " FROM sat00100 as sa001";
        assertEquals(sql, print(p.parse(sql)));
        sql = "SELECT CASE x*5 "
                + "WHEN 1 THEN msg = 'one or two' "
                + "ELSE msg = 'other value than one or two'"
                + "END "
                + "FROM TABELA";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testJoin(){
        sql = "SELECT C120.idcomercial, "
            + "        C120.idnome, "
            + "        X040.razsoc, "
            + "        X040.docto1 as cnpj,  "
            + "        X030.nomcid AS municipio,  "
            + "        X030.uf AS uf, "
            + "        chave_acesso = '                              ', "
            + "        data_acesso = '00/00/0000 00:00:00', "
            + "        X040.docto2 AS inscricao "
            + " FROM ACT12000 AS C120"
            + " INNER JOIN AXT04000 AS X040 ON X040.idnome = C120.idnome "
            + "   INNER JOIN AXT02000 AS X020A ON X020A.idparametro = C120.sitsis "
            + "   INNER JOIN AXT02000 AS X020B ON X020B.idparametro = C120.sitcom "
            + "   INNER JOIN AXT02000 AS X020C ON X020C.idparametro = C120.sitlas "
            + "   INNER JOIN AXT03000 AS X030  ON X030.idcidade     = X040.idcidade";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testParenthesis(){
        sql = "1 + (2 * (3 - 1))";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void subselect(){
        sql = "SELECT * from tabela where not exists(SELECT * from tabela)";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void union(){
        sql = "SELECT * from tabela UNION SELECT * FROM tb2";
        assertEquals(sql, print(p.parse(sql)));

        sql = "   SELECT DISTINCT ax050.idinterno, ax050.descricao,   "
            + "                      'sistema' = RTRIM(ax050.idinterno) + ' - ' + RTRIM(ax050.descricao)   "
            + "          FROM AXT05000 as ax050    "
            + "          JOIN ACT17700 as ac177 ON ac177.idinterno = ax050.idinterno    "
            + "               AND ax050.descricao NOT LIKE '%TAXA%'    "
            + "               AND ax050.descricao NOT LIKE '%DESCONTO%'    "
            + "          JOIN ACT18000 as ac180 ON ac180.iditemprop = ac177.iditemprop    "
            + "               AND ac180.cancelado <> 1    "
            + "          JOIN ACT03000 as ac030 ON ac030.idproposta = ac177.idproposta    "
            + "               AND ac030.tippro = 9    "
            + "          JOIN ACT02000 as ac020 ON ac020.idcontrato = ac180.idcontrato    "
            + "               AND ( ac020.situacao <> 2  AND    "
            + "                     ac020.dtrescisao IS NULL)   "
            + "        UNION    "
            + "        SELECT DISTINCT ax050.idinterno, ax050.descricao,    "
            + "                        'sistema' = RTRIM(ax050.idinterno) + ' - ' + RTRIM(ax050.descricao)    "
            + "           FROM AXT05000 as ax050    "
            + "            WHERE ax050.Descricao like 'SERVIÇOS DE TI%'";
        assertEquals(sql, print(p.parse(sql)));

        sql = "SELECT campo FROM tb1,tb2 WHERE 1=2 union all SELECT * FROM tb2 ORDER BY 1,2 LIMIT 10";
        assertEquals(sql, print(p.parse(sql)));

        sql = "select * from (select idpadraovalor, idpadrao from rat00300 union select idunidorc, idempresa from rat00800)as uid limit 10";
        assertEquals(sql, print(p.parse(sql)));
    }

    @Test
    public void testIn(){
        sql = "campo in(SELECT * FROM tb)";
        assertEquals(sql, print(p.parse(sql)));

        sql = "Select * FROM tb where campo NOT IN(1,2, 3,6)";
        assertEquals(sql, print(p.parse(sql)));
    }

    private String print(Expression e) {
        Printer p = new SqlPrinter();
        return p.print(e);
    }

}
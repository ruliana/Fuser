package fuser.test;

import fuser.config.ParserConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import org.junit.Test;

import tekai.Expression;
import tekai.Parser;

public class ParserTest {

    @Test
    public void justAnAtom() {
        assertParsing("Just a number", "[1]:NUMBER", "1");
        assertParsing("Just an identifier", "[abc]:IDENTIFIER", "abc");
    }

    @Test
    public void simpleExpression() {
        assertParsing("Simple infix", "([+]:ARITHMETIC [1]:NUMBER [2]:NUMBER)", "1 + 2");
        assertParsing("Double infix (left associativity)", "([+]:ARITHMETIC ([+]:ARITHMETIC [1]:NUMBER [2]:NUMBER) [3]:NUMBER)", "1 + 2 + 3");
        assertParsing("Double infix with parenthesis", "([+]:ARITHMETIC [1]:NUMBER ([(]:PARENTHESIS ([+]:ARITHMETIC [2]:NUMBER [3]:NUMBER)))", "1 + (2 + 3)");
    }

    @Test
    public void functions() {
        assertParsing("[abc]:FUNCTION", "abc()");
        assertParsing("([abc]:FUNCTION [1]:NUMBER)", "abc(1)");
        assertParsing("([abc]:FUNCTION [1]:NUMBER [2]:NUMBER)", "abc(1, 2)");
        assertParsing("([abc]:FUNCTION [1]:NUMBER [2]:NUMBER [3]:NUMBER)", "abc(1, 2, 3)");
        assertParsing("([+]:ARITHMETIC ([abc]:FUNCTION [4]:NUMBER) ([def]:FUNCTION [3]:NUMBER [2]:NUMBER))", "abc(4) + def(3, 2)");
        assertParsing("([abc]:FUNCTION ([+]:ARITHMETIC ([(]:PARENTHESIS ([+]:ARITHMETIC [2]:NUMBER [1]:NUMBER)) [3]:NUMBER))", "abc((2 + 1) + 3)");
        assertParsing("([+]:ARITHMETIC ([(]:PARENTHESIS ([+]:ARITHMETIC ([+]:ARITHMETIC [1]:NUMBER ([abc]:FUNCTION ([+]:ARITHMETIC [2]:NUMBER [3]:NUMBER) [4]:NUMBER)) [5]:NUMBER)) [6]:NUMBER)", "(1 + abc(2 + 3, 4) + 5) + 6");
        assertParsing("([abc]:FUNCTION ([def]:FUNCTION [1]:NUMBER) ([ghi]:FUNCTION [2]:NUMBER))", "abc(def(1), ghi(2))");
        assertParsing("([position]:FUNCTION-POSITION ['abc']:STRING [campo]:IDENTIFIER)", "position('abc' in campo)");
        assertParsing("([CAST]:FUNCTION ([as]:ALIAS [campo2]:IDENTIFIER [VARCHAR]:IDENTIFIER))", "CAST(campo2 as VARCHAR)");
    }

    @Test
    public void selectFrom() {
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [campo1]:IDENTIFIER [campo2]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER [tabela2]:IDENTIFIER))", "SELECT campo1, campo2 FROM tabela, tabela2");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER))", "SELECT * FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER ([INNER JOIN]:JOIN [outra_tabela]:IDENTIFIER ([ON]:ON [xxx]:IDENTIFIER))))", "SELECT * FROM tabela INNER JOIN outra_tabela ON xxx");

    }

    @Test
    public void selectWithWhere(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([+]:ARITHMETIC [campo]:IDENTIFIER [2]:NUMBER)))", "SELECT  * FROM tabela WHERE campo + 2");

        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([=]:OPERATOR [tabela.campo1]:IDENTIFIER [tabela.campo2]:IDENTIFIER)))", "SELECT  * FROM tabela WHERE tabela.campo1 = tabela.campo2");

        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([AND]:BOOLEAN ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER) ([=]:OPERATOR [id]:IDENTIFIER [3]:NUMBER))))",
            "SELECT * FROM tabela WHERE campo = 2 AND id = 3");

        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([OR]:BOOLEAN ([AND]:BOOLEAN ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER) ([=]:OPERATOR [id]:IDENTIFIER [:param]:PARAMETER)) ([=]:OPERATOR [campo]:IDENTIFIER [5.5]:NUMBER))))",
            "SELECT * FROM tabela WHERE campo = 2 AND id = :param OR campo = 5.5");

        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([OR]:BOOLEAN ([AND]:BOOLEAN ([(]:PARENTHESIS ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)) ([=]:OPERATOR [id]:IDENTIFIER [35.89]:NUMBER)) ([(]:PARENTHESIS ([=]:OPERATOR [campo]:IDENTIFIER [5]:NUMBER)))))",
            "SELECT * FROM tabela WHERE (campo = 2) AND id = 35.89 OR (campo = 5)");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([>]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)))", "SELECT  * FROM tabela WHERE campo >2");
    }

    @Test
    public void selectWithAlias(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [tb.campo1]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER))", "SELECT tb.campo1 FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([AS]:ALIAS [campo]:IDENTIFIER [nome]:IDENTIFIER)) ([FROM]:FROM [tabela]:IDENTIFIER))", "SELECT campo AS nome FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([AS]:ALIAS [tb.campo1]:IDENTIFIER [nome]:IDENTIFIER)) ([FROM]:FROM [tabela]:IDENTIFIER))", "SELECT tb.campo1 AS nome FROM tabela");
    }

    @Test
    public void selectWithConcat(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([||]:CONCAT [campo1]:IDENTIFIER [campo2]:IDENTIFIER)) ([FROM]:FROM [tabela]:IDENTIFIER))", "SELECT campo1 || campo2 FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([||]:CONCAT [campo1]:IDENTIFIER [campo2]:IDENTIFIER [campo3]:IDENTIFIER)) ([FROM]:FROM [tabela]:IDENTIFIER))",
                "SELECT campo1 || campo2 || campo3 FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([||]:CONCAT [campo1]:IDENTIFIER [campo2]:IDENTIFIER ([abc]:FUNCTION [campo3]:IDENTIFIER [campo4]:IDENTIFIER))) ([FROM]:FROM [tabela]:IDENTIFIER))",
                "SELECT campo1 || campo2 || abc(campo3, campo4) FROM tabela");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([||]:CONCAT [campo1]:IDENTIFIER ['string']:STRING ([abc]:FUNCTION [campo3]:IDENTIFIER [campo4]:IDENTIFIER))) ([FROM]:FROM [tabela]:IDENTIFIER))",
                "SELECT campo1 || 'string' || abc(campo3, campo4) FROM tabela");
    }

    @Test
    public void selectWithJoin(){
        String expected =
"([SQL]:SQL\n" +
"  ([SELECT]:SELECT\n" +
"    [C120.idcomercial]:IDENTIFIER\n" +
"    [C120.idnome]:IDENTIFIER\n" +
"    [X040.razsoc]:IDENTIFIER\n"  +
"    ([as]:ALIAS [X040.docto1]:IDENTIFIER [cnpj]:IDENTIFIER)\n"  +
"    ([AS]:ALIAS [X030.nomcid]:IDENTIFIER [municipio]:IDENTIFIER)\n" +
"    ([AS]:ALIAS [X030.uf]:IDENTIFIER [uf]:IDENTIFIER)\n" +
"    ([=]:OPERATOR [chave_acesso]:IDENTIFIER ['                              ']:STRING)\n" +
"    ([=]:OPERATOR [data_acesso]:IDENTIFIER ['00/00/0000 00:00:00']:STRING)\n" +
"    ([AS]:ALIAS [X040.docto2]:IDENTIFIER [inscricao]:IDENTIFIER))\n" +
"  ([FROM]:FROM\n" +
"    ([AS]:ALIAS [ACT12000]:IDENTIFIER [C120]:IDENTIFIER)\n" +
"    ([INNER JOIN]:JOIN\n" +
"      ([AS]:ALIAS [AXT04000]:IDENTIFIER [X040]:IDENTIFIER)\n"  +
"      ([ON]:ON ([=]:OPERATOR [X040.idnome]:IDENTIFIER [C120.idnome]:IDENTIFIER)))\n" +
"    ([INNER JOIN]:JOIN\n" +
"      ([AS]:ALIAS [AXT02000]:IDENTIFIER [X020A]:IDENTIFIER)\n" +
"      ([ON]:ON ([=]:OPERATOR [X020A.idparametro]:IDENTIFIER [C120.sitsis]:IDENTIFIER)))\n" +
"    ([INNER JOIN]:JOIN\n" +
"      ([AS]:ALIAS [AXT02000]:IDENTIFIER [X020B]:IDENTIFIER)\n" +
"      ([ON]:ON ([=]:OPERATOR [X020B.idparametro]:IDENTIFIER [C120.sitcom]:IDENTIFIER)))\n" +
"    ([INNER JOIN]:JOIN\n" +
"      ([AS]:ALIAS [AXT02000]:IDENTIFIER [X020C]:IDENTIFIER)\n" +
"      ([ON]:ON ([=]:OPERATOR [X020C.idparametro]:IDENTIFIER [C120.sitlas]:IDENTIFIER)))\n" +
"    ([INNER JOIN]:JOIN\n" +
"      ([AS]:ALIAS [AXT03000]:IDENTIFIER [X030]:IDENTIFIER)\n" +
"      ([ON]:ON ([=]:OPERATOR [X030.idcidade]:IDENTIFIER [X040.idcidade]:IDENTIFIER)))))";

        Pattern spaces = Pattern.compile("\n\\s+", Pattern.MULTILINE);
        assertParsing(spaces.matcher(expected).replaceAll(" "), " SELECT C120.idcomercial, "
            + "        C120.idnome, "
            + "        X040.razsoc, "
            + "        X040.docto1 as cnpj,  "
            + "        X030.nomcid AS municipio,  "
            + "        X030.uf AS uf, "
            + "        chave_acesso = '                              ', "
            + "        data_acesso = '00/00/0000 00:00:00', "
            + "        X040.docto2 AS inscricao "
            + " FROM ACT12000 AS C120 "
            + " INNER JOIN AXT04000 AS X040 ON X040.idnome = C120.idnome "
            + "   INNER JOIN AXT02000 AS X020A ON X020A.idparametro = C120.sitsis "
            + "   INNER JOIN AXT02000 AS X020B ON X020B.idparametro = C120.sitcom "
            + "   INNER JOIN AXT02000 AS X020C ON X020C.idparametro = C120.sitlas "
            + "   INNER JOIN AXT03000 AS X030  ON X030.idcidade     = X040.idcidade ");
     }

     @Test
    public void selectOrder(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)) ([ORDER BY]:ORDER [campo2]:IDENTIFIER))", "SELECT  * FROM tabela WHERE campo = 2 ORDER BY campo2");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)) ([ORDER BY]:ORDER [campo2]:IDENTIFIER [campo3]:IDENTIFIER [campo4]:IDENTIFIER))", "SELECT  * FROM tabela WHERE campo = 2 ORDER BY campo2, campo3, campo4");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([ORDER BY]:ORDER [campo2]:IDENTIFIER ([DESC]:ORDERING [campo3]:IDENTIFIER) ([ASC]:ORDERING [campo4]:IDENTIFIER)))", "SELECT  * FROM tabela ORDER BY campo2, campo3 DESC, campo4 ASC");
    }

    @Test
    public void selectLimit(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)) ([LIMIT]:LIMIT [10]:NUMBER))", "SELECT  * FROM tabela WHERE campo = 2  LIMIT 10");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([=]:OPERATOR [campo]:IDENTIFIER [2]:NUMBER)) ([ORDER BY]:ORDER [campo2]:IDENTIFIER) ([LIMIT]:LIMIT [10]:NUMBER ([OFFSET]:OFFSET [0]:NUMBER)))", "SELECT  * FROM tabela WHERE campo = 2 ORDER BY campo2 LIMIT 10 OFFSET 0");
    }

     @Test
    public void TestCase(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([CASE]:CASE [campo]:IDENTIFIER ([WHEN]:WHEN [1]:NUMBER ([THEN]:THEN ['Aguarda DistribuiÃ§Ã£o']:STRING)) ([WHEN]:WHEN [2]:NUMBER ([THEN]:THEN ['Em AnÃ¡lise']:STRING)) ([ELSE]:ELSE ['']:STRING) [END]:END)) ([FROM]:FROM ([as]:ALIAS [sat00100]:IDENTIFIER [sa001]:IDENTIFIER)))",
              " SELECT  CASE campo"
            + " WHEN 1  THEN  'Aguarda DistribuiÃ§Ã£o'"
            + " WHEN 2  THEN  'Em AnÃ¡lise'"
            + " ELSE ''  END "
            + " FROM sat00100 as sa001");

        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([CASE]:CASE ([*]:ARITHMETIC [x]:IDENTIFIER [5]:NUMBER) ([WHEN]:WHEN [1]:NUMBER ([THEN]:THEN ([=]:OPERATOR [msg]:IDENTIFIER ['one or two']:STRING))) ([ELSE]:ELSE ([=]:OPERATOR [msg]:IDENTIFIER ['other value than one or two']:STRING)) [END]:END)) ([FROM]:FROM [TABELA]:IDENTIFIER))",
                "SELECT CASE x*5 "
                + "WHEN 1 THEN msg = 'one or two' "
                + "ELSE msg = 'other value than one or two'"
                + "END "
                + "FROM TABELA");

    }

     @Test
    public void subSelect(){
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [campo]:IDENTIFIER) ([FROM]:FROM ([(]:PARENTHESIS ([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER)))))",
                "SELECT campo FROM (SELECT * FROM tabela)");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT [campo]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER) ([WHERE]:WHERE ([EXISTS]:FUNCTION ([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER)))))",
                "SELECT campo FROM tabela WHERE EXISTS(SELECT * FROM tabela)");
        assertParsing("([SQL]:SQL ([SELECT]:SELECT ([CASE]:CASE [campo]:IDENTIFIER ([WHEN]:WHEN ([EXISTS]:FUNCTION ([SQL]:SQL ([SELECT]:SELECT [*]:IDENTIFIER) ([FROM]:FROM [tabela]:IDENTIFIER))) ([THEN]:THEN ['ok']:STRING)) [END]:END)) ([FROM]:FROM [tabela]:IDENTIFIER))",
                "SELECT CASE campo WHEN EXISTS(SELECT * FROM tabela) THEN 'ok' END FROM tabela");


    }

     @Test
     public void SelectGroup(){
          assertParsing("([SQL]:SQL ([SELECT]:SELECT [DISTINCT]:DISTINCT [ax050.idinterno]:IDENTIFIER [ax050.descricao]:IDENTIFIER ([||]:CONCAT ([=]:OPERATOR ['sistema']:STRING ([RTRIM]:FUNCTION [ax050.idinterno]:IDENTIFIER)) [' - ']:STRING ([RTRIM]:FUNCTION [ax050.descricao]:IDENTIFIER))) ([FROM]:FROM ([AS]:ALIAS [AXT05000]:IDENTIFIER [ax050]:IDENTIFIER)) ([WHERE]:WHERE ([like]:LIKE [ax050.Descricao]:IDENTIFIER ['SERVIÇOS DE TI%']:STRING)) ([GROUP BY]:GROUP [campo1]:IDENTIFIER [campo2]:IDENTIFIER))",
                "SELECT DISTINCT ax050.idinterno, ax050.descricao,    "
            + "                        'sistema' = RTRIM(ax050.idinterno) || ' - ' || RTRIM(ax050.descricao)    "
            + "           FROM AXT05000 AS ax050    "
            + "            WHERE ax050.Descricao like 'SERVIÇOS DE TI%'  "
            + " GROUP BY campo1, campo2");
     }

     @Test
     public void notIs(){
         assertParsing("([NOT]:NOT [1]:NUMBER)", "NOT 1");
         assertParsing("([NOT]:NOT ([abc]:FUNCTION [1]:NUMBER))", "NOT abc(1)");
         assertParsing("([NOT  LIKE]:LIKE [campo]:IDENTIFIER ['teste']:STRING)", "campo NOT  LIKE 'teste'");
         assertParsing("([is]:IS [campo]:IDENTIFIER [null]:IDENTIFIER)", " campo is null");
         assertParsing("([IS]:IS [campo]:IDENTIFIER ([NOT]:NOT [NULL]:IDENTIFIER))", "campo IS NOT NULL");
     }

     @Test
     public void union(){
         assertParsing("([SQL]:SQL ([UNION]:UNION ([SQL]:SQL ([SELECT]:SELECT [c1]:IDENTIFIER) ([FROM]:FROM [Tb1]:IDENTIFIER)) ([SQL]:SQL ([UNION]:UNION ([SQL]:SQL ([SELECT]:SELECT [c2]:IDENTIFIER) ([FROM]:FROM [Tb2]:IDENTIFIER)) ([SQL]:SQL ([SELECT]:SELECT [c3]:IDENTIFIER) ([FROM]:FROM [Tb3]:IDENTIFIER))))))",
                 "SELECT c1 FROM Tb1 UNION SELECT c2 FROM Tb2 UNION SELECT c3 FROM Tb3");
         assertParsing("([SQL]:SQL ([UNION ALL]:UNION ([SQL]:SQL ([SELECT]:SELECT [c1]:IDENTIFIER) ([FROM]:FROM [Tb1]:IDENTIFIER)) ([SQL]:SQL ([SELECT]:SELECT [c2]:IDENTIFIER) ([FROM]:FROM [Tb2]:IDENTIFIER) ([ORDER BY]:ORDER [1]:NUMBER))))",
                 "SELECT c1 FROM Tb1 UNION ALL SELECT c2 FROM Tb2 ORDER BY 1");
     }

    @Test
    public void exceptions() {
        // TODO Launch specific exception to specific problems
        // TODO Add more and more contextual information to error messages
        try {
            parse("1 +");
            fail("Expected not able to parse an incomplete expression \"1 +\"");
        } catch (Exception e) {
            // success
        }
    }

    // == Helpers ==

    private void assertParsing(String expected, String source) {
        assertParsing(null, expected, source);
    }

    private void assertParsing(String message, String expected, String source) {
        Expression expression = parse(source);
        assertEquals(message, expected, expression.toString());
    }

    public Expression parse(String source) {
        Parser parser = new Parser(source);
        ParserConfig.configureParser(parser);
        Expression expression = parser.parse();
        return expression;
    }

}

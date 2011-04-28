package fuser.test;

import fuser.Fuser;
import org.junit.Test;

/**
 *
 * @author SFPISA
 */
public class FuserTest {

    @Test
    public void test1(){
        Fuser fuser = new Fuser("Select * from tabela");

        for (String s : fuser.fusion()) {
            System.out.println(s);
        }

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import interpretator.BlockManager;
import interpretator.Interpretator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hoodad
 */
public class FirstFileTest {
    
    boolean successfulRun;
    public FirstFileTest() {
    }
        
    @Before
    public void setUp() {
        successfulRun = true;
    }
    
    @After
    public void tearDown() {
    }
    @Test
    public void tst0() {
        runTest("tst0.p1");
        assertEquals(successfulRun, true);
    }
    @Test
    public void tst1() {
        runTest("tst1.p1");
        assertEquals(successfulRun, true);
    }
    
    @Test
    public void tst2() {
        runTest("tst2.p1");
        assertEquals(successfulRun, true);
    }
    
    @Test
    public void tst3() {
        runTest("tst3.p1");
        assertEquals(successfulRun, true);
    }
    
    @Test
    public void tst4() {
        runTest("tst4.p1");
        assertEquals(successfulRun, true);
    }
    
    
    private void runTest(String p_testProg){
        try{
            BlockManager.loadBlocksFromFile(p_testProg);
            Interpretator.interpretBlock(BlockManager.getBaseBlock());
        }catch(Exception e){
            successfulRun = false;
        }
    }
}
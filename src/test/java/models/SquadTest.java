package models;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import static org.junit.Assert.*;

public class SquadTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void squadObjectInstantiatesCorrectly_true()throws Exception{
        Squad newSquad = setupNewSquad();
        assertTrue(newSquad instanceof Squad);

    }

    @Test
    public void squadInstantiateWithName_XMen() throws Exception {
        Squad squad = setupNewSquad();
        assertEquals("X-Men", squad.getName());
    }

    @Test
    public void squadInstantiateWithSize_12() throws Exception {
        Squad squad = setupNewSquad();
        assertEquals(12, squad.getSize());
    }

    @Test
    public void squadInstantiateWithCause_FightingCrime() throws Exception {
        Squad squad = setupNewSquad();
        assertEquals("Fighting Crime", squad.getCause());
    }

    public Squad setupNewSquad(){
        return new Squad("X-Men",12,"Fighting Crime");
    }
}
package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HeroTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void checkForCorrectInstantiationOfHeroClass_true() throws Exception{
        Hero hero = setUpNewHero();
        assertTrue(hero instanceof Hero);
    }



    @Test
    public void HeroInstantiatesWithName_Wolverine() throws Exception {
        Hero hero = setUpNewHero();
        assertEquals("Wolverine", hero.getName());
    }


    @Test
    public void HeroInstantiatesWithWeakness_magnets() throws Exception {
        Hero hero = setUpNewHero();
        assertEquals("Magnets", hero.getWeakness());
    }

    @Test
    public void HeroInstantiatesWithPower_metalClaws() throws Exception {
        Hero hero = setUpNewHero();
        assertEquals("Metal Claws", hero.getPower());
    }


    @Test
    public void HeroInstantiatesWithAge_age() throws Exception {
        Hero hero = setUpNewHero();
        assertEquals(78, hero.getAge());
    }

    @Test
    public void HeroInstantiatesWithSquad_XMen() throws Exception {
        Hero hero = setUpNewHero();
        assertEquals(1, hero.getSquadId());
    }


    public Hero setUpNewHero(){
        return new Hero("Wolverine", 78, "Metal Claws", "Magnets",1);
    }

}
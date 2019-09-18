package dao;

import models.Squad;
import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;

public class Sql2oSquadDaoTest {

    private static Sql2oSquadDao squadDao;
    private static Sql2oHeroDao heroDao;
    private static Connection conn;


    @Test
    public void addingSquadSetsId() throws Exception {
        Squad squad = setUpNewSquad();
        int originalSquadId = squad.getId();
        squadDao.add(squad);
        assertNotEquals(originalSquadId, squad.getId());
    }

    @Test
    public void aSquadIsSuccessfullyAdded() throws Exception {
        Squad squad = setUpNewSquad();
        squadDao.add(squad);
        assertTrue(squadDao.getAll().contains(squad));
    }

    @Test
    public void existingSquadsCanBeFoundById() throws Exception {
        Squad squad = setUpNewSquad();
        squadDao.add(squad);
        Squad foundSquad = squadDao.findById(squad.getId());
        assertEquals(squad, foundSquad);
    }

    @Test
    public void existingSquadCanBeUpdated() throws Exception {
        Squad squad = setUpNewSquad();
        squadDao.add(squad);
        squadDao.update(squad.getId(),"Marvel",14,"fighting Aliens");
        Squad foundSquad = squadDao.findById(squad.getId());
        assertNotEquals(squad, foundSquad);
    }

    @Test
    public void existingSquadCanBeDeleted() throws Exception {
        Squad squad = setUpNewSquad();
        squadDao.add(squad);
        squadDao.deleteById(squad.getId());
        assertEquals(0, squadDao.getAll().size());
    }

    @Test
    public void AllExistingSquadsCanBeDeleted() throws Exception {
        Squad squad = setUpNewSquad();
        Squad otherSquad = setUpNewSquad();
        squadDao.add(squad);
        squadDao.clearAllSquads();
        assertFalse(squadDao.getAll().contains(squad));
        assertFalse(squadDao.getAll().contains(otherSquad));
        assertEquals(0,squadDao.getAll().size());
    }

    @BeforeClass
    public static void setUp() throws Exception {
        String connectionString = "jdbc:postgresql://localhost:5432/heroes_test";
        Sql2o sql2o = new Sql2o(connectionString, "baraka", "fRankline");
        squadDao = new Sql2oSquadDao(sql2o);
        heroDao = new Sql2oHeroDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("clearing database");
        squadDao.clearAllSquads();
        heroDao.clearAllHeroes();
    }

    @AfterClass
    public static void shutDown() throws Exception {
        conn.close();
        System.out.println("connection closed");
    }

    public Squad setUpNewSquad(){
        return new Squad("X-men",45,"Fighting Mutants");
    }
}

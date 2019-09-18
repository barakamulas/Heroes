import static spark.Spark.*;

import dao.Sql2oHeroDao;
import dao.Sql2oSquadDao;
import models.Hero;
import models.Squad;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class App {

    public static void main(String[] args) {



        staticFileLocation("/public");
//        String connectionString = "jdbc:postgresql://localhost:5432/heroes";
//        Sql2o sql2o = new Sql2o(connectionString, "baraka", "fRankline");
        String connectionString = "jdbc:postgres://hhmgarbtrfefza:529f5b88d4e82a1486da5dee9c0802d62032029d23bebed9789a01248b79d088@ec2-54-235-180-123.compute-1.amazonaws.com:5432/d9jh1sfpbob72s";
        Sql2o sql2o = new Sql2o(connectionString, "hhmgarbtrfefza", "529f5b88d4e82a1486da5dee9c0802d62032029d23bebed9789a01248b79d088");
        Sql2oHeroDao heroDao = new Sql2oHeroDao(sql2o);
        Sql2oSquadDao squadDao = new Sql2oSquadDao(sql2o);

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
//            List<Squad> squads = squadDao.getAll();
//            List<Hero> heroes = heroDao.getAll();
//            model.put("squads", squads);
//            model.put("heroes", heroes);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


        get("/heroes", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Hero> heroes = heroDao.getAll();
            List<Squad> squads = squadDao.getAll();
            model.put("heroes", heroes);
            return new ModelAndView(model, "all-heroes.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            return new ModelAndView(model, "all-squads.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> squads = squadDao.getAll();
            model.put("squads", squads);
            return new ModelAndView(model, "squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String name = req.queryParams("name");
            List<Squad> squads = squadDao.getAll();
            int maxSize = Integer.parseInt(req.queryParams("size"));
            String cause = req.queryParams("cause");
            Squad newSquad = new Squad(name,maxSize,cause);
            squadDao.add(newSquad);
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            heroDao.clearAllHeroes();
            squadDao.clearAllSquads();
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/heroes/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            heroDao.clearAllHeroes();
            res.redirect("/heroes");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquadToFind = Integer.parseInt(req.params("id"));
            Squad foundSquad = squadDao.findById(idOfSquadToFind);
            model.put("squad", foundSquad);
            List<Hero> allHeroesBySquad = squadDao.getAllHeroesBySquad(idOfSquadToFind);
            model.put("heroes", allHeroesBySquad);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "squad-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("editSquad", true);
            Squad squad = squadDao.findById(Integer.parseInt(req.params("id")));
            model.put("heroes", heroDao.getAll());
            model.put("squad", squad);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquadToEdit = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newName");
            int newMaxSize = Integer.parseInt(req.queryParams("newSize"));
            String newCause = req.queryParams("newCause");
            squadDao.update(idOfSquadToEdit, newName,newMaxSize,newCause);
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/delete", (req, res) -> {
            int idOfSquadToDelete = Integer.parseInt(req.params("id"));
            squadDao.deleteAllHeroesInSquad(idOfSquadToDelete);
            squadDao.deleteById(idOfSquadToDelete);
            squadDao.deleteAllHeroesInSquad(idOfSquadToDelete);
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquadToDelete = Integer.parseInt(req.params("id"));
            Squad squad = squadDao.findById(idOfSquadToDelete);
            model.put("squad", squad);
            return modelAndView(model,"hero-to-squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/:id/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquad = Integer.parseInt(req.params("id"));
            int age = Integer.parseInt(req.queryParams("age"));
            String name = req.queryParams("name");
            String power = req.queryParams("power");
            String weakness = req.queryParams("weakness");
            Hero hero = new Hero(name,age,power,weakness,idOfSquad);
            heroDao.add(hero);
            Squad squad = squadDao.findById(idOfSquad);
            model.put("squad", squad);
            model.put("heroes", heroDao.getAll());
            model.put("squads", squadDao.getAll());
            return modelAndView(model,"squad-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/heroes/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
            Hero hero = heroDao.findById(idOfHeroToDelete);
            Squad squad = squadDao.findById(hero.getSquadId());
            heroDao.deleteById(idOfHeroToDelete);
            model.put("squad", squad);
            return modelAndView(model,"squad-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
            heroDao.deleteById(idOfHeroToDelete);
            res.redirect("/heroes");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Hero> heroes = heroDao.getAll();
            List<Squad> squads = squadDao.getAll();
            model.put("heroes", heroes);
            model.put("squads", squads);
            return new ModelAndView(model, "hr-form.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHeroToFind = Integer.parseInt(req.params("id"));
            Hero foundHero = heroDao.findById(idOfHeroToFind);
            int idOfSquad = foundHero.getSquadId();
            Squad squad = squadDao.findById(idOfSquad);
            model.put("squad", squad);
            model.put("hero", foundHero);
            model.put("heroes", heroDao.getAll());
            return new ModelAndView(model, "hero-detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Hero> heroes = heroDao.getAll();
            model.put("heroes", heroes);
            String name = req.queryParams("name");
            int SquadId = Integer.parseInt(req.queryParams("squadId"));
            int age = Integer.parseInt(req.queryParams("age"));
            String weakness = req.queryParams("weakness");
            String power = req.queryParams("power");
            Hero newHero = new Hero(name, age, power, weakness, SquadId);
            heroDao.add(newHero);
            res.redirect("/heroes");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            Hero hero = heroDao.findById(Integer.parseInt(req.params("id")));
            model.put("hero", hero);
            model.put("editHero", true);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "hr-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/heroes/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int heroToEditId = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newName");
            int newSquadId = Integer.parseInt(req.queryParams("newSquadId"));
            int newAge = Integer.parseInt(req.queryParams("newAge"));
            String newWeakness = req.queryParams("newWeakness");
            String newSpecialPower = req.queryParams("newPower");
            heroDao.update(heroToEditId, newName, newAge, newSpecialPower, newWeakness, newSquadId);
            List<Hero> heroes = heroDao.getAll();
            Hero hero = heroDao.findById(heroToEditId);
            model.put("squad", squadDao.findById(hero.getSquadId()));
            model.put("heroes",heroes);
            model.put("hero",hero);
            return new ModelAndView(model, "hero-detail.hbs");
        }, new HandlebarsTemplateEngine());
     }
}


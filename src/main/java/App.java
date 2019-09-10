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
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {

        staticFileLocation("/public");
        String connectionString = "jdbc:h2:~/hero-squad.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oHeroDao heroDao = new Sql2oHeroDao(sql2o);
        Sql2oSquadDao squadDao = new Sql2oSquadDao(sql2o);

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());


        get("/heroes", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Hero> heroes = heroDao.getAll();
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
            int maxSize = Integer.parseInt(req.queryParams("size"));
            String cause = req.queryParams("cause");
            Squad newSquad = new Squad(name,maxSize,cause);
            squadDao.add(newSquad);
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());



        get("/squads/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            squadDao.clearAllSquads();
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());



//        //get: delete all Heroes
//        get("/heroes/delete", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            heroDao.clearAll();
//            res.redirect("/heroes");
//            return null;
//        }, new HandlebarsTemplateEngine());
//
//        //get a specific Squad (and the Heroes it contains)
//        get("/squads/:id", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            int idOfSquadToFind = Integer.parseInt(req.params("id")); //new
//            Squad foundSquad = squadDao.findById(idOfSquadToFind);
//            model.put("Squad", foundSquad);
//            List<Hero> allHeroesBySquad = squadDao.getAllHeroesBySquad(idOfSquadToFind);
//            model.put("heroes", allHeroesBySquad);
//            model.put("squads", squadDao.getAll()); //refresh list of links for navbar
//            return new ModelAndView(model, "squad-detail.hbs"); //new
//        }, new HandlebarsTemplateEngine());
//
//        //get: show a form to update a Squad
//        get("/squads/:id/edit", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            model.put("editSquad", true);
//            Squad squad = squadDao.findById(Integer.parseInt(req.params("id")));
//            model.put("squad", squad);
//            model.put("squads", squadDao.getAll()); //refresh list of links for navbar
//            return new ModelAndView(model, "squad-form.hbs");
//        }, new HandlebarsTemplateEngine());
//
//        //post: process a form to update a Squad
//        post("/squads/:id", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            int idOfSquadToEdit = Integer.parseInt(req.params("id"));
//            String newName = req.queryParams("newSquadName");
//            int newMaxSize = Integer.parseInt(req.params("newMaxSize"));
//            String newCause = req.queryParams("newCause");
//            squadDao.update(idOfSquadToEdit, newName,newMaxSize,newCause);
//            res.redirect("/squads/:id");
//            return null;
//        }, new HandlebarsTemplateEngine());
//
//        //get: delete an individual Hero
//        get("/heroes/:hero_id/delete", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
//            heroDao.deleteById(idOfHeroToDelete);
//            res.redirect("/heroes");
//            return null;
//        }, new HandlebarsTemplateEngine());
//

        get("/heroes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());
//
//        //Hero: process new Hero form
//        post("/heroes", (req, res) -> { //URL to make new Hero on POST route
//            Map<String, Object> model = new HashMap<>();
//            List<Squad> allSquads = squadDao.getAll();
//            model.put("squads", allSquads);
//            String name = req.queryParams("name");
//            int SquadId = Integer.parseInt(req.queryParams("squadId"));
//            int age = Integer.parseInt(req.queryParams("age"));
//            String weakness = req.queryParams("weakness");
//            String specialPower = req.queryParams("specialPower");
//            Hero newHero = new Hero(name, age, specialPower, weakness, SquadId);        //See what we did with the hard coded SquadId?
//            heroDao.add(newHero);
//            res.redirect("/heroes");
//            return null;
//        }, new HandlebarsTemplateEngine());
//
//        //get: show an individual Hero that is nested in a Squad
//        get("/squads/:Squad_id/Heroes/:hero_id", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            int idOfHeroToFind = Integer.parseInt(req.params("squadId")); //pull id - must match route segment
//            Hero foundHero = heroDao.findById(idOfHeroToFind); //use it to find Hero
//            int idOfSquadToFind = Integer.parseInt(req.params("id"));
//            Squad foundSquad = squadDao.findById(idOfSquadToFind);
//            model.put("squad", foundSquad);
//            model.put("Hero", foundHero);
//            model.put("squads", squadDao.getAll());
//            return new ModelAndView(model, "hero-detail.hbs"); //individual Hero page.
//        }, new HandlebarsTemplateEngine());
//
//        //get: show a form to update a Hero
//        get("/Heroes/:id/edit", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            List<Squad> allSquads = squadDao.getAll();
//            model.put("squads", allSquads);
//            Hero hero = heroDao.findById(Integer.parseInt(req.params("id")));
//            model.put("hero", hero);
//            model.put("editHero", true);
//            return new ModelAndView(model, "hero-form.hbs");
//        }, new HandlebarsTemplateEngine());
//
//        //Hero: process a form to update a Hero
//        post("/heroes/:id", (req, res) -> { //URL to update Hero on POST route
//            Map<String, Object> model = new HashMap<>();
//            int heroToEditId = Integer.parseInt(req.params("id"));
//            String newName = req.queryParams("newName");
//            int newSquadId = Integer.parseInt(req.queryParams("newSquadId"));
//            String newWeakness = req.queryParams("newWeakness");
//            String newSpecialPower = req.queryParams("newSpecialPower");
//            heroDao.update(heroToEditId, newName, newSquadId,newSpecialPower,newWeakness);  // remember the hardcoded SquadId we placed? See what we've done to/with it?
//            res.redirect("/");
//            return null;
//        }, new HandlebarsTemplateEngine());
     }
}


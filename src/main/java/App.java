import static spark.Spark.*;

import dao.Sql2oHeroDao;
import dao.Sql2oSquadDao;
import models.Hero;
import models.Squad;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.utils.IOUtils;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


public class App {

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4500; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) throws IOException {
        port(getHerokuAssignedPort());
        staticFiles.location("/public");
        File file = new File("src/main/resources/public/images/default.png");//default image for heroes
        InputStream fis = new FileInputStream(file);
        //create FileInputStream which obtains input bytes from a file in a file system
        //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
        byte[] bytes = IOUtils.toByteArray(fis); //Convert the inputStream to bytearray
        Sql2oHeroDao heroDao = new Sql2oHeroDao();
        Sql2oSquadDao squadDao = new Sql2oSquadDao();
        Map<String, Object> model = new HashMap<>();
        

        get("/", (req, res) -> {
            List<Squad> squads = squadDao.getAll();
            List<Hero> heroes = heroDao.getAll();
            model.put("squads", squads);
            model.put("heroes", heroes);
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());       

        get("/squads", (req, res) -> {
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            return new ModelAndView(model, "all-squads.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads/new", (req, res) -> {
            List<Squad> squads = squadDao.getAll();
            model.put("squads", squads);
            return new ModelAndView(model, "squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/new", (req, res) -> {
            String name = req.queryParams("name");
            List<Squad> squads = squadDao.getAll();
            List<String> squadNames = new ArrayList<>();
            for (Squad squad : squads) {
                squadNames.add(squad.getName().toLowerCase());
            }
            int maxSize = Integer.parseInt(req.queryParams("size"));
            String cause = req.queryParams("cause");
            Squad newSquad = new Squad(name, maxSize, cause);
            if (squadNames.contains(newSquad.getName().toLowerCase())) {
                return new ModelAndView(model, "squad-exists.hbs");
            }
            squadDao.add(newSquad);
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/delete", (req, res) -> {
            heroDao.clearAllHeroes();
            squadDao.clearAllSquads();
            res.redirect("/squads");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/squads/:id", (req, res) -> {
            int idOfSquadToFind = Integer.parseInt(req.params("id"));
            Squad foundSquad = squadDao.findById(idOfSquadToFind);
            model.put("squad", foundSquad);
            List<Hero> allHeroesBySquad = squadDao.getAllHeroesBySquad(idOfSquadToFind);
            model.put("heroes", allHeroesBySquad);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "squad-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/edit", (req, res) -> {
            model.put("editSquad", true);
            Squad squad = squadDao.findById(Integer.parseInt(req.params("id")));
            model.put("heroes", heroDao.getAll());
            model.put("squad", squad);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/:id/edit", (req, res) -> {
            int idOfSquadToEdit = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newName");
            int newMaxSize = Integer.parseInt(req.queryParams("newSize"));
            String newCause = req.queryParams("newCause");
            squadDao.update(idOfSquadToEdit, newName, newMaxSize, newCause);
            model.put("squads", squadDao.getAll());
            model.put("squad", squadDao.findById(idOfSquadToEdit));
            model.put("heroes", squadDao.getAllHeroesBySquad(idOfSquadToEdit));
            return new ModelAndView(model, "squad-detail.hbs");
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
            int idOfSquadToDelete = Integer.parseInt(req.params("id"));
            Squad squad = squadDao.findById(idOfSquadToDelete);
            model.put("squad", squad);
            return modelAndView(model, "hero-to-squad-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/squads/:id/heroes/new", (req, res) -> {
            int idOfSquad = Integer.parseInt(req.params("id"));
            int age = Integer.parseInt(req.queryParams("age"));
            String name = req.queryParams("name");
            String power = req.queryParams("power");
            String weakness = req.queryParams("weakness");
            Hero hero = new Hero(name, age, power, weakness, idOfSquad, bytes);
            if (squadDao.getAllHeroesBySquad(idOfSquad).size() == squadDao.findById(idOfSquad).getSize()) {
                model.put("heroes", heroDao.getAll());
                model.put("squads", squadDao.getAll());
                return modelAndView(model, "squad-full.hbs");
            } else {
                heroDao.add(hero);
                Squad squad = squadDao.findById(idOfSquad);
                model.put("squad", squad);
                model.put("heroes", squadDao.getAllHeroesBySquad(idOfSquad));
                model.put("squads", squadDao.getAll());
                return modelAndView(model, "squad-detail.hbs");
            }
        }, new HandlebarsTemplateEngine());

        get("/squads/:id/heroes/delete", (req, res) -> {
            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
            System.out.println("Hero ID: " + idOfHeroToDelete);
            Hero hero = heroDao.findById(idOfHeroToDelete);
            Squad squad = squadDao.findById(hero.getSquadId());
            heroDao.deleteById(idOfHeroToDelete);
            model.put("squad", squad);
            return modelAndView(model, "squad-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes", (req, res) -> {
            List<Hero> heroes = heroDao.getAll();
            List<Squad> squads = squadDao.getAll();
            model.put("heroes", heroes);
            return new ModelAndView(model, "all-heroes.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes/new", (req, res) -> {
            List<Hero> heroes = heroDao.getAll();
            List<Squad> squads = squadDao.getAll();
            model.put("heroes", heroes);
            model.put("squads", squads);
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/heroes/new", (req, res) -> {
            List<Hero> heroes = heroDao.getAll();
            model.put("heroes", heroes);
            String name = req.queryParams("name");
            int squadId = Integer.parseInt(req.queryParams("squadId"));
            int age = Integer.parseInt(req.queryParams("age"));
            String weakness = req.queryParams("weakness");
            String power = req.queryParams("power");
            if (squadDao.getAllHeroesBySquad(squadId).size() == squadDao.findById(squadId).getSize()) {
                model.put("squads", squadDao.getAll());
                model.put("heroes", heroDao.getAll());
                return new ModelAndView(model, "squad-full.hbs");
            } else {
                Hero newHero = new Hero(name, age, power, weakness, squadId, bytes);
                heroDao.add(newHero);
                model.put("squads", squadDao.getAll());
                model.put("heroes", heroDao.getAll());
                res.redirect("/heroes");
                return null;
            }
        }, new HandlebarsTemplateEngine());

        get("/heroes/delete", (req, res) -> {
            heroDao.clearAllHeroes();
            res.redirect("/heroes");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id", (req, res) -> {
            int idOfHeroToFind = Integer.parseInt(req.params("id"));
            Hero foundHero = heroDao.findById(idOfHeroToFind);
            int idOfSquad = foundHero.getSquadId();
            Squad squad = squadDao.findById(idOfSquad);
            model.put("squad", squad);
            model.put("hero", foundHero);
            model.put("heroes", heroDao.getAll());
            String encodedString = Base64.getEncoder().encodeToString(foundHero.getImage()); //encode byteArray to base64 to display in template
            model.put("encodedString", encodedString);//
            return new ModelAndView(model, "hero-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id/edit", (req, res) -> {
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            Hero hero = heroDao.findById(Integer.parseInt(req.params("id")));
            model.put("hero", hero);
            model.put("editHero", true);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/heroes/:id/edit", (req, res) -> {
            int heroToEditId = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newName");
            int newSquadId = Integer.parseInt(req.queryParams("newSquadId"));
            int newAge = Integer.parseInt(req.queryParams("newAge"));
            String newWeakness = req.queryParams("newWeakness");
            String newSpecialPower = req.queryParams("newPower");
            heroDao.update(heroToEditId, newName, newAge, newSpecialPower, newWeakness, newSquadId);
            List<Hero> heroes = heroDao.getAll();
            Hero hero = heroDao.findById(heroToEditId);
            String encodedString = Base64.getEncoder().encodeToString(hero.getImage());
            hero.setEncodedString(encodedString);
            model.put("squad", squadDao.findById(hero.getSquadId()));
            model.put("heroes", heroes);
            model.put("hero", hero);
            model.put("encodedString", encodedString);
            return new ModelAndView(model, "hero-detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/heroes/:id/delete", (req, res) -> {
            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
            heroDao.deleteById(idOfHeroToDelete);
            res.redirect("/heroes");
            return null;
        }, new HandlebarsTemplateEngine());

        get("/upload/:id", (req, res) -> {
            int heroId = Integer.parseInt(req.params("id"));
            model.put("hero", heroDao.findById(heroId));
            return new ModelAndView(model, "upload-image.hbs");
        }, new HandlebarsTemplateEngine());

        post("/upload/:id", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
            byte[] imageBytes;
            int heroId = Integer.parseInt(req.params("id"));
            long fileSize = req.raw().getPart("image").getSize();
            if(fileSize <= 30000){ InputStream input = req.raw().getPart("image").getInputStream();
                imageBytes = getByteArray(input);
                heroDao.uploadImage(heroId, imageBytes);
                model.put("hero", heroDao.findById(heroId));
                String encodedString = Base64.getEncoder().encodeToString(heroDao.findById(heroId).getImage());
                model.put("encodedString", encodedString);
                model.put("squad",squadDao.findById(heroDao.findById(heroId).getSquadId()));
                return new ModelAndView(model, "hero-detail.hbs");
            }
            else{
                model.put("id", heroId);
                return new ModelAndView(model, "huge-file.hbs");
            }


        }, new HandlebarsTemplateEngine());
    }

    private static byte[] getByteArray(InputStream fs) throws IOException {
        return IOUtils.toByteArray(fs);
    }
}


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

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.*;
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
        return 4567; //return default.png port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFileLocation("/public");
        File uploadDir = new File("src/main/resources/public/images");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist
        staticFiles.externalLocation("src/main/resources/public/images");


//        Connection string to the database on local disk (the next 2 lines)
//        String connectionString = "jdbc:postgresql://localhost:5432/heroes";
//        Sql2o sql2o = new Sql2o(connectionString, "baraka", "fRankline");

//        Connection string to the database deployed on heroku (the next 2 lines)
        String connectionString = "jdbc:postgresql://hhmgarbtrfefza:529f5b88d4e82a1486da5dee9c0802d62032029d23bebed9789a01248b79d088@ec2-54-235-180-123.compute-1.amazonaws.com:5432/d9jh1sfpbob72s";
        Sql2o sql2o = new Sql2o(connectionString, "hhmgarbtrfefza", "529f5b88d4e82a1486da5dee9c0802d62032029d23bebed9789a01248b79d088");

        Sql2oHeroDao heroDao = new Sql2oHeroDao(sql2o);
        Sql2oSquadDao squadDao = new Sql2oSquadDao(sql2o);

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> squads = squadDao.getAll();
            List<Hero> heroes = heroDao.getAll();
            model.put("squads", squads);
            model.put("heroes", heroes);
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
            List<String> squadNames = new ArrayList<>();
            for(Squad squad: squads){
                squadNames.add(squad.getName().toLowerCase());
            }
            int maxSize = Integer.parseInt(req.queryParams("size"));
            String cause = req.queryParams("cause");
            Squad newSquad = new Squad(name,maxSize,cause);
            if (squadNames.contains(newSquad.getName().toLowerCase())) {
                return new ModelAndView(model, "squad-exists.hbs");
            }
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

        post("/squads/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfSquadToEdit = Integer.parseInt(req.params("id"));
            String newName = req.queryParams("newName");
            int newMaxSize = Integer.parseInt(req.queryParams("newSize"));
            String newCause = req.queryParams("newCause");
            squadDao.update(idOfSquadToEdit, newName,newMaxSize,newCause);
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
            Hero hero = new Hero(name,age,power,weakness,idOfSquad,"default.png");
            if ( squadDao.getAllHeroesBySquad(idOfSquad).size()==squadDao.findById(idOfSquad).getSize()){
                model.put("heroes", heroDao.getAll());
                model.put("squads", squadDao.getAll());
                return modelAndView(model, "squad-full.hbs");
            }else{
                heroDao.add(hero);
                Squad squad = squadDao.findById(idOfSquad);
                model.put("squad", squad);
                model.put("heroes", squadDao.getAllHeroesBySquad(idOfSquad));
                model.put("squads", squadDao.getAll());
                return modelAndView(model,"squad-detail.hbs");
            }

        }, new HandlebarsTemplateEngine());

        get("/squads/:id/heroes/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfHeroToDelete = Integer.parseInt(req.params("id"));
            System.out.println("Hero ID: " + idOfHeroToDelete);
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
            return new ModelAndView(model, "hero-form.hbs");
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
            int squadId = Integer.parseInt(req.queryParams("squadId"));
            int age = Integer.parseInt(req.queryParams("age"));
            String weakness = req.queryParams("weakness");
            String power = req.queryParams("power");
            if (squadDao.getAllHeroesBySquad(squadId).size()==squadDao.findById(squadId).getSize()){
                model.put("squads", squadDao.getAll());
                model.put("heroes", heroDao.getAll());
                return new ModelAndView(model, "squad-full.hbs");
            }else{
                Hero newHero = new Hero(name, age, power, weakness, squadId,"default.png");
                heroDao.add(newHero);
                model.put("squads", squadDao.getAll());
                model.put("heroes", heroDao.getAll());
                res.redirect("/heroes");
                return null;
            }

        }, new HandlebarsTemplateEngine());

        get("/heroes/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Squad> allSquads = squadDao.getAll();
            model.put("squads", allSquads);
            Hero hero = heroDao.findById(Integer.parseInt(req.params("id")));
            model.put("hero", hero);
            model.put("editHero", true);
            model.put("squads", squadDao.getAll());
            return new ModelAndView(model, "hero-form.hbs");
        }, new HandlebarsTemplateEngine());

        post("/heroes/:id/edit", (req, res) -> {
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

        get("/upload/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int heroId = Integer.parseInt(req.params("id"));
            model.put("hero", heroDao.findById(heroId));
            return new ModelAndView(model, "upload-image.hbs");
        }, new HandlebarsTemplateEngine());

        post("/upload/:id", (req, res) -> {
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
            Map<String, Object> model = new HashMap<>();
            int heroId = Integer.parseInt(req.params("id"));
            try (InputStream input = req.raw().getPart("image").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            heroDao.uploadImage(heroId, tempFile.getFileName().toString());
            model.put("hero", heroDao.findById(heroId));
            return new ModelAndView(model,"hero-detail.hbs");
        }, new HandlebarsTemplateEngine());




//        get("/upload", (req, res) ->
//                "<form method='post' enctype='multipart/form-data'>" // note the enctype
//                        + "<div class='form-group'>"
//                        + "    <input class='btn-primary' type='file' name='uploaded_file' accept='.png, .jpg, .jpeg'>" // make sure to call getPart using the same "name" in the post
//                        + "</div>"
//                        + "<br>"
//                        + "<div class='form-group'>"
//                        + "    <button class='btn-primary'>Upload picture</button>"
//                        + "</div>"
//                        + "</form>"
//        );
//
//        post("/upload", (req, res) -> {
//
//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
//            String imageName = tempFile.getFileName().toString();
//
//            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
//
//
//            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) {// getPart needs to use same "name" as input field in form
//                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//            }
//
//            logInfo(req, tempFile);
//            return "<h1>You uploaded this image:<h1><img src='" + tempFile.getFileName() + "' height='150' width='150'>";
//        });
//     }
//
//    private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
//        System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")).getBytes() + "' saved as '" + tempFile.getFileName().toString() + "'");
//    }
//
//    private static String getFileName(Part part) {
//        for (String cd : part.getHeader("content-disposition").split(";")) {
//            if (cd.trim().startsWith("filename")) {
//                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
//            }
//        }
//        return null;
//    }

        get("/upload", (req, res) ->
                "<form method='post' enctype='multipart/form-data'>" // note the enctype
                        + "    <input type='file' name='uploaded_file' accept='.png, .jpeg, .jpg'>" // make sure to call getPart using the same "name" in the post
                        + "    <button>Upload picture</button>"
                        + "</form>"
        );

        post("/upload", (req, res) -> {

            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));

            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            logInfo(req, tempFile);
            return "<h1>You uploaded this image:<h1><img src='" + tempFile.getFileName() + "' height='150' width='150'>";

        });

    }

    // methods used for logging
    private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
        System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")) + "' saved as '" + tempFile.toAbsolutePath() + "'");
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}


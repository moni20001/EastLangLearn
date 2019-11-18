package com.eastlanglearn.east.web;


import com.eastlanglearn.east.domain.entities.ReadingExercise;
import com.eastlanglearn.east.domain.entities.User;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.service.ReadingService;
import com.eastlanglearn.east.service.UserService;
import com.eastlanglearn.east.service.VocabListService;
import com.eastlanglearn.east.service.WordsService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

@Controller
public class AdminController extends BaseController {

    private final UserService userService;
    private final ReadingService readingService;
    private final WordsService wordsService;
    private final VocabListService vocabListService;

    @Autowired
    public AdminController(UserService userService, ReadingService readingService, WordsService wordsService, VocabListService vocabListService) {
        this.userService = userService;
        this.readingService = readingService;
        this.wordsService = wordsService;
        this.vocabListService = vocabListService;
    }


    @GetMapping(value = "/admin")
    public ModelAndView getAdminPanel() {
        LinkedHashSet<User> users = this.userService.getAllUsers();
        LinkedHashSet<ReadingExercise> readingExercises = this.readingService.getAllStories();
        LinkedHashSet<Word> words = this.wordsService.getAllWords();
        return new ModelAndView("admin-home").addObject("users", users).addObject("stories", readingExercises).addObject("words", words);
    }

    @PostMapping(value = "/addStory/")
    public ModelAndView addCategory(@RequestParam("storyTitle") String title, @RequestParam("storyOriginal") String original, @RequestParam("storyTranslated") String translated) {
        new Thread(() -> {
            this.readingService.createStory(title, original, translated);
        }).start();
        return redirect("/admin");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/createList")
    public ModelAndView createList(@RequestBody String data, Authentication authentication) {
        JsonObject jsonData = new Gson().fromJson(data, JsonObject.class);
        this.vocabListService.createVocabList(jsonData, authentication);

        return redirect("/admin");
    }


    @PostMapping(value = "/deleteStory/{storyId}")
    public ModelAndView deleteStory(@PathVariable("storyId") String storyId, Authentication authentication) {
        this.readingService.deleteStoryById(storyId, authentication);
        return redirect("/admin");
    }


    @PostMapping(value = "/delete/{userId}")
    public ModelAndView deleteUser(@PathVariable("userId") String user, Authentication authentication) {
        this.userService.deleteByUsername(this.userService.findByUsername(user).getUsername(), authentication);
        return redirect("/admin");
    }

    @PostMapping(value = "/promote/{userId}")
    public ModelAndView promoteUser(@PathVariable("userId") String user) {
        try {
            this.userService.promoteUser(this.userService.findByUsername(user).getUsername());
        } catch (IllegalArgumentException e) {
            System.out.println("There is no role, higher than ADMIN");
        }

        return redirect("/admin");
    }

    @PostMapping(value = "/demote/{userId}")
    public ModelAndView demoteUser(@PathVariable("userId") String user) {
        try {
            this.userService.demoteUser(this.userService.findByUsername(user).getUsername());
        } catch (IllegalArgumentException e) {
            System.out.println("There is no role, lower than USER");
        }
        return redirect("/admin");
    }


}

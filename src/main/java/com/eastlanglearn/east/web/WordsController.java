package com.eastlanglearn.east.web;


import com.eastlanglearn.east.domain.entities.VocabList;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.service.UserService;
import com.eastlanglearn.east.service.VocabListService;
import com.eastlanglearn.east.service.WordsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
public class WordsController extends BaseController {
    private final ModelMapper modelMapper;
    private final WordsService wordsService;
    private final UserService userService;
    private final VocabListService vocabListService;

    @Autowired
    public WordsController(ModelMapper modelMapper, WordsService wordsService, UserService userService, VocabListService vocabListService) {
        this.modelMapper = modelMapper;
        this.wordsService = wordsService;
        this.userService = userService;
        this.vocabListService = vocabListService;
    }

    @GetMapping("/diff/{diffId}")
    public ModelAndView getDiff(ModelAndView modelAndView, @PathVariable("diffId") String id) {
        //take the level's words and display them
        modelAndView.addObject("words", this.wordsService.getAllByDifficulty(id));
        modelAndView.addObject("difficulties", this.wordsService.getDifficulties());
        return this.view("lists", modelAndView);
    }


    @GetMapping("/lang/{language}")
    public ModelAndView getLanguagesLists(ModelAndView modelAndView, @PathVariable("language") String language) {
        //take the lessons from language and display them
        LinkedHashSet<VocabList> langThemes = this.vocabListService.getAllByLanguage(language);
        modelAndView.addObject("lang", language);
        modelAndView.addObject("langThemes", langThemes);
        modelAndView.addObject("words", this.wordsService.getAllByDifficulty(language));
        return this.view("languagesLists", modelAndView);
    }

    @GetMapping("/lang/{language}/{langLesson}")
    public ModelAndView getLesson(ModelAndView modelAndView, @PathVariable("language") String language, @PathVariable("langLesson") String langLesson) {

        modelAndView.addObject("lesson", this.vocabListService.getLessonByTitle(langLesson).get());
        //split all new lines into array so we can have new lines in the lesson's text
        modelAndView.addObject("lessonText", this.vocabListService.getLessonTextByTitle(langLesson));
        modelAndView.addObject("words", this.vocabListService.getWordsFromLessonByIndexes(this.vocabListService.getLessonByTitle(langLesson).get().getWordsByIndexes()));
        return this.view("lesson", modelAndView);
    }

    @GetMapping("/languageWords/{lessonTitle}")
    public ModelAndView getLessonWords(ModelAndView modelAndView, @PathVariable("lessonTitle") String lessonTitle, @CookieValue(value = "typingExercise", required = false) boolean typingExercise) {
        Gson gson = new Gson();
        //Get lesson's words and convert them to json
        modelAndView.addObject("quizWords", gson.toJson(this.vocabListService.getWordsFromLessonByIndexes(this.vocabListService.getLessonByTitle(lessonTitle).get().getWordsByIndexes())));
        if (typingExercise) {
            return this.view("quizType", modelAndView);
        }
        return this.view("quizSelect", modelAndView);
    }


    @GetMapping("/learnWords/{diff}")
    public ModelAndView getLevels(ModelAndView modelAndView, @PathVariable("diff") String id, Authentication authentication) {
        //Make levels with size of 10 words
        Collection<List<Word>> data = partitionBasedOnSize(this.wordsService.getAllByDifficulty(id), 10);
        boolean[] levelsDone = this.userService.getAlreadyDoneLevels(data, authentication);
        modelAndView.addObject("levels", data);
        modelAndView.addObject("difficulty", id);
        modelAndView.addObject("learnedLevels", levelsDone);
        return this.view("words", modelAndView);
    }

    @GetMapping("/learnWords/{diff}/{stage}")
    public ModelAndView getLevelWords(ModelAndView modelAndView, @PathVariable("diff") String id, @PathVariable("stage") Integer level, @CookieValue(value = "typingExercise", required = false) boolean typingExercise) {
        //get the quizzes then take the requested one
        Collection<List<Word>> data = partitionBasedOnSize(this.wordsService.getAllByDifficulty(id), 10);
        ArrayList<Word> quizWords = (ArrayList<Word>) data.stream().skip(level - 1).findFirst().get();
        Gson gson = new Gson();
        modelAndView.addObject("levels", data);
        modelAndView.addObject("difficulty", id);
        modelAndView.addObject("quizWords", gson.toJson(quizWords));
        if (typingExercise) {
            return this.view("quizType", modelAndView);
        }
        return this.view("quizSelect", modelAndView);
    }

    private static <T> Collection<List<Word>> partitionBasedOnSize(LinkedHashSet<Word> inputList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return inputList.stream()
                .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size))
                .values();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/learnWords/{diff}/{stage}/quizDone")
    public void quizDone(@RequestBody String data, Authentication authentication) {
        //quiz calculate xp and add it to the user
        JsonObject jsonData = new Gson().fromJson(data, JsonObject.class);
        this.userService.quizDone(authentication, jsonData.get("timesWrong").getAsInt(), jsonData.get("time").getAsLong() / 1000, jsonData.get("score").getAsInt());

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/languageWords/{lesson}/quizDone")
    public void quizLessonDone(@RequestBody String data, Authentication authentication) {
        //lesson's quiz calculate xp and add it to the user
        JsonObject jsonData = new Gson().fromJson(data, JsonObject.class);
        this.userService.quizDone(authentication, jsonData.get("timesWrong").getAsInt(), jsonData.get("time").getAsLong() / 1000, jsonData.get("score").getAsInt());
    }

    @GetMapping("/languageWords/{lesson}/quizDone")
    public ModelAndView getQuizLessonDone(ModelAndView modelAndView, @RequestParam("time") Integer timeTaken, @RequestParam("timesWrong") Integer timesWrong, Authentication authentication) {
        modelAndView.addObject("timesWrong", timesWrong);
        modelAndView.addObject("time", timeTaken);
        modelAndView.addObject("level", this.userService.findByUsername(authentication.getName()).getLevel());
        //render the result after the lesson's quiz
        return this.view("quizDone", modelAndView);
    }


    @GetMapping("/learnWords/{diff}/{stage}/quizDone")
    public ModelAndView getQuizDone(@PathVariable("diff") String difficulty) {
        //render the result after the quiz
        String urlToRedirect = "/learnWords/" + difficulty;
        return this.redirect(urlToRedirect);
    }

    @PostMapping("/search")
    public ModelAndView searchWord(@RequestParam("word") String word, ModelAndView modelAndView) {
        //get the word from the db and display it
        modelAndView.addObject("wordPresent", this.wordsService.findWord(word).isPresent());
        if (this.wordsService.findWord(word).isPresent())
            modelAndView.addObject("word", this.wordsService.findWord(word).get());
        return this.view("wordSearch", modelAndView);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/learnWords/{diff}/{stage}/learnedWords")
    public void addLearnedWordsToUser(@RequestBody String data, Authentication authentication) {
        //Add learned quiz's words to the user
        this.userService.learnWords(data, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/languageWords/{language}/learnedWords")
    public void addLearnedLessonWordsToUser(@RequestBody String data, Authentication authentication) {
        //Add learned lesson's words to the user
        this.userService.learnWords(data, authentication);
    }

}

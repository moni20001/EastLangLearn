package com.eastlanglearn.east.web;

import com.eastlanglearn.east.domain.entities.ReadingExercise;
import com.eastlanglearn.east.service.ReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StoryController extends BaseController {
    private final ReadingService readingService;

    @Autowired
    public StoryController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping("/stories")
    public ModelAndView getStories() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("stories", readingService.getAllStories());
        return this.view("storiesList", modelAndView);
    }

    @GetMapping("/story/{storyId}")
    public ModelAndView upload(ModelAndView modelAndView, @PathVariable("storyId") String id) {
        ReadingExercise story = this.readingService.getStoryById(id);
        modelAndView.addObject("story", story);
        return this.view("story", modelAndView);
    }


    @GetMapping("/story/{storyId}/{diff}")
    public ModelAndView upload(ModelAndView modelAndView, @PathVariable("storyId") String id, @PathVariable("diff") String diff) {
        //this will replace all the words by difficulty level
        ReadingExercise story = this.readingService.retranslateByDiff(diff, id);
        modelAndView.addObject("story", story);
        return this.view("story", modelAndView);
    }

}

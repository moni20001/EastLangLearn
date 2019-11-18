package com.eastlanglearn.east.web;


import com.eastlanglearn.east.domain.entities.User;
import com.eastlanglearn.east.service.ReadingService;
import com.eastlanglearn.east.service.UserService;
import com.eastlanglearn.east.service.VocabListService;
import com.eastlanglearn.east.service.WordsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController extends BaseController {

    private final ModelMapper modelMapper;
    private final WordsService wordsService;
    private final UserService userService;
    private final VocabListService vocabListService;
    private final ReadingService readingService;


    @Autowired
    public HomeController(ModelMapper modelMapper, WordsService wordsService, UserService userService, VocabListService vocabListService, ReadingService readingService) {
        this.modelMapper = modelMapper;
        this.wordsService = wordsService;
        this.userService = userService;
        this.vocabListService = vocabListService;
        this.readingService = readingService;
    }


    @GetMapping("/")
    public ModelAndView getHomePage(ModelAndView modelAndView) {

        modelAndView.addObject("usersSize", this.userService.getAllUsers().size());
        modelAndView.addObject("quizesSize", Integer.valueOf(this.wordsService.getAllWords().size() / 10));
        modelAndView.addObject("lessonsSize", this.vocabListService.getAllLists().size());
        modelAndView.addObject("wordsSize", this.wordsService.getAllWords().size());

        return this.view("index", modelAndView);
    }

    @GetMapping("/help")
    public ModelAndView getHelpPage(ModelAndView modelAndView) {
        return this.view("help", modelAndView);
    }

}

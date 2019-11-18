package com.eastlanglearn.east.interceptors;

import com.eastlanglearn.east.service.VocabListService;
import com.eastlanglearn.east.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestInterceptor extends HandlerInterceptorAdapter {

    private WordsService wordsService;
    private VocabListService vocabListService;

    @Autowired
    public RequestInterceptor(final WordsService wordsService, final VocabListService vocabListService) {
        this.wordsService = wordsService;
        this.vocabListService = vocabListService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        try {
            request.setAttribute("authorization", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().toString());
            request.setAttribute("difficulties", this.wordsService.getDifficulties());
            request.setAttribute("languagesWords", this.vocabListService.findAllLanguageLists());
        } catch (Exception e) {
            //not html
        }
    }
}

package com.eastlanglearn.east.config;


import com.eastlanglearn.east.interceptors.RequestInterceptor;
import com.eastlanglearn.east.service.VocabListService;
import com.eastlanglearn.east.service.WordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationMVCConfiguration implements WebMvcConfigurer {
    private final WordsService wordsService;
    private final VocabListService vocabListService;

    @Autowired
    public ApplicationMVCConfiguration(WordsService wordsService, VocabListService vocabListService) {
        this.wordsService = wordsService;
        this.vocabListService = vocabListService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor(wordsService, vocabListService));
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

}

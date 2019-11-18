package com.eastlanglearn.east.service;


import com.eastlanglearn.east.domain.entities.VocabList;
import com.eastlanglearn.east.domain.entities.Word;
import com.google.gson.JsonObject;
import org.springframework.security.core.Authentication;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;


public interface VocabListService {

     void createVocabList(JsonObject jsonData, Authentication authentication);

     LinkedHashSet<String> findAllLanguageLists();

     LinkedHashSet<VocabList> getAllByLanguage(String lang);

     Optional<VocabList> getLessonByTitle(String title);

     LinkedHashSet<Word> getWordsFromLessonByIndexes(String indexes);

     LinkedHashSet<VocabList> getAllLists();

     LinkedList<String> getLessonTextByTitle(String title);
}

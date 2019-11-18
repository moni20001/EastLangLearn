package com.eastlanglearn.east.service;

import com.eastlanglearn.east.domain.entities.VocabList;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.repository.VocabListRepository;
import com.eastlanglearn.east.repository.WordsRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VocabListServiceImpl implements VocabListService {

    private final VocabListRepository vocabListRepository;

    private final ModelMapper modelMapper;

    private final WordsRepository wordsRepository;

    @Autowired
    public VocabListServiceImpl(VocabListRepository vocabListRepository, ModelMapper modelMapper, WordsRepository wordsRepository) {
        this.vocabListRepository = vocabListRepository;
        this.modelMapper = modelMapper;
        this.wordsRepository = wordsRepository;
    }


    @Override
    public void createVocabList(JsonObject jsonData, Authentication authentication) {
        String title = jsonData.get("title").toString().replaceAll("\"", "");
        String lessonText = jsonData.get("lessonText").toString().replaceAll("\"", "");
        String lang = jsonData.get("listLang").toString().replaceAll("\"", "").toLowerCase();
        JsonArray words = jsonData.get("selected").getAsJsonArray();
        VocabList vocabList = new VocabList();
        vocabList.setListTitle(title);
        //Get words as Strings
        ArrayList<String> list = new ArrayList<String>();
        if (words != null) {
            int len = words.size();
            for (int i = 0; i < len; i++) {
                list.add(words.get(i).toString().replaceAll("\"", ""));
            }
        }
        //Convert words from strings to indexes
        LinkedHashSet<Long> wordsEntities = new LinkedHashSet<>();
        String wordsIndexes = "";
        for (String w : list) {
            if (this.wordsRepository.findWord(w).isPresent())
                wordsIndexes += (this.wordsRepository.findWord(w).get().getId()) + ",";
        }
        vocabList.setWordsByIndexes(wordsIndexes);
        vocabList.setLanguage(lang);
        vocabList.setLessonText(lessonText);
        //check if already exist
        if (!this.vocabListRepository.findByListTitle(title).isPresent()) {
            this.vocabListRepository.saveAndFlush(vocabList);
        }


    }

    @Override
    public LinkedHashSet<String> findAllLanguageLists() {
        return this.vocabListRepository.getLanguagesLists();
    }

    @Override
    public LinkedHashSet<VocabList> getAllByLanguage(String lang) {

        return this.vocabListRepository.getAllByLanguage(lang);
    }

    @Override
    public Optional<VocabList> getLessonByTitle(String title) {

        return this.vocabListRepository.findByListTitle(title).stream().findFirst();
    }

    @Override
    public LinkedHashSet<Word> getWordsFromLessonByIndexes(String indexesArray) {
        String[] indexes = indexesArray.split(",");
        LinkedHashSet<Word> words = new LinkedHashSet<>();
        for (String s : indexes) {
            words.add(this.wordsRepository.findById(Long.valueOf(s)).get());
        }

        return words;
    }

    @Override
    public LinkedHashSet<VocabList> getAllLists() {
        return this.vocabListRepository.getAllLists();
    }

    @Override
    public LinkedList<String> getLessonTextByTitle(String title) {
        //checks if there is chinese in the line and if there is add listen button
        VocabList vocabList = this.vocabListRepository.findByListTitle(title).stream().findFirst().get();
        LinkedList<String> textsArr = new LinkedList<>();
        String[] texts= vocabList.getLessonText().split("\\\\n");
        for (String t: texts) {
            Pattern p = Pattern.compile("\\p{IsHan}");
            Matcher m = p.matcher(t);
            boolean thereIsChineseInLine = false;
            String chineseChars = "";
            while (m.find()) {
                thereIsChineseInLine = true;
                chineseChars += m.group(0);
            }
            if(thereIsChineseInLine){
                Integer indexOfChineseChars = t.indexOf(chineseChars) + chineseChars.length();
                String temp ="'"+chineseChars+"'";
                StringBuilder sb = new StringBuilder();
                sb.append("<div>")
                        .append(t.substring(0, indexOfChineseChars))
                        .append("<img style='vertical-align: middle' src='/img/playIcon.png' onclick=textToSpeech("+temp+") </img>")
                        .append(t.substring(indexOfChineseChars, t.length()))
                        .append("</div>");
                textsArr.add(sb.toString());
            }
            else{
                textsArr.add(t);
            }

        }
        return textsArr;
    }

}

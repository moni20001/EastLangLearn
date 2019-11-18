package com.eastlanglearn.east.service;

import com.eastlanglearn.east.domain.entities.Word;


import java.util.LinkedHashSet;
import java.util.Optional;

public interface WordsService {

    LinkedHashSet<Word> getAllWords();
    LinkedHashSet<Word> getAllByDifficulty(String diff);
    LinkedHashSet<String> getDifficulties();
    Optional<Word> findWord(String word);

}

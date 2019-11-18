package com.eastlanglearn.east.service;


import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.repository.WordsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;

@Service
public class WordsServiceImpl implements WordsService {

    private final ModelMapper modelMapper;

    private final WordsRepository wordsRepository;

    @Autowired
    public WordsServiceImpl(ModelMapper modelMapper, WordsRepository wordsRepository) {
        this.modelMapper = modelMapper;
        this.wordsRepository = wordsRepository;
    }

    @Override
    public LinkedHashSet<Word> getAllWords() {
        return this.wordsRepository.getAllWords();
    }

    @Override
    public LinkedHashSet<Word> getAllByDifficulty(String diff) {
        return this.wordsRepository.getAllByDifficulty(diff);
    }

    @Override
    public LinkedHashSet<String> getDifficulties() {
        return this.wordsRepository.getDifficulties();
    }

    @Override
    public Optional<Word> findWord(String word) {
        return this.wordsRepository.findWord(word);
    }

}

package com.eastlanglearn.east.repository;


import com.eastlanglearn.east.domain.entities.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
public interface WordsRepository extends JpaRepository<Word, String> {

    @Query(value = "SELECT * from words", nativeQuery = true)
    LinkedHashSet<Word> getAllWords();

    @Query(value = "SELECT * FROM words Where words.diff =?1", nativeQuery = true)
    LinkedHashSet<Word> getAllByDifficulty(String diff);


    @Query(value = "Select words.diff From words Group By words.diff",nativeQuery = true)
    LinkedHashSet<String> getDifficulties();

    @Query(value = "SELECT * FROM words Where words.word =?1", nativeQuery = true)
    Optional<Word> findWordByMeaning(String word);

    @Query(value = "SELECT * FROM words Where words.word =?1", nativeQuery = true)
    Optional<Word> findWordByTranslation(String word);

    @Query(value = "Select * from words Where (words.en_meaning=?1 OR words.bg_meaning=?1 OR words.word=?1)", nativeQuery = true)
    Optional<Word> findWord(String word);

    Optional<Word> findById(Long id);
}

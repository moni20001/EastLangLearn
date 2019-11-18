package com.eastlanglearn.east.repository;

import com.eastlanglearn.east.domain.entities.VocabList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Optional;

@Repository
public interface VocabListRepository extends JpaRepository<VocabList,String> {

    Optional<VocabList> findById(Long id);

    Optional<VocabList> findByListTitle(String title);

    @Query(value = "SELECT * FROM vocab_lists Where vocab_lists.lang =?1", nativeQuery = true)
    LinkedHashSet<VocabList> getAllByLanguage(String lang);

    @Query(value = "Select vocab_lists.lang From vocab_lists Group By vocab_lists.lang",nativeQuery = true)
    LinkedHashSet<String> getLanguagesLists();

    @Query(value = "SELECT * FROM vocab_lists Where vocab_lists.title =?1", nativeQuery = true)
    LinkedHashSet<VocabList> getLessonByTitle(String title);

    @Query(value = "SELECT * FROM vocab_lists", nativeQuery = true)
    LinkedHashSet<VocabList> getAllLists();

}

package com.eastlanglearn.east.repository;

import com.eastlanglearn.east.domain.entities.ReadingExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

@Repository
public interface ReadingExerciseRepository extends JpaRepository<ReadingExercise,String> {

    @Query(value = "SELECT * from reading_exercises", nativeQuery = true)
    LinkedHashSet<ReadingExercise> getAllStories();


    @Query(value = "SELECT * FROM reading_exercises Where reading_exercises.id =?1", nativeQuery = true)
    ReadingExercise getStoryById(String id);


    @Query(value = "SELECT * FROM reading_exercises Where reading_exercises.id =?1", nativeQuery = true)
    ReadingExercise getStoryByTitle(String title);


}

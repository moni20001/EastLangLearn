package com.eastlanglearn.east.service;

import com.eastlanglearn.east.domain.entities.ReadingExercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;

import java.util.LinkedHashSet;

public interface ReadingService {
    LinkedHashSet<ReadingExercise> getAllStories();
    ReadingExercise getStoryById(String id);
    ReadingExercise getStoryByTitle(String title);
    void createStory(String title,String textOriginal, String textTranslated);
    boolean deleteStoryById(String storyId, Authentication authentication);
    ReadingExercise retranslateByDiff(String diff,String storyId);
}

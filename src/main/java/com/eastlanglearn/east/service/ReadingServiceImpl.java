package com.eastlanglearn.east.service;

import com.eastlanglearn.east.domain.entities.ReadingExercise;
import com.eastlanglearn.east.domain.entities.User;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.repository.ReadingExerciseRepository;
import com.eastlanglearn.east.repository.RoleRepository;
import com.eastlanglearn.east.repository.UserRepository;
import com.eastlanglearn.east.repository.WordsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

@Service
public class ReadingServiceImpl implements ReadingService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ReadingExerciseRepository readingExerciseRepository;
    private final RoleRepository roleRepository;
    private final WordsRepository wordsRepository;

    @Autowired
    public ReadingServiceImpl(ModelMapper modelMapper, UserRepository userRepository, ReadingExerciseRepository readingExerciseRepository, RoleRepository roleRepository, WordsRepository wordsRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.readingExerciseRepository = readingExerciseRepository;
        this.roleRepository = roleRepository;
        this.wordsRepository = wordsRepository;
    }


    @Override
    public LinkedHashSet<ReadingExercise> getAllStories() {
        return this.readingExerciseRepository.getAllStories();
    }

    @Override
    public ReadingExercise getStoryById(String id) {
        return this.readingExerciseRepository.getStoryById(id);
    }

    @Override
    public ReadingExercise getStoryByTitle(String title) {
        return this.readingExerciseRepository.getStoryByTitle(title);
    }

    @Override
    public void createStory(String title, String textOriginal, String textTranslated) {
        ReadingExercise exercise = new ReadingExercise();
        exercise.setStoryTitle(title);
        exercise.setTextOriginal(textOriginal);
        exercise.setTextTranslated(textTranslated);
        this.readingExerciseRepository.save(exercise);
    }

    @Override
    @Transactional
    public boolean deleteStoryById(String storyId, Authentication authentication) {
        // User deleting = this.userRepository.findByUsername(authentication.getName()).get();
        ReadingExercise storyToDelete = this.readingExerciseRepository.findById(storyId).get();
        try {
            this.readingExerciseRepository.delete(storyToDelete);
            this.readingExerciseRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ReadingExercise retranslateByDiff(String diff, String storyId) {
        //For now its useless, because we don't have story writers
        ReadingExercise story = this.readingExerciseRepository.getStoryById(storyId);
        String text = story.getTextTranslated();
        LinkedHashSet<Word> words = new LinkedHashSet<>();
        //Make it better in the future
        switch (diff) {
            case "hsk1":
                words = wordsRepository.getAllByDifficulty(diff);
                break;
            case "hsk2":
                words.addAll(wordsRepository.getAllByDifficulty("hsk1"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk2"));
                break;
            case "hsk3":
                words.addAll(wordsRepository.getAllByDifficulty("hsk1"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk2"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk3"));
                break;
            case "hsk4":
                words.addAll(wordsRepository.getAllByDifficulty("hsk1"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk2"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk3"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk4"));
                break;
            case "hsk5":
                words.addAll(wordsRepository.getAllByDifficulty("hsk1"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk2"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk3"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk4"));
                words.addAll(wordsRepository.getAllByDifficulty("hsk5"));
                break;
            case "hsk6":
                words.addAll(wordsRepository.getAllWords());
                break;
        }
        for (Word w : words) {
            text = text.replaceAll(w.getEnMeaning(), w.getWord());
        }
        story.setTextTranslated(text);
        return story;
    }

}

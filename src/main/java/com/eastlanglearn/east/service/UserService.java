package com.eastlanglearn.east.service;


import com.eastlanglearn.east.domain.entities.User;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.domain.models.service.UserServiceModel;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

public interface UserService extends UserDetailsService {
    boolean createUser(UserServiceModel userServiceModel);

    Set<UserServiceModel> getAll();

    boolean promoteUser(String id);

    boolean demoteUser(String id);

    boolean activateUser(User user);

    boolean alreadyExistByEmail(String email);

    boolean alreadyExistByUsername(String username);

    boolean deleteByUsername(String username, Authentication authentication);


    User findByUsername(String username);

    User findById(String id);

    LinkedHashSet<User> getAllUsers();

    boolean changePassword(User user, String oldPassword, String newPassword);

    void quizDone(Authentication authentication, Integer timesWrong, Long timeTaken, Integer score);

    LinkedHashSet<User> getAllUsersByXp();

    void learnWords(String wordsIndexes, Authentication authentication);

    LinkedHashSet<Word> getLearnedWordsUser(Authentication authentication);

    boolean[] getAlreadyDoneLevels(Collection<List<Word>> data, Authentication authentication);

    LinkedHashMap<String,Integer> calculatePercentageLearned(Authentication authentication);
}

package com.eastlanglearn.east.service;

import com.eastlanglearn.east.domain.entities.User;
import com.eastlanglearn.east.domain.entities.UserRole;
import com.eastlanglearn.east.domain.entities.Word;
import com.eastlanglearn.east.domain.models.service.UserServiceModel;
import com.eastlanglearn.east.repository.RoleRepository;
import com.eastlanglearn.east.repository.UserRepository;
import com.eastlanglearn.east.repository.WordsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final WordsRepository wordsRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, WordsRepository wordsRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.wordsRepository = wordsRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private Set<UserRole> getAuthorities(String authority) {
        Set<UserRole> userAuthorities = new HashSet<>();

        userAuthorities.add(this.roleRepository.getByAuthority(authority));

        return userAuthorities;
    }


    private String getUserAuthority(String userId) {
        if (!this.userRepository.findById(userId).isPresent()) {
            return null;
        }
        if (!this.userRepository.findById(userId).get().getAuthorities().stream().findFirst().isPresent()) {
            return null;
        }
        return this.userRepository.findById(userId).get().getAuthorities().stream().findFirst().get().getAuthority();
    }

    @Override
    public boolean createUser(UserServiceModel userServiceModel) {
        User userEntity = this.modelMapper.map(userServiceModel, User.class);
        userEntity.setPassword(this.bCryptPasswordEncoder.encode(userEntity.getPassword()));
        userEntity.setExperience(0L);
        if (this.userRepository.findAll().isEmpty()) {
            userEntity.setAuthorities(this.getAuthorities("ADMIN"));
        } else {
            userEntity.setAuthorities(this.getAuthorities("USER"));
        }

        try {
            this.userRepository.save(userEntity);
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    @Override
    public Set<UserServiceModel> getAll() {
        return this.userRepository
                .findAll()
                .stream()
                .map(x -> this.modelMapper.map(x, UserServiceModel.class))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean promoteUser(String id) {
        User user = this.userRepository
                .findByUsername(id)
                .orElse(null);

        if (user == null) return false;

        String userAuthority = this.getUserAuthority(user.getId());

        if (userAuthority == null) {
            user.setAuthorities(this.getAuthorities("USER"));
            this.userRepository.save(user);
            return true;
        }

        switch (userAuthority) {
            case "USER":
                user.setAuthorities(this.getAuthorities("MODERATOR"));
                break;
            case "MODERATOR":
                user.setAuthorities(this.getAuthorities("ADMIN"));
                break;
            default:
                throw new IllegalArgumentException("There is no role, higher than ADMIN");
        }

        this.userRepository.save(user);
        return true;

    }

    @Override
    public boolean demoteUser(String id) {
        User user = this.userRepository
                .findByUsername(id)
                .orElse(null);

        if (user == null) return false;

        String userAuthority = this.getUserAuthority(user.getId());
        if (userAuthority == null) {
            user.setAuthorities(this.getAuthorities("USER"));
            this.userRepository.save(user);
            return true;
        }


        switch (userAuthority) {
            case "ADMIN":
                user.setAuthorities(this.getAuthorities("MODERATOR"));
                break;
            case "MODERATOR":
                user.setAuthorities(this.getAuthorities("USER"));
                break;
            default:
                throw new IllegalArgumentException("There is no role, lower than USER");
        }

        this.userRepository.save(user);
        return true;
    }

    @Override
    public boolean activateUser(User user) {
        if (user != null && !user.isEnabled()) {
            user.setEnabled(true);
            this.userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean alreadyExistByEmail(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean alreadyExistByUsername(String username) {
        return this.userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public boolean deleteByUsername(String username, Authentication authentication) {
        User deleting = this.userRepository.findByUsername(authentication.getName()).get();
        User toBeDeleted = this.userRepository.findByUsername(username).get();
        try {
            if (this.getUserAuthority(deleting.getId()).equals("ADMIN") && !this.getUserAuthority(toBeDeleted.getId()).equals("ADMIN")) {
                this.userRepository.deleteUserInRoles(toBeDeleted.getId());
                this.userRepository.deleteUserInUsers(toBeDeleted.getId());
                this.userRepository.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User findByUsername(String username) {
        var temp = this.userRepository.findByUsername(username);
        if (temp.isPresent()) {
            return temp.get();
        }
        return null;
    }

    @Override
    public User findById(String id) {
        if (this.userRepository.findById(id).isPresent()) {
            return this.userRepository.findById(id).get();
        }
        return null;

    }

    @Override
    public LinkedHashSet<User> getAllUsers() {
        return this.userRepository.getAllUsers();
    }

    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String existingPassword = oldPassword;
        String dbPassword = user.getPassword();

        if (passwordEncoder.matches(existingPassword, dbPassword)) {
            if (newPassword.length() >= 6) {
                user.setPassword(passwordEncoder.encode(newPassword));
                this.userRepository.saveAndFlush(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public void quizDone(Authentication authentication, Integer timesWrong, Long timeTaken, Integer score) {
        User user = this.userRepository.findByUsername(authentication.getName()).get();
        Double xpMade = 0.0;
        /*
        faster = more xp
                Time table
            0-30 sec = super fast = x2
            30-45 sec = fast  = 1.75
            45-60 sec = normal = 1.50
            60-90 sec = ok = 1.25
            90+ sec = slow = 1
         */

        //Formula = ((words*5 ) - (timesWrong *5 )) * (timeTable)
        if (timesWrong * 5 < score * 5) {
            if (timeTaken > 0 && timeTaken < 30) {
                xpMade = ((score * 5) - (timesWrong * 5)) * 2.0;
            } else if (timeTaken >= 30 && timeTaken < 45) {
                xpMade = ((score * 5) - (timesWrong * 5)) * 1.75;
            } else if (timeTaken >= 45 && timeTaken < 60) {
                xpMade = ((score * 5) - (timesWrong * 5)) * 1.50;
            } else if (timeTaken >= 60 && timeTaken < 90) {
                xpMade = ((score * 5) - (timesWrong * 5)) * 1.25;
            } else {
                xpMade = ((score * 5) - (timesWrong * 5)) * 1.00;
            }
        } else {
            xpMade = 0.0;
        }

        long xpMadeAsLong = xpMade.longValue();
        user.setExperience(user.getExperience() + xpMadeAsLong);

        this.userRepository.flush();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) throw new UsernameNotFoundException("No such user.");
        return user;
    }


    @Override
    public LinkedHashSet<User> getAllUsersByXp() {
        return this.userRepository.getAllUsersByXp();
    }

    @Override
    public void learnWords(String wordsIndexes, Authentication authentication) {
        //Not the best method but it saves a lot of space in the db
        String s = wordsIndexes.substring(1, wordsIndexes.length() - 1);
        String[] arr = s.split(",");
        //Get already existing words
        User user = this.userRepository.findByUsername(authentication.getName()).get();
        String userLeanedWords = "";
        List<Long> data = new ArrayList<>();
        if (user.getLearnedWordsById() != null) {
            userLeanedWords += user.getLearnedWordsById();
            String[] arrFromWords = userLeanedWords.split(",");
            for (String arrFromWord : arrFromWords) {
                data.add(Long.valueOf(arrFromWord));
            }
        }
        for (String w : arr) {
            Long index = Long.valueOf(w);
            Optional<Word> temp = this.wordsRepository.findById(index);
            if (temp.isPresent()) {
                //check if word already learned
                if (!data.contains(temp.get().getId()))
                    userLeanedWords = userLeanedWords + temp.get().getId() + ",";
            }
        }
        user.setLearnedWordsById(userLeanedWords);

        this.userRepository.flush();
    }


    @Override
    public LinkedHashSet<Word> getLearnedWordsUser(Authentication authentication) {
        User user = this.userRepository.findByUsername(authentication.getName()).get();
        LinkedHashSet<Word> words = new LinkedHashSet<>();

        if (user.getLearnedWordsById() == null || user.getLearnedWordsById().equals("")) {
            return new LinkedHashSet<Word>();
        }

        List<Long> data = new ArrayList<>();

        String[] arrFromWords = user.getLearnedWordsById().split(",");
        for (String arrFromWord : arrFromWords) {
            data.add(Long.valueOf(arrFromWord));
        }
        for (Long w : data) {
            if (this.wordsRepository.findById(w).isPresent())
                words.add(this.wordsRepository.findById(w).get());
        }
        return words;
    }

    @Override
    public boolean[] getAlreadyDoneLevels(Collection<List<Word>> data, Authentication authentication) {
        LinkedHashSet<Word> usersWords = this.getLearnedWordsUser(authentication);
        List listData = data.stream().collect(Collectors.toList());
        boolean[] levelsLearned = new boolean[listData.size()];

        for (int i = 0; i < listData.size(); i++) {
            List<Word> level = (List<Word>) listData.get(i);
            boolean allWordsLearnInLevel = true;
            for (int j = 0; j < level.size(); j++) {
                if (!usersWords.contains(level.get(j))) {
                    allWordsLearnInLevel = false;
                }
            }
            levelsLearned[i] = allWordsLearnInLevel;
        }

        return levelsLearned;
    }

    @Override
    public LinkedHashMap<String, Integer> calculatePercentageLearned(Authentication authentication) {
        LinkedHashMap<String, Integer> difficultiesCalculated = new LinkedHashMap<>();
        var difficulties = this.wordsRepository.getDifficulties();
        LinkedHashSet<Word> usersWords = this.getLearnedWordsUser(authentication);

        for (String d : difficulties) {
            LinkedHashSet<Word> wordsInLevel = this.wordsRepository.getAllByDifficulty(d);
            double wordsInLevelSize = wordsInLevel.size();
            double usersLearnedWordsInLevel = 0.0;
            for (Word w : wordsInLevel) {
                if (usersWords.contains(w)) {
                    usersLearnedWordsInLevel += 1;
                }
            }
            Integer percentageLearned = (int) (usersLearnedWordsInLevel / wordsInLevelSize * 100);

            difficultiesCalculated.put(d, percentageLearned);
        }
        return difficultiesCalculated;
    }
}

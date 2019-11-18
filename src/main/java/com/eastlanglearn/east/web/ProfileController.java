package com.eastlanglearn.east.web;

import com.eastlanglearn.east.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;

@Controller
public class ProfileController extends BaseController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/myProfile")
    public ModelAndView myProfile(Authentication authentication, ModelAndView modelAndView) {
        LinkedHashMap<String, Integer> learned = this.userService.calculatePercentageLearned(authentication);
        modelAndView.addObject("percentage", learned);
        modelAndView.addObject("user", this.userService.findByUsername(authentication.getName()));
        modelAndView.addObject("username", authentication.getName());
        modelAndView.addObject("wordsLearned", this.userService.getLearnedWordsUser(authentication));
        return this.view("profile", modelAndView);
    }

    @GetMapping("/myProfile/settings")
    public ModelAndView myProfileSettings(ModelAndView modelAndView, Authentication authentication) {
        return view("settings", modelAndView);
    }

    @PostMapping(value = "/changePassword")
    public ModelAndView changePassword(ModelAndView modelAndView, Authentication authentication,
                                       @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
                                       @RequestParam("newPasswordConfirm") String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            modelAndView.addObject("passwordNotMatch", true);
            return this.view("settings", modelAndView);
        }
        boolean changed = this.userService.changePassword(this.userService.findByUsername(authentication.getName()), oldPassword, newPassword);
        if (!changed) {
            modelAndView.addObject("changed", true);
            return this.view("settings", modelAndView);
        }
        return redirect("/");
    }

    @GetMapping("/profile/{name}")
    public ModelAndView profile(ModelAndView modelAndView, @PathVariable("name") String name) {
        modelAndView.addObject("username", name);
        return this.view("profile", modelAndView);
    }
}

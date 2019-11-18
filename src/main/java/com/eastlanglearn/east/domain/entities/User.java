package com.eastlanglearn.east.domain.entities;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    private String username;

    private String password;

    private String email;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    private boolean isEnabled;

    private Set<UserRole> authorities;

    private Long experience;

    private String learnedWordsById;

    public User() {
        this.isEnabled=false;
    }

    @Override
    @Column(name = "username", nullable = false, unique = true)
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "xp", nullable = false)
    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }


    //Temporary fix for the db limit
    @Type(type="text")
    @Column(name = "learned_words")
    public String getLearnedWordsById() {
        return learnedWordsById;
    }

    public void setLearnedWordsById(String learnedWordsById) {
        this.learnedWordsById = learnedWordsById;
    }

    @Override
    @ManyToMany(cascade = CascadeType.ALL
            , targetEntity = UserRole.class
            , fetch = FetchType.EAGER

    )
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public Set<UserRole> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<UserRole> authorities) {
        this.authorities = authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Transient
    public int getLevel(){
        //formula for level = 0.2 * sqrt(xp)
        return Double.valueOf(0.2*Math.sqrt(this.getExperience())).intValue();
    }

    @Transient
    public int getNextLevelXp(){
        //formula for level = 0.2 * sqrt(xp)
        int nextLevel = this.getLevel()+1;
        return (int) Math.pow((nextLevel/0.2),2);
    }
    @Transient
    public int getCurrentLevelXp(){
        //formula for level = 0.2 * sqrt(xp)
        int level = this.getLevel();
        return (int) Math.pow((level/0.2),2);
    }
    @Transient
    public int getPercentageNextLevel(){
        //formula for level = 0.2 * sqrt(xp)
        double xpForTheLevel = getNextLevelXp() - getCurrentLevelXp();
        double currXp = this.getExperience() - getCurrentLevelXp();
        //calculate how much xp % we have
        Double percentage= Math.floor((currXp/xpForTheLevel)*100);
        return percentage.intValue();
    }
}

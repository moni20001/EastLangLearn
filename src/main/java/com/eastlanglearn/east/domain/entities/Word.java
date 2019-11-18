package com.eastlanglearn.east.domain.entities;

import javax.persistence.*;

@Entity
@Table(name = "words")
public class Word  {
    private Long id;

    private String word;

    private String en_meaning;

    private String bg_meaning;

    private String difficulty;

    private String pronunciation;

    public Word(){}

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "word", nullable = false)
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Column(name = "en_meaning", nullable = false)
    public String getEnMeaning() {
        return en_meaning;
    }

    public void setEnMeaning(String meaning) {
        this.en_meaning = meaning;
    }

    @Column(name = "bg_meaning", nullable = false)
    public String getBgMeaning() {
        return bg_meaning;
    }

    public void setBgMeaning(String meaning) {
        this.bg_meaning = meaning;
    }

    @Column(name = "diff")
    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Column(name = "pronunciation", nullable = false)
    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}

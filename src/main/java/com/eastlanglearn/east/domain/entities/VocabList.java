package com.eastlanglearn.east.domain.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "vocab_lists")
public class VocabList {
    private Long id;

    private String listTitle;

    private String wordsByIndexes;

    private String language;

    private String lessonText;

    public VocabList() {
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    @Column(name = "words", nullable = false)
    @Type(type="text")
    public String getWordsByIndexes() {
        return wordsByIndexes;
    }

    public void setWordsByIndexes(String wordsByIndexes) {
        this.wordsByIndexes = wordsByIndexes;
    }


    @Column(name = "lang", nullable = false)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Column(name = "lesson", nullable = false)
    @Type(type="text")
    public String getLessonText() {
        return lessonText;
    }

    public void setLessonText(String lessonText) {
        this.lessonText = lessonText;
    }
}

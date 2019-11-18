package com.eastlanglearn.east.domain.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name="readingExercises")
public class ReadingExercise {

    private String id;

    private String storyTitle;

    private String textOriginal;

    private String textTranslated;

    public ReadingExercise() {
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "textOriginal", nullable = false)
    @Type(type="text")
    public String getTextOriginal() {
        return textOriginal;
    }

    public void setTextOriginal(String textOriginal) {
        this.textOriginal = textOriginal;
    }

    @Column(name = "textTranslated", nullable = false)
    @Type(type="text")
    public String getTextTranslated() {
        return textTranslated;
    }

    public void setTextTranslated(String textTranslated) {
        this.textTranslated = textTranslated;
    }

    @Column(name = "storyTitle", nullable = false)
    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }
}

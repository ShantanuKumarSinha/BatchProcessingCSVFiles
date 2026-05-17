package com.shann.springbatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "batch_movie_rating")
@Data
public class MovieRating {

    @Id
    private String constId;

    private Integer yourRating;
    private String dateRated;
    private String title;
    private String url;
    private String titleType;
    private Double imdbRating;
    private Integer runtimeMins;
    private Integer year;
    private String genres;
    private Long numVotes;
    private String releaseDate;
    private String directors;
}

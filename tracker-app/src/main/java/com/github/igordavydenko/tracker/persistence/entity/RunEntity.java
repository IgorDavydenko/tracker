package com.github.igordavydenko.tracker.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "runs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RunEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_date_time", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "finish_date_time")
  private LocalDateTime finishDateTime;

  @Column(name = "start_latitude", nullable = false)
  private Double startLatitude;

  @Column(name = "finish_latitude")
  private Double finishLatitude;

  @Column(name = "start_longitude", nullable = false)
  private Double startLongitude;

  @Column(name = "finish_longitude")
  private Double finishLongitude;

  @Column(name = "distance")
  private Integer distance;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

}
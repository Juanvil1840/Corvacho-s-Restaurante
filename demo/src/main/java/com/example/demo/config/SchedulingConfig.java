package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Activa las tareas programadas (@Scheduled), por ejemplo el "ping" del seguimiento en vivo
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

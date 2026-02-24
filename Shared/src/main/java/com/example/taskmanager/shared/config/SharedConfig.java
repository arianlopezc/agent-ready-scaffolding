package com.example.taskmanager.shared.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the Shared module.
 *
 * <p>Scans for components in the shared package.
 */
@Configuration
@ComponentScan(basePackages = "com.example.taskmanager.shared")
public class SharedConfig {}

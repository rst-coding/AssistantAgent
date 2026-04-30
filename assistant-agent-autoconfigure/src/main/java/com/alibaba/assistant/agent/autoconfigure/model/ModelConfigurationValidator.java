/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.assistant.agent.autoconfigure.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

/**
 * 模型配置验证器
 *
 * <p>验证模型配置并提供友好的错误提示。
 * 实际的 ChatModel Bean 由 Spring AI starter 自动配置创建。
 *
 * @author Assistant Agent Team
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(ModelProperties.class)
public class ModelConfigurationValidator {

    private static final Logger log = LoggerFactory.getLogger(ModelConfigurationValidator.class);

    private final ModelProperties properties;
    private final Environment environment;

    public ModelConfigurationValidator(ModelProperties properties, Environment environment) {
        this.properties = properties;
        this.environment = environment;
    }

    @PostConstruct
    public void validate() {
        boolean hasNewConfig = properties.getPrimary() != null && properties.getPrimary().getProvider() != null;
        boolean hasDashScopeConfig = environment.containsProperty("spring.ai.dashscope.api-key");
        boolean hasOpenAiConfig = environment.containsProperty("spring.ai.openai.api-key");

        if (hasNewConfig) {
            log.info("✅ Using new model configuration format");
            log.info("   Provider: {}", properties.getPrimary().getProvider());
            log.info("   Model: {}", properties.getPrimary().getModel());
            
            if (properties.getEvaluation() != null && properties.getEvaluation().getProvider() != null) {
                log.info("   Evaluation Provider: {}", properties.getEvaluation().getProvider());
                log.info("   Evaluation Model: {}", properties.getEvaluation().getModel());
            }
        } else if (hasDashScopeConfig) {
            log.info("✅ Using DashScope configuration");
            log.info("   Model: qwen-max (default)");
        } else if (hasOpenAiConfig) {
            log.info("✅ Using OpenAI-compatible configuration (DeepSeek/OpenAI/etc.)");
            String baseUrl = environment.getProperty("spring.ai.openai.base-url", "https://api.openai.com/v1");
            String model = environment.getProperty("spring.ai.openai.chat.options.model", "gpt-4o");
            log.info("   Base URL: {}", baseUrl);
            log.info("   Model: {}", model);
        } else {
            log.error("❌ No model configuration found!");
            log.error("   Please configure one of:");
            log.error("   1. assistant.agent.models.primary (new format)");
            log.error("   2. spring.ai.dashscope.api-key (DashScope)");
            log.error("   3. spring.ai.openai.api-key (OpenAI/DeepSeek)");
            throw new IllegalStateException("No model configuration found");
        }
    }
}

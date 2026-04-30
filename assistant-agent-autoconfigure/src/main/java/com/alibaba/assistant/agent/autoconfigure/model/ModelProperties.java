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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模型提供商配置属性
 *
 * <p>支持配置主对话模型和评估模型，可独立选择不同的模型提供商。
 * 支持的提供商包括：dashscope（阿里云）、deepseek（通过 OpenAI 兼容 API）、openai。
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * assistant:
 *   agent:
 *     models:
 *       primary:
 *         provider: deepseek
 *         model: deepseek-chat
 *         api-key: ${DEEPSEEK_API_KEY}
 *         base-url: https://api.deepseek.com/v1
 *         temperature: 0.7
 *       evaluation:
 *         provider: openai
 *         model: gpt-4o-mini
 *         api-key: ${OPENAI_API_KEY}
 * }</pre>
 *
 * @author Assistant Agent Team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "assistant.agent.models")
public class ModelProperties {

    /**
     * 主对话模型配置
     */
    private ModelConfig primary;

    /**
     * 评估模型配置（可选，未配置时使用 primary 模型）
     */
    private ModelConfig evaluation;

    public ModelConfig getPrimary() {
        return primary;
    }

    public void setPrimary(ModelConfig primary) {
        this.primary = primary;
    }

    public ModelConfig getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(ModelConfig evaluation) {
        this.evaluation = evaluation;
    }

    /**
     * 模型配置
     */
    public static class ModelConfig {

        /**
         * 模型提供商：dashscope | deepseek | openai
         */
        private String provider;

        /**
         * 模型名称
         */
        private String model;

        /**
         * API 密钥
         */
        private String apiKey;

        /**
         * API 基础 URL（OpenAI 兼容时需要，如 DeepSeek）
         */
        private String baseUrl;

        /**
         * 温度参数（0.0 - 2.0），控制随机性
         */
        private Double temperature;

        /**
         * Top-p 采样参数（0.0 - 1.0）
         */
        private Double topP;

        /**
         * 最大生成 token 数
         */
        private Integer maxTokens;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Double getTopP() {
            return topP;
        }

        public void setTopP(Double topP) {
            this.topP = topP;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        @Override
        public String toString() {
            return "ModelConfig{" +
                    "provider='" + provider + '\'' +
                    ", model='" + model + '\'' +
                    ", apiKey='[PROTECTED]'" +
                    ", baseUrl='" + baseUrl + '\'' +
                    ", temperature=" + temperature +
                    ", topP=" + topP +
                    ", maxTokens=" + maxTokens +
                    '}';
        }
    }
}

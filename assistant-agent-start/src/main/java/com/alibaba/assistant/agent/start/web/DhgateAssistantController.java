package com.alibaba.assistant.agent.start.web;

import com.alibaba.assistant.agent.autoconfigure.CodeactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assistant")
public class DhgateAssistantController {

	private static final Logger logger = LoggerFactory.getLogger(DhgateAssistantController.class);

	private final CodeactAgent agent;
	private final ObjectMapper objectMapper;

	public DhgateAssistantController(@Qualifier("dhgateAssistantAgent") CodeactAgent agent, ObjectMapper objectMapper) {
		this.agent = agent;
		this.objectMapper = objectMapper;
	}

	@PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> chat(@RequestBody DhgateChatRequest request) {
		long startTime = System.currentTimeMillis();
		String sessionId = request.getSessionId();
		if (sessionId == null || sessionId.isBlank()) {
			sessionId = UUID.randomUUID().toString();
		}

		logger.info("DhgateAssistantController#chat - sessionId={}, message={}",
			sessionId, request.getMessage());

		try {
			String prompt = buildPrompt(request);
			org.springframework.ai.chat.messages.AssistantMessage assistantMessage = agent.call(prompt);
			String raw = assistantMessage != null ? assistantMessage.getText() : null;

			Map<String, Object> response = new LinkedHashMap<>();
			response.put("success", true);

			Map<String, Object> data = new LinkedHashMap<>();
			data.put("sessionId", sessionId);
			data.put("intent", "general_qa");

			// 尝试解析为结构化数据
			Map<String, Object> parsed = tryParseJson(raw);
			if (parsed != null) {
				data.putAll(parsed);
			} else {
				// 兜底：返回文本格式
				Map<String, Object> answer = new LinkedHashMap<>();
				answer.put("type", "text");
				answer.put("content", raw == null ? "抱歉，我暂时无法理解您的问题。" : raw.trim());
				data.put("answer", answer);
			}

			data.put("confidence", 0.8);
			data.put("responseTime", System.currentTimeMillis() - startTime);

			response.put("data", data);
			response.put("timestamp", Instant.now().toString());

			logger.info("DhgateAssistantController#chat - sessionId={}, success=true, responseTime={}ms",
				sessionId, System.currentTimeMillis() - startTime);

			return ResponseEntity.ok(response);

		} catch (GraphRunnerException e) {
			logger.error("DhgateAssistantController#chat - GraphRunnerException: sessionId={}, error={}",
				sessionId, e.getMessage(), e);
			return buildErrorResponse(sessionId, "AI_SERVICE_ERROR",
				"AI 服务暂时不可用，请稍后再试", e.getMessage());

		} catch (Exception e) {
			logger.error("DhgateAssistantController#chat - Exception: sessionId={}, error={}",
				sessionId, e.getMessage(), e);
			return buildErrorResponse(sessionId, "INTERNAL_ERROR",
				"服务器内部错误，请稍后再试", e.getMessage());
		}
	}

	private String buildPrompt(DhgateChatRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("用户问题：\n");
		sb.append(request.getMessage() == null ? "" : request.getMessage());
		sb.append("\n");

		if (request.getLang() != null && !request.getLang().isBlank()) {
			sb.append("\n偏好语言：").append(request.getLang()).append("\n");
		}
		if (request.getDocVersion() != null && !request.getDocVersion().isBlank()) {
			sb.append("\n文档版本：").append(request.getDocVersion()).append("\n");
		}
		if (request.getAllowWeb() != null) {
			sb.append("\n允许联网补充：").append(request.getAllowWeb()).append("\n");
		}
		if (request.getTopK() != null) {
			sb.append("\n检索条数上限：").append(request.getTopK()).append("\n");
		}

		List<DhgateChatMessage> history = request.getHistory();
		if (history != null && !history.isEmpty()) {
			sb.append("\n对话历史（从旧到新）：\n");
			for (DhgateChatMessage m : history) {
				if (m == null) {
					continue;
				}
				String role = m.getRole() == null ? "user" : m.getRole();
				String content = m.getContent() == null ? "" : m.getContent();
				sb.append("- ").append(role).append(": ").append(content).append("\n");
			}
		}

		return sb.toString();
	}

	private Map<String, Object> tryParseJson(String raw) {
		if (raw == null) {
			return null;
		}
		String trimmed = raw.trim();
		if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
			return null;
		}
		try {
			return objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			return null;
		}
	}

	private ResponseEntity<Map<String, Object>> buildErrorResponse(
			String sessionId, String code, String message, String details) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("success", false);

		Map<String, Object> error = new LinkedHashMap<>();
		error.put("code", code);
		error.put("message", message);
		if (details != null && !details.isBlank()) {
			error.put("details", details);
		}

		response.put("error", error);
		response.put("timestamp", Instant.now().toString());

		if (sessionId != null) {
			Map<String, Object> data = new LinkedHashMap<>();
			data.put("sessionId", sessionId);
			response.put("data", data);
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}


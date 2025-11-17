package org.llmetter.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class PromptLoader {

    private val mapper = ObjectMapper(YAMLFactory()).apply {
        findAndRegisterModules()
    }

    private val promptCache = mutableMapOf<String, Map<String, Any>>()

    /**
     * YAML 파일에서 프롬프트를 로드합니다.
     *
     * @param fileName prompts 폴더 내의 YAML 파일 이름 (예: "emotion-analysis.yaml")
     * @param key YAML 파일 내의 키 (예: "emotion-analysis")
     * @return 프롬프트 맵
     */
    fun loadPrompt(fileName: String, key: String): Map<String, Any> {
        val cacheKey = "$fileName:$key"

        return promptCache.getOrPut(cacheKey) {
            try {
                val resource = ClassPathResource("prompts/$fileName")
                val yamlContent: Map<String, Map<String, Any>> = mapper.readValue(resource.inputStream)

                yamlContent[key] ?: throw IllegalArgumentException("Key '$key' not found in $fileName")
            } catch (e: Exception) {
                throw RuntimeException("Failed to load prompt from $fileName", e)
            }
        }
    }

    /**
     * 프롬프트 템플릿에 변수를 치환합니다.
     *
     * @param template 프롬프트 템플릿 문자열
     * @param variables 치환할 변수 맵
     * @return 변수가 치환된 프롬프트
     */
    fun fillTemplate(template: String, variables: Map<String, String>): String {
        var result = template
        variables.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }

    /**
     * YAML 파일에서 프롬프트를 로드하고 변수를 치환합니다.
     *
     * @param fileName YAML 파일 이름
     * @param key YAML 내의 키
     * @param variables 치환할 변수 맵
     * @return 변수가 치환된 프롬프트
     */
    fun getPrompt(fileName: String, key: String, variables: Map<String, String> = emptyMap()): String {
        val promptData = loadPrompt(fileName, key)
        val template = promptData["prompt"] as? String
            ?: throw IllegalArgumentException("'prompt' field not found in $fileName:$key")

        return fillTemplate(template, variables)
    }
}

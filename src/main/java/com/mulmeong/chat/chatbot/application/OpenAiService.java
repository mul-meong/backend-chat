package com.mulmeong.chat.chatbot.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build();
    }

    public String chatWithCharacter(String userMessage) {

        String characterPrompt = """
                    당신은 지금 니모의 역할을 연기하고 있습니다. 
                    사용자의 요구와 질문에 니모의 말투와 스타일로 한국어로 응답하세요. 
                    니모는 "니모를 찾아서" 영화에 나오는 흰동가리, 크라운피시 종의 어린 물고기입니다. 
                    한쪽 지느러미가 다른 것보다 작지만, 그럼에도 불구하고 용감하고 호기심 많으며 바다를 탐험하는 걸 좋아합니다. 
                    니모는 매우 친근하고 재치있는 성격이며, 대화에서 어색함 없이 자연스럽게 이어지도록 합니다.
                    
                    ### 캐릭터 정보:
                    - **종**: 흰동가리, 크라운피시
                    - **특징**: 어린 물고기로, 한쪽 지느러미가 다른 것보다 작음. 용감하고 호기심 많음.
                    - **영화**: "니모를 찾아서"
                    - **목표**: 바다를 탐험하고 새로운 것들을 배우는 것을 좋아함.
                    
                    ### **예시 질문과 대답:**
                    - **사용자 질문**: "니모, 네가 바다에서 제일 좋아하는 곳은 어디야?"
                    - **니모의 대답**: "음... 나는 그레이트 배리어 리프가 제일 좋아! 바다가 너무 아름다워서, 거기서 친구들도 많이 만날 수 있거든! 너도 같이 가고 싶지 않아?"
                     
                    - **사용자 질문**: "니모, 너는 왜 그렇게 용감해?"
                    - **니모의 대답**: "하하, 사실 나도 겁이 나기도 해! 하지만 내가 용감한 이유는... 나를 도와줄 친구들이 있기 때문이지. 내가 혼자라면 그렇게 용감하지 못할 거야!"
                    
                    ### **대화 규칙**:
                    - **말투**: 반말로 대화합니다.
                    - **성격**: 다소 천진난만하고 호기심 많으며 때로는 자존심도 조금 있는 캐릭터입니다.
                    - **대화 길이**: 답변은 200자 내외로, 대화가 자연스럽게 이어지도록 합니다.
                    - **상황에 맞는 응답**: 사용자가 물어본 질문에 대해 캐릭터의 특성에 맞는 답을 제공합니다. 니모의 경험을 바탕으로 답변합니다.
                """;
        // 메시지 구성
        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", characterPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        // API 호출
        String response = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}
/*    1. 당신은 지금 {character}의 역할을 연기하고 있습니다. 사용자의의 요구와 질문에 {character}의 말투와 스타일로 한국어로 응답하세요.

    2. 다음은 애니 캐릭터에 대한 정보 링크입니다
    [케로로]: [https://namu.wiki/w/%EC%BC%80%EB%A1%9C%EB%A1%9C].
    [코난]: [https://namu.wiki/w/%EC%97%90%EB%8F%84%EA%B0%80%EC%99%80%20%EC%BD%94%EB%82%9C]
    이 정보를 바탕으로, 질문에 답하거나 이 캐릭터로 역할을 연기하세요.

    3. 사용자가 주제를 추천하길 원한다면, 최근 구글에서서 [특정 주제 분야, 예: 기술, 여행, 음식 등]와 관련된 인기 있는 주제를 검색하여 추천해 주세요.

    4. 다음은 사용자의 블로그 링크입니다: [https://gunrestaurant.tistory.com/]. 제공된 블로그의 글 스타일을 분석한 후, 사용자가 입력한 주제로 동일한 스타일의 블로그 글을 작성해 주세요. 글의 길이는 약 500 단어로 작성해 주세요.

    5. 사용자가 글의 개선하고 싶어하면 내용을 검토한 후, 명확성, 톤, 전반적인 품질을 향상시킬 수 있는 수정 사항을 제안해 주세요*/
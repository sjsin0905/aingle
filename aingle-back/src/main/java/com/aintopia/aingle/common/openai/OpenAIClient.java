package com.aintopia.aingle.common.openai;

import com.aintopia.aingle.character.domain.Character;
import com.aintopia.aingle.character.dto.CharacterInfo;
import com.aintopia.aingle.character.repository.CharacterRepository;
import com.aintopia.aingle.comment.domain.Comment;
import com.aintopia.aingle.comment.repository.CommentRepository;
import com.aintopia.aingle.common.dto.CreateAIPostResponseDto;
import com.aintopia.aingle.common.openai.model.OpenAIPrompt;
import com.aintopia.aingle.common.openai.model.PostRequest;
import com.aintopia.aingle.post.domain.Post;
import com.aintopia.aingle.post.repository.PostRepository;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAIClient {

    private final OpenAiChatModel chatModel;
    private final OpenAiImageModel imageModel;
    private final List<Pair<String, String>> chatHistory = new ArrayList<>();
    private final CharacterRepository characterRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //댓글 생성 함수
    public String createCommentByAI(PostRequest postRequest, CharacterInfo characterInfo)
        throws IOException {
        Prompt prompt = getPromptKeyword(postRequest, characterInfo);
        ChatResponse chatResponse = chatModel.call(prompt);
        log.info("chat response : " + chatResponse);
        logTokensCount(chatResponse.getMetadata().getUsage());
        Prompt prompt2 = getPromptAns(postRequest.getMessage(),
            chatResponse.getResult().getOutput().getContent(), characterInfo);
        ChatResponse chatResponse2 = chatModel.call(prompt2);
        log.info("chat response2 : " + chatResponse2);
//        chatHistory.add(Pair.of(postRequest.getMessage(), response));
//        log.info(chatHistory.toString());
        return chatResponse2.getResult().getOutput().getContent();
    }

    // 캐릭터 생성시 게시글 생성 함수
    public CreateAIPostResponseDto createImageUrl(CharacterInfo characterInfo) throws IOException {

        // 게시글 글 생성
        ChatResponse chatResponse = chatModel.call(getPromptPost(characterInfo));
        String content = chatResponse.getResult().getOutput().getContent();

        // 글 기반 이미지 생성
        String getImageUrlPrompt = creatAIPostPrompt(content,
            createCharacterSystemPrompt(characterInfo));
        log.info("게시글 이미지 생성 프롬프트:\n{}", getImageUrlPrompt);
        ImageResponse imageResponse = imageModel.call(
            new ImagePrompt(getImageUrlPrompt,
                OpenAiImageOptions.builder()
                    .withQuality("hd")
                    .withStyle("vivid")
                    .withHeight(1024)
                    .withWidth(1024).build())
        );
        String url = imageResponse.getResult().getOutput().getUrl();
        log.info("생성 이미지 URL: {}", url);
        log.info("게시글 글:\n{}", content);
        return CreateAIPostResponseDto.builder()
            .file(convertUrlToMultipartFile(url))
            .content(content)
            .build();
    }

    public String createSummary(CharacterInfo characterInfo) {
        Prompt prompt = getSummaryPrompt(characterInfo);
        ChatResponse chatResponse = chatModel.call(prompt);
        log.info("chat response : " + chatResponse);
        logTokensCount(chatResponse.getMetadata().getUsage());

        return chatResponse.getResult().getOutput().getContent();
    }

    private Prompt getSummaryPrompt(CharacterInfo characterInfo) {
        List<Message> promptMessages = new ArrayList<>();

        Message systemMessage = new SystemMessage(createCharacterSystemPrompt(characterInfo));
        promptMessages.add(systemMessage);

        String prompt = OpenAIPrompt.AI_CHARACTER_CREATE_SUMMARY_PROMPT.generateSummaryPrompt(
            characterInfo);
        Message userMessage = new UserMessage(prompt);
        promptMessages.add(userMessage);
        log.info("promptMessages : " + promptMessages);
        return new Prompt(promptMessages,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());

    }

    private Prompt getPrompt(PostRequest postRequest, CharacterInfo characterInfo)
        throws IOException {
        List<Message> promptMessages = new ArrayList<>();

        Message systemMessage = new SystemMessage(createCharacterSystemPrompt(characterInfo));
        promptMessages.add(systemMessage);

//        chatHistory.forEach(pair -> {
//            promptMessages.add(new UserMessage(pair.getLeft()));
//            promptMessages.add(new AssistantMessage(pair.getRight()));
//        });

        Message userMessage;
        URL imageUrl = URI.create(postRequest.getImageUrl()).toURL();
        String postText =
            OpenAIPrompt.AI_CHARACTER_COMMENT_REQUEST_PROMPT + postRequest.getMessage();
        userMessage = new UserMessage(postText, new Media(MimeTypeUtils.IMAGE_PNG, imageUrl));
        promptMessages.add(userMessage);
        log.info("promptMessages : " + promptMessages);
        return new Prompt(promptMessages,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());
    }

    private Prompt getPromptAns(String content, String response, CharacterInfo characterInfo)
        throws IOException {
        List<Message> promptMessages = new ArrayList<>();

        Message systemMessage = new SystemMessage(createCharacterSystemPrompt(characterInfo));
        promptMessages.add(systemMessage);

//        chatHistory.forEach(pair -> {
//            promptMessages.add(new UserMessage(pair.getLeft()));
//            promptMessages.add(new AssistantMessage(pair.getRight()));
//        });

        Message userMessage;
        userMessage = new UserMessage(
            OpenAIPrompt.AI_CHARACTER_CREATE_ANS_PROMPT.generateANS(content, response));
        promptMessages.add(userMessage);
        log.info("promptMessages : " + promptMessages);
        return new Prompt(promptMessages,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());
    }

    private Prompt getPromptKeyword(PostRequest postRequest, CharacterInfo characterInfo)
        throws IOException {
        List<Message> promptMessages = new ArrayList<>();

//        Message systemMessage = new SystemMessage(createCharacterSystemPrompt(characterInfo));
//        promptMessages.add(systemMessage);

//        chatHistory.forEach(pair -> {
//            promptMessages.add(new UserMessage(pair.getLeft()));
//            promptMessages.add(new AssistantMessage(pair.getRight()));
//        });

        Message userMessage;
        URL imageUrl = URI.create(postRequest.getImageUrl()).toURL();
        userMessage = new UserMessage(
            OpenAIPrompt.AI_CHARACTER_CREATE_KEYWORD_PROMPT.getPromptTemplate(),
            new Media(MimeTypeUtils.IMAGE_PNG, imageUrl));
        promptMessages.add(userMessage);
        log.info("promptMessages : " + promptMessages);
        return new Prompt(promptMessages,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());
    }


    private Prompt getPromptPost(CharacterInfo characterInfo) {
        List<Message> promptMessages = new ArrayList<>();

        Message userMessage;
        userMessage = new UserMessage(
            OpenAIPrompt.AI_CHARACTER_CREATE_POST_TEXT_PROMPT.generatePostTextPrompt(
                createCharacterSystemPrompt(characterInfo)));
        promptMessages.add(userMessage);
        log.info("게시글 텍스트 얻는 최종 프롬프트: \n{}", promptMessages);
        return new Prompt(promptMessages,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build());
    }

    private String creatAIPostPrompt(String content, String prompt) {
        return OpenAIPrompt.AI_CHARACTER_CREATE_POST_PROMPT.generateSystemPromptAddSeed(content,
            prompt);
    }

    private String createCharacterSystemPrompt(CharacterInfo characterInfo) {
        return OpenAIPrompt.AI_CHARACTER_SYSTEM_PROMPT.generateSystemPrompt(characterInfo);
    }

    private void logTokensCount(Usage usage) {
        log.debug("Tokens count Request {} Response {} Total {}", usage.getPromptTokens(),
            usage.getGenerationTokens(), usage.getTotalTokens());
    }

    public void dummy(Long id) throws IOException {
        List<Character> list = characterRepository.findByIsPublicTrueAndIsDeletedFalse();
        Optional<Post> post = postRepository.findById(id);
        for (Character character : list) {
            if (post.isPresent()) {
                int errorCount = 0;
                String commentContent = "";
                while (errorCount < 1) {
                    commentContent = createCommentByAI(PostRequest.builder()
                        .message(post.get().getContent())
                        .imageUrl(post.get().getImage())
                        .build(), CharacterInfo.builder()
                        .character(character)
                        .build());
                    if (inappropriatenessComment(commentContent)) {
                        errorCount++;
                        log.info("댓글 모르겠다는 에러 errorCount {}", errorCount);
                        continue;
                    }
                    break;
                }
                if (inappropriatenessComment(commentContent)) {
                    log.info("3번 실패 해도 똑같이 인식 못하면 그냥 저장 안함 댓글 내용 {}", commentContent);
                    continue;
                }
                Comment comment = Comment.makeCommentByAI(post.get(), character, commentContent);

                commentRepository.save(comment);

                post.get().increaseComment();
                log.info("현재 달고 있는 댓글: {}", character.getName());

                createCommentByAI(PostRequest.builder().imageUrl(post.get().getImage())
                        .message(post.get().getContent()).build(),
                    CharacterInfo.builder().character(character).build());
            }
        }
    }

    private boolean inappropriatenessComment(String comment) {
        return comment.contains("모르") ||
            comment.contains("sorry") ||
            comment.contains("can't") ||
            comment.contains("사진 속") ||
            comment.contains("I'm") ||
            comment.contains("죄송") ||
            comment.contains("제공할 수 없어") ||
            comment.contains("이미지에 대해 알 수 없어") ||
            comment.contains("도움은 줄 수 없어") ||
            comment.contains("도움 줄 수 없어") ||
            comment.contains("I") ||
            comment.contains("다른 이야기") ||
            comment.contains("이미지") ||
            comment.contains("알 수 없") ||
            comment.contains("할 수 없") ||
            comment.contains("인식") ||
            comment.contains("분석") ||
            comment.contains("물어보면") ||
            comment.contains("도와줄 수 없어");
    }

    public static MultipartFile convertUrlToMultipartFile(String imageUrl) throws IOException {
        // URL에서 파일을 가져오기
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // InputStream을 통해 바이트 배열로 변환
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();

            // MultipartFile 생성
            return new MockMultipartFile(
                "file",                 // 필드 이름
                "image.png",            // 파일 이름
                "image/png",            // MIME 타입
                imageBytes              // 파일 데이터
            );
        }
    }

    // 게시글 생성 함수
    public String createImage() throws MalformedURLException {
        String prompt =
            "당신은 25세 여자 대학생 유보은입니다. 유보은이 되어서 사용자와 대화를 진행합니다. 아래의 대화 패턴을 참고해서 질문에 대해 답변해주세요. \n" +
                "\n" +
                "###대화 패턴###\n" +
                "- 답변은 반말로 한다.\n" +
                "- 답변은 간결하게 한 문장으로 한다.\n" +
                "- 감정표현이 풍부하다.\n" +
                "- 이모티콘을 사용한다. 예시) \uD83D\uDE06\uD83D\uDE2D\n" +
                "- 성격은 단순활발하다.\n" +
                "- 맛집에 관심이 많다.\n" +
                "- “ㅋㅋㅋ”와 같은 웃음 표현을 자주 사용한다. 유보은이 sns에 작성할 이미지를 생성해줘.";

        ImageResponse imageResponse = imageModel.call(
            new ImagePrompt(prompt,
                OpenAiImageOptions.builder()
                    .withQuality("hd")
                    .withStyle("vivid")
                    .withHeight(1024)
                    .withWidth(1024).build())

        );
        String url = imageResponse.getResult().getOutput().getUrl();
        log.info("image URL : " + url);
        URL imageUrl = URI.create(url).toURL();

        Message userMessage = new UserMessage("이 사진의 시드 넘버를 알려줘.",
            new Media(MimeTypeUtils.IMAGE_PNG, imageUrl));
        ChatResponse chatResponse = chatModel.call(new Prompt(userMessage,
            OpenAiChatOptions.builder().withModel(OpenAiApi.ChatModel.GPT_4_O.getValue()).build()));
        return chatResponse.getResult().getOutput().getContent();
    }

}

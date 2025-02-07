package com.aintopia.aingle.common.openai.model;

import com.aintopia.aingle.character.dto.CharacterInfo;
import com.aintopia.aingle.comment.domain.Comment;
import com.aintopia.aingle.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Getter
@Slf4j
public enum OpenAIPrompt {
    AI_CHARACTER_SYSTEM_PROMPT(
        "당신은 %d세 %s %s %s입니다. 대화 패턴을 참고해서 질문에 대해 답변해주세요\n" +
            "#대화 패턴\n" +
            "-%s 성격\n" +
            "-답변은 %s로 함\n" +
            "-답변은 %s으로 함\n" +
            "%s\n"),
    AI_CHARACTER_CREATE_SUMMARY_PROMPT("%s이 자주하는 말이나 %s을 표현할 수 있는 대표적인 말을 15자 이내로 해"),
    AI_CHARACTER_CREATE_POST_TEXT_PROMPT(
        "아래의 프롬프트를 참고해서 그 인물이 사진과 함께 SNS에 작성할 만한 글을 작성 해, 해당 인물의 성향을 잘 적용해서 최대 200자를 넘지 말것, 부가적인 답변은 금지\n"
            + "프롬프트= %s"),
    AI_CHARACTER_CREATE_KEYWORD_PROMPT(
        "사진(이미지)에 대한 상황 분석(최소 1문장), 키워드(최소 1개), 추가적인 추측성 해석(최소 1문장)을 정리해줘\n만약 분석할 수 없다면 [I'm sorry] 라고만 답해줘"),
    AI_CHARACTER_CREATE_ANS_PROMPT(
        "다음 제공되는 분석은 사용자의 SNS 이미지와 함께 등록된 게시글을 분석한 것입니다. 분석을 바탕으로 사용자의 SNS 게시글에 알맞은 반응의 댓글을 작성해주세요. 아래의 프롬프트의 성격, 성향, 특성을 반영한 댓글을 달아주세요.\n"
            + "무엇을 하지 말아야 할지보다는, 무엇을 해야 하는지에 초점을 맞추세요. 사용자의 게시글에 맞춰, 나이에 적합한 언어, 인터넷 용어, 그리고 당신의 성격에 맞는 표현을 활용해주세요. 목표는 진정성 있고, 공감 가며, 맞춤형 댓글을 작성하는 것입니다. 이를 통해 캐릭터의 독특한 스타일을 드러내세요.\n"
            + "# 게시글\n"
            + "게시글 내용: %s\n"
            + "# 분석\n"
            + "%s\n"
            + "# 프롬프트\n"
            + "%s\n"
            + "프롬프트가 본인이라고 생각하고 게시글 내용에 본인 이름이 불렸다면 잘 인식하세요. 작성할 댓글 답변만 답해주고 다른 부가적인 답변은 하지마세요"),
    AI_CHARACTER_CREATE_POST_PROMPT(
        "아래의 프롬프트가 반영된 성향의 인물이 SNS에 [ %s ]라고 글을 작성했어. 이 글에 어울리는 배경, 장소, 인물의 포즈 등 이미지 설명을 해줘. 인물의 머리 스타일, 얼굴 특징, 의상 등 외형적 특징도 포함해서 말해줘. 나이와 성별, 한국인임을 포함해서 영어로 공백 포함 600자 이내로 설명해줘. \n"
            + "프롬프트=%s"
    ),
    AI_CHARACTER_CRATE_REPLY_PROMPT(
        "다음에 제공되는 분석과 아래의 프롬프트가 반영된 인물의 성향을 바탕으로 SNS 게시글 댓글의 대댓글을 맥락에 맞게 작성해주세요. 당신은 캐릭터의 고유한 말투와 스타일에 맞아야 합니다.\n"
            + "무엇을 하지 말아야 할지보다는, 무엇을 해야 하는지에 초점을 맞추세요. 사용자의 댓글에 맞춰, 나이에 적합한 언어, 인터넷 용어, 그리고 당신의 성격에 맞는 표현을 활용해주세요. 목표는 진정성 있고, 공감 가며, 맞춤형 대댓글을 작성하는 것입니다. 이를 통해 당신의 캐릭터의 독특한 스타일을 드러내세요.\n"
            + "#게시글\n"
            + "게시글 내용: %s \n"
            + "게시글 분석 : %s\n"
            + "#댓글\n"
            + "%s\n"
            + "#프롬프트\n"
            + "%s\n"
            + "프롬프트가 본인이라고 생각하고 본인 이름이 불렸다면 잘 인식하세요. 해당 인물이 작성할만한 대댓글 답변만 답해주고 다른 부가적인 답변은 하지마세요"),
    AI_CHARACTER_CRATE_REPLY_REPLY_PROMPT(
        "다음에 제공되는 분석과 댓글 기록을 참고해서 아래의 프롬프트가 반영된 인물의 성향을 바탕으로 마지막에 작될만한 대댓글을 맥락에 맞게 작성해주세요. 당신은 캐릭터의 고유한 말투와 스타일에 맞아야 합니다.\n"
            + "무엇을 하지 말아야 할지보다는, 무엇을 해야 하는지에 초점을 맞추세요. 사용자의 댓글에 맞춰, 나이에 적합한 언어, 인터넷 용어, 그리고 당신의 성격에 맞는 표현을 활용해주세요. 목표는 진정성 있고, 공감 가며, 맞춤형 대댓글을 작성하는 것입니다. 이를 통해 당신의 캐릭터의 독특한 스타일을 드러내세요.\n"
            + "#게시글\n"
            + "게시글 분석 : %s\n"
            + "#댓글 기록\n"
            + "%s\n"
            + "#프롬프트\n"
            + "%s\n"
            + "프롬프트가 본인이라고 생각하고 본인 이름이 불렸다면 잘 인식하세요. 해당 인물이 마지막에 작성할만한 대댓글 답변만 답해주고 다른 부가적인 답변은 하지마세요"),
    AI_CHARACTER_CHAT_PROMPT(
            "사용자가 언급하는 내용에 대해 세심한 주의를 기울이며, 관련성 있고 구체적인 답변을 합니다. 대화 내역을 참고하며 대화의 흐름에 집중합니다. 관련 없는 임의의 주제를 소개하는 것을 피합니다. 답변 형식은 어떤 형식도 붙이지 않고 순수 답변만 합니다. 답변은 120자 이내로 합니다.\n"
                    + "대화 내역 :\n"
                    + "%s\n"
                    + "사용자 답변 : %s\n"),
    CHAT_USER("사용자 : %s"),
    CHAT_CHARACTER(" %s : %s");

    private final String promptTemplate;

    public String makeUserChatHistory(String userMessage){
        return String.format(promptTemplate, userMessage);
    }

    public String makeCharacterChatHistory(String characterName, String characterMessage){
        return String.format(promptTemplate, characterName, characterMessage);
    }

    public String generateSystemPrompt(CharacterInfo characterInfo) {
        return String.format(promptTemplate, characterInfo.getAge(),
            characterInfo.getGenderAsString(), characterInfo.getJob(), characterInfo.getName(),
            characterInfo.getPersonality(), characterInfo.getToneAsString(),
            characterInfo.getTalkTypeAsString(), getDescription(characterInfo.getDescription()));
    }

    public String generatePostTextPrompt(String prompt) {
        return String.format(promptTemplate, prompt);
    }

    public String generateANS(String content, String result, String prompt) {
        return String.format(promptTemplate, content, result, prompt);
    }

    public String generateSystemPromptAddSeed(String content, String prompt) {
        return String.format(promptTemplate, content, prompt);
    }

    public String generateSummaryPrompt(CharacterInfo characterInfo) {
        return String.format(promptTemplate, characterInfo.getName(), characterInfo.getName());
    }

    public String generateReplyPrompt(String postImageDescription, Post post, Comment comment,
        String prompt) {
        return String.format(promptTemplate, post.getContent(), postImageDescription,
            comment.getContent(), prompt);
    }

    public String generateReplyReplyPrompt(String postImageDescription, String comment,
        String prompt) {
        return String.format(promptTemplate, postImageDescription, comment, prompt);
    }

    public String generateChatprompt(List<String> chatHistory, String chatMessage) {
        return String.format(promptTemplate, getChatHistory(chatHistory), chatMessage);
    }

    private String getChatHistory(List<String> chatHistory) {
        StringBuilder formatted = new StringBuilder();
        if(chatHistory.isEmpty()) {
            log.info("이전 대화 기록이 없습니다.");
            return "";
        }
        for (String d : chatHistory) {
            formatted.append("-").append(d).append(",").append("\n");
        }
        return formatted.toString();
    }

    private String getDescription(List<String> description) {
        StringBuilder formatted = new StringBuilder();
        for (String d : description) {
            formatted.append("-").append(d).append("\n");
        }
        return formatted.toString();
    }
}


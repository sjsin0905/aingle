package com.aintopia.aingle.vote.controller;

import com.aintopia.aingle.character.dto.response.AllCharacterResponse;
import com.aintopia.aingle.common.util.MemberInfo;
import com.aintopia.aingle.vote.dto.VoteCharacterDetailResponse;
import com.aintopia.aingle.vote.dto.VoteResponse;
import com.aintopia.aingle.vote.dto.request.VoteRegistRequestDto;
import com.aintopia.aingle.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {
    private final VoteService voteService;

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "투표 정보 조회", description = "현재 진행 중인 투표 종료 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 정보 조회에 성공하였습니다!",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Message 로 전달함",
                    content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<?> getVoteInfo(){
        LocalDateTime remainTime = voteService.getVoteInfo();
        return ResponseEntity.ok().body(remainTime);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "투표 후보 캐릭터 리스트 조회", description = "지금 진행 중인 투표에 참가한 캐릭터 후보 리스트 조회 API, 득표수로 정렬")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 후보 캐릭터 리스트 조회에 성공하였습니다!",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Message 로 전달함",
                    content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<AllCharacterResponse> getCharacterByCurrentVote(){
        AllCharacterResponse allCharacterResponse = voteService.getCharacterByVoteId();
        return ResponseEntity.ok().body(allCharacterResponse);
    }

    @GetMapping(value = "/{characterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "투표 후보 캐릭터 특징 상세 조회", description = "투표 후보 캐릭터 특징 상세 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표 후보 캐릭터 특징 상세 조회에 성공하였습니다!",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Message 로 전달함",
                    content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<VoteCharacterDetailResponse> getVoteCharacterDetailById(@PathVariable Long characterId){
        VoteCharacterDetailResponse voteCharacterDetailResponse = voteService.getVoteCharacterDetailById(characterId);
        return ResponseEntity.ok().body(voteCharacterDetailResponse);
    }

    @PostMapping(value = "/{characterId}")
    @Operation(summary = "캐릭터 투표하기", description = "해당 캐릭터에 투표하는 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "투표에 성공하였습니다!",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Message 로 전달함",
                    content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<VoteResponse> voteCharacter(@PathVariable Long characterId){
        Long memberId = MemberInfo.getId();
        VoteResponse voteResponse = voteService.voteCharacter(characterId, memberId);
        return ResponseEntity.ok().body(voteResponse);
    }

    @PostMapping(value = "/character")
    @Operation(summary = "캐릭터 투표 등록", description = "투표 후보에 캐릭터 등록하는 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "캐릭터 등록에 성공하였습니다!",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error Message 로 전달함",
                    content = @Content(mediaType = "application/json")
            ),
    })
    public ResponseEntity<Void> registerCharacterToVote(@RequestBody VoteRegistRequestDto voteRegistRequestDto){
        Long memberId = MemberInfo.getId();
        voteService.registerCharacterToVote(voteRegistRequestDto.getCharacterId(), memberId);
        return ResponseEntity.ok().build();
    }


}

package com.aintopia.aingle.alarm.controller;

import com.aintopia.aingle.alarm.dto.response.AlarmResponseDto;
import com.aintopia.aingle.alarm.service.AlarmService;
import com.aintopia.aingle.common.util.MemberInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/alarms")
@RestController
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping
    @Operation(summary = "알람 조회", description = "알람 조회시 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 조회 성공",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> selectAllAlarms(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        Long memberId = MemberInfo.getId();

        List<AlarmResponseDto> allAlarmList = alarmService.selectAllAlarms(memberId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(allAlarmList);
    }

    @PatchMapping("/{alarmId}")
    @Operation(summary = "알람 읽음 처리", description = "알람 읽음 처리시 사용하는 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "알람 읽음 처리 성공",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> readAlarm(@PathVariable("alarmId") Long alarmId) {
        Long memberId = MemberInfo.getId();

        alarmService.readAlarm(alarmId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body("읽음 처리가 완료되었습니다.");
    }
}

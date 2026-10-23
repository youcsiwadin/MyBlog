package com.chiaya.controller;

import com.chiaya.common.PageResult;
import com.chiaya.common.Result;
import com.chiaya.dto.GuestMessageRequest;
import com.chiaya.pojo.GuestMessage;
import com.chiaya.service.GuestMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 留言板接口（博客站点公开使用）。
 */
@RestController
@RequestMapping("/api/guest-messages")
public class GuestMessageController {

    private final GuestMessageService guestMessageService;

    public GuestMessageController(GuestMessageService guestMessageService) {
        this.guestMessageService = guestMessageService;
    }

    /** 分页查询留言 GET /api/guest-messages?page=1&pageSize=10 */
    @GetMapping
    public Result<PageResult<GuestMessage>> page(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(guestMessageService.page(page, pageSize));
    }

    /** 提交留言 POST /api/guest-messages */
    @PostMapping
    public ResponseEntity<Result<Long>> create(@RequestBody GuestMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Result.success("留言成功", guestMessageService.create(request)));
    }
}

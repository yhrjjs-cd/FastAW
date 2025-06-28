
package com.cdyhrj.cloud.approve.controller;

import com.cdyhrj.cloud.approve.domain.flow.Flow;
import com.cdyhrj.cloud.approve.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 流程设计器
 *
 * @author 黄奇
 */
@RestController
@RequestMapping("/wf/designer")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping("/save-config")
    public void saveConfig(@RequestParam String id, @RequestBody Flow flow) {
        templateService.saveConfig(id, flow);
    }

    @PostMapping("/get-config")
    public Flow getConfig(@RequestParam String id) {
        return templateService.getConfig(id);
    }
}

package com.cdyhrj.cloud.approve.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdName {
    private Long id;
    private String name;

    public static IdName of(Long id, String name) {
        return IdName.builder().id(id).name(name).build();
    }
}

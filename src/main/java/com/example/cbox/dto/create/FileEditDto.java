package com.example.cbox.dto.create;

import com.example.cbox.enumeration.FileAccessType;

public record FileEditDto(
        Long id,
        String link,
        FileAccessType accessType
) {
}

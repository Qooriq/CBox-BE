package com.example.cbox.dto.read;

public record FileGetDto(
        byte[] file,
        String name,
        String type
) {
}

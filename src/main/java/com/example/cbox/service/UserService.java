package com.example.cbox.service;

import com.example.cbox.annotation.TransactionalService;
import com.example.cbox.dto.create.UserCreateEditDto;
import com.example.cbox.dto.read.UserReadDto;
import com.example.cbox.entity.User;
import com.example.cbox.enumeration.UserStatus;
import com.example.cbox.repository.UserRepository;
import com.example.cbox.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@TransactionalService
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public Page<UserReadDto> findAll(Integer page, Integer limit) {
        PageRequest req = PageRequest.of(page - 1, limit);
        return userRepository.findAll(req)
                .map(userMapper::toUserReadDto);
    }

    public Optional<UserReadDto> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toUserReadDto);
    }

    @Transactional
    public UserReadDto create(UserCreateEditDto dto) {
        return Optional.of(dto)
                .map(userMapper::toUser)
                .map(user -> {
                    user.setStatus(UserStatus.ACTIVE);
                    return user;
                })
                .map(userRepository::save)
                .map(userMapper::toUserReadDto)
                .orElseThrow();
    }

    @Transactional
    public Optional<UserReadDto> update(UUID id, UserCreateEditDto dto) {
        return userRepository.findById(id)
                .map(user -> {
                    userMapper.map(user, dto);
                    return user;
                })
                .map(userRepository::saveAndFlush)
                .map(userMapper::toUserReadDto);
    }

    @Transactional
    public boolean delete(UUID id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(UserStatus.DELETED);
                    return true;
                })
                .orElse(false);
    }
}

package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.UserDto;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.model.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractMapper<UserDto, User> {

    public UserMapper() {
        super(UserDto.class, User.class);
    }

    @Override
    public User updateToEntity(UserDto dto, User entity) {
        if (dto.getName()     != null) entity.setName(dto.getName());
        if (dto.getEmail()    != null) entity.setEmail(dto.getEmail());
        if (dto.getPassword() != null) entity.setPassword(dto.getPassword());
        if (dto.getRole()     != null) entity.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        return entity;
    }
}
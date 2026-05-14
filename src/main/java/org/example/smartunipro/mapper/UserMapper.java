package org.example.smartunipro.mapper;

import org.example.smartunipro.dto.UserDto;
import org.example.smartunipro.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends AbstractMapper<UserDto, User> {

    public UserMapper() {
        super(UserDto.class, User.class);
    }

    @Override
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // student fields
        dto.setAcademicNumber(user.getAcademicNumber());
        dto.setLevel(user.getLevel());
        // instructor fields
        dto.setDepartment(user.getDepartment());
        // password intentionally not mapped (WRITE_ONLY)
        return dto;
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setAcademicNumber(dto.getAcademicNumber());
        user.setLevel(dto.getLevel());
        user.setDepartment(dto.getDepartment());
        // password set separately (encoded) in service
        return user;
    }

    @Override
    public User updateToEntity(UserDto dto, User user) {
        if (dto.getName()           != null) user.setName(dto.getName());
        if (dto.getEmail()          != null) user.setEmail(dto.getEmail());
        if (dto.getRole()           != null) user.setRole(dto.getRole());
        if (dto.getAcademicNumber() != null) user.setAcademicNumber(dto.getAcademicNumber());
        if (dto.getLevel()          != null) user.setLevel(dto.getLevel());
        if (dto.getDepartment()     != null) user.setDepartment(dto.getDepartment());
        return user;
    }
}
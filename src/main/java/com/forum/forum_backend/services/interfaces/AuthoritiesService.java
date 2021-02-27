package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.dtos.UserDto;
import com.forum.forum_backend.enums.Permission;
import com.forum.forum_backend.exceptions.BadRequestException;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface AuthoritiesService {
	List<Permission> getAuthorities();
	UserDto assign(PermissionDto permissionDto, int userId) throws NotFoundException, UnauthorizedException, BadRequestException;
}

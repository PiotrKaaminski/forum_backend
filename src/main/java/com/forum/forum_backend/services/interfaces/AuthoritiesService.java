package com.forum.forum_backend.services.interfaces;

import com.forum.forum_backend.dtos.PermissionDto;
import com.forum.forum_backend.exceptions.NotFoundException;
import com.forum.forum_backend.exceptions.UnauthorizedException;

import java.util.List;

public interface AuthoritiesService {
	List<String> getAuthorities();
	void assignPermission(PermissionDto permissionDto, int userId) throws NotFoundException, UnauthorizedException;
}

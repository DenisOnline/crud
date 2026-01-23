package pet.project.shulzhenko.crud.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pet.project.shulzhenko.crud.controller.UserApi;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserControllerImpl implements UserApi {

    private final UserService userService;

    @Override
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<UserShortDtoResponse>> getAllUsers() {
        List<UserShortDtoResponse> allUsers = userService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
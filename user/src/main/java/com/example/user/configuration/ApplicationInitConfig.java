package com.example.user.configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.user.entity.Permission;
import com.example.user.entity.User;
import com.example.user.enums.Roles;
import com.example.user.entity.Role;
import com.example.user.repository.PermissionRepository;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    
    private Permission createOrGetPermission(PermissionRepository permissionRepository, String name, String description) {
        Optional<Permission> permissionOpt = permissionRepository.findByName(name);
        if (permissionOpt.isEmpty()) {
            Permission newPermission = Permission.builder()
                    .name(name)
                    .description(description)
                    .build();
            permissionRepository.save(newPermission);
            log.info("Permission {} created.", name);
            return newPermission;
        }
        return permissionOpt.get();
    }

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        PermissionRepository permissionRepository,
                                        RoleRepository roleRepository){
        return args -> {
            // Tạo hoặc lấy các Permission mặc định
            Permission permUpdateRole = createOrGetPermission(permissionRepository, "UPDATE_ROLE", "Update a role");
            Permission permCreatePermission = createOrGetPermission(permissionRepository, "CREATE_PERMISSION", "Create a permission");
            Permission permReadUsers = createOrGetPermission(permissionRepository, "READ_USERS", "Read users information");
            Permission permDeletePosts = createOrGetPermission(permissionRepository, "DELETE_POSTS", "Delete posts");
            // Thêm các permission khác nếu cần

            // Tập hợp các permissions cho ADMIN
            Set<Permission> adminPermissions = new HashSet<>(Arrays.asList(
                    permUpdateRole,
                    permCreatePermission,
                    permReadUsers,
                    permDeletePosts
            ));

            // Tạo Role ADMIN nếu chưa tồn tại, hoặc cập nhật permissions nếu đã tồn tại
            Optional<Role> adminRoleOptional = roleRepository.findByName(Roles.ADMIN.name());
            Role adminRoleEntity;

            if (adminRoleOptional.isEmpty()) {
                adminRoleEntity = Role.builder()
                        .name(Roles.ADMIN.name())
                        .description("Administrator role with full permissions")
                        .permission(adminPermissions) // Gán tất cả permissions đã định nghĩa cho ADMIN
                        .build();
                roleRepository.save(adminRoleEntity);
                log.warn("Role ADMIN has been created with defined permissions.");
            }else {
              // Hoặc adminRoleEntity được khởi tạo ở đây nếu Optional không rỗng
                adminRoleEntity = adminRoleOptional.get();
                log.info("Role ADMIN already exists. Checking and updating permissions...");
                 // ... (logic cập nhật permissions)
}

            if (userRepository.findByUsername("admin").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Roles.ADMIN.name());

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        // .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}

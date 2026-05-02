package com.microfinance.core_banking.support;

import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtTokenProvider {

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode("Y29yZUJhbmtpbmdUZXN0U2VjcmV0S2V5Rm9ySldUVG9rZW5HZW5lcmF0aW9uVGhpckRUUg==")
    );

    private JwtTokenProvider() {}

    public static String generateToken(Utilisateur utilisateur) {
        Date now = new Date();
        Date expiry = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
            .setSubject(utilisateur.getLogin())
            .claim("id_user", utilisateur.getIdUser())
            .claim("roles", utilisateur.getRoles().stream()
                .map(RoleUtilisateur::getCodeRoleUtilisateur)
                .collect(Collectors.toList()))
            .claim("permissions", utilisateur.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()))
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }

    public static String generateTokenWithRole(String role) {
        Utilisateur user = new Utilisateur();
        user.setLogin("test_" + role.toLowerCase());
        user.setIdUser(1L);
        user.setActif(true);

        RoleUtilisateur roleEntity = new RoleUtilisateur();
        roleEntity.setCodeRoleUtilisateur(role);
        roleEntity.setActif(true);
        user.setRoles(Set.of(roleEntity));

        return generateToken(user);
    }

    public static String generateExpiredToken() {
        Date now = new Date();
        Date past = Date.from(LocalDateTime.now().minusHours(2).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
            .setSubject("expired_user")
            .setIssuedAt(past)
            .setExpiration(past)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }

    public static String generateTokenWithPermissions(String... permissions) {
        Utilisateur user = new Utilisateur();
        user.setLogin("perm_user");
        user.setIdUser(2L);
        user.setActif(true);

        Set<PermissionSecurite> perms = Set.of(permissions).stream().map(p -> {
            PermissionSecurite perm = new PermissionSecurite();
            perm.setCodePermission(p);
            perm.setActif(true);
            return perm;
        }).collect(Collectors.toSet());

        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur("ADMIN");
        role.setActif(true);
        role.setPermissions(perms);
        user.setRoles(Set.of(role));

        return generateToken(user);
    }

    public static Key getSecretKey() {
        return SECRET_KEY;
    }
}

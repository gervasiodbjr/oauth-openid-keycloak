package com.gervasio.fluxo2_webapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController()
public class InfoController {

    private String getMenu(OidcUser user) {
        String name = user != null ? user.getName() : "";
        return "<center><h3><nav>" +
                "  <a href=\"/\">Home</a> | " +
                "  <a href=\"user\">User Page</a> | " +
                "  <a href=\"admin\">Admin Page</a> | " +
                "  <a href=\"logout\">Sair (" + name + ")</a> " +
                "</nav></h3></center>";
    }

    @GetMapping()
    public ResponseEntity<String> Menu(@AuthenticationPrincipal OidcUser user, Model model){
        String content = getMenu(user) +
                "<div style=\"" +
                "  color:blue; font-size:38px; font-weight:bold; display: flex;" +
                "  justify-content: center;" +
                "  align-items: center;" +
                "  height: 200px;" +
                "\">" +
                "Página Inicial" +
                "</div>";
        return ResponseEntity.ok(content);
    }

    @GetMapping("admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getAdimPage(@AuthenticationPrincipal OidcUser user, Model model) {
        String content = getMenu(user) +
                "<div style=\"" +
                "  color:blue; font-size:38px; font-weight:bold; display: flex;" +
                "  justify-content: center;" +
                "  align-items: center;" +
                "  height: 200px;" +
                "\">" +
                "Página do Administrador: " + user.getName() +
                "</div>";
        return ResponseEntity.ok(content);
    }

    @GetMapping("user")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<String> getUserPage(@AuthenticationPrincipal OidcUser user, Model model) {
        String content = getMenu(user) +
                "<div style=\"" +
                "  color:blue; font-size:38px; font-weight:bold; display: flex;" +
                "  justify-content: center;" +
                "  align-items: center;" +
                "  height: 200px;" +
                "\">" +
                "Página do Usuário: " + user.getName() +
                "</div>";
        return ResponseEntity.ok(content);
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal OidcUser user, Model model) {
        String content = getMenu(null);
        return ResponseEntity.ok(content);
    }

    @GetMapping("acessonaoautorizado")
    public ResponseEntity<String> generateAcessoNaoAutorizado(@AuthenticationPrincipal OidcUser user, Model model) {
        String content = getMenu(user) +
                "<div style=\"" +
                "  color:red; font-size:38px; font-weight:bold; display: flex;" +
                "  justify-content: center;" +
                "  align-items: center;" +
                "  height: 200px;" +
                "\">" +
                "Usuário não tem permissão de acesso a esta página!" +
                "</div>";
        return ResponseEntity.ok(content);
    }



}

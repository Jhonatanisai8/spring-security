package com.isai.springsecurity.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFiltre
        //este filtro se ejecutará una sola vez por solicitud.
        extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;


    //Este método se ejecuta en cada solicitud antes de que llegue al controlador.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        //obtengo el header de autorizacion
        final String authorizationHeader = request.getHeader("Authorization");
        final String token;
        String email = null;

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authorizationHeader.substring(7);
        email = jwtService.getUserName(token);

        //verificar si el usuario ya esta autenticado
        if (email != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService
                    .loadUserByUsername(email);

            //validamos el token
            if (jwtService.validateToken(token, userDetails)) {

                //creamos una autenticacion para spring security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        userDetails.getAuthorities());

                //asociamos la autenticacion con la solicitud
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));

                //registramos la autenticacion
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }
    }
}

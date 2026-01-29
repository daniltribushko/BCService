package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.models.dto.DTOMapper;
import ru.tdd.geo.application.models.dto.UserDTO;
import ru.tdd.geo.application.models.exceptions.SimpleRuntimeException;
import ru.tdd.geo.application.utils.URLUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 */
@Service
public class GeoUserDetailsService implements UserDetailsService  {

    @Value("${services.gateway-port}")
    private String gatewayPort;

    @Value("${services.users.host}")
    private String userServiceHost;

    @Value("${services.users.name}")
    private String userServiceName;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build()
        ) {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(
                            URI.create(
                                    URLUtils.builder(userServiceHost + ":" + gatewayPort)
                                            .addPathPart(userServiceName)
                                            .addQueryParameter("username", username, false)
                                            .build()
                            )
                    )
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value())
                return DTOMapper.fromJson(response.body(), UserDTO.class);
            else
                throw new SimpleRuntimeException(response.body());
        } catch (IOException e) {
            throw new SimpleRuntimeException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SimpleRuntimeException(e.getMessage());
        }
    }
}

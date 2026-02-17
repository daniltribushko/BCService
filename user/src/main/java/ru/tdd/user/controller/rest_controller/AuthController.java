package ru.tdd.user.controller.rest_controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tdd.user.application.models.dto.ExceptionDTO;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;
import ru.tdd.user.application.services.AuthService;
import ru.tdd.user.controller.config.OpenApiConfig;

/**
 * @author Tribushko Danil
 * @since 05.02.2026
 * Контроллер для авторизации и регистрации пользователей
 */
@RestController
@RequestMapping("/auth")
@Tag(name = OpenApiConfig.AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @Operation(summary = "Sign Up", description = "Регистрация новых пользователей")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Пользователь успешно зарегестрирован",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtTokenDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Пользователь уже создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "422", description = "Данные не валидны",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<JwtTokenDTO> signUp(@Valid @RequestBody SignUp dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(dto));
    }

    @Operation(summary = "Sign In", description = "Авторизация пользователей")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизовался",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = JwtTokenDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ExceptionDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<JwtTokenDTO> signIn(@Valid @RequestBody SignIn dto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signIn(dto));
    }
}

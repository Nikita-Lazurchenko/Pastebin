package pet.project.ott;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.service.EmailService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OttSuccessHandler implements OneTimeTokenGenerationSuccessHandler{
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException {
        String magicLink = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .path("/login/ott")
                .queryParam("token", oneTimeToken.getTokenValue())
                .toUriString();

        User user = userRepository.loadUserByUsername(oneTimeToken.getUsername()).orElseThrow();

        emailService.sendMagicLink(user.getEmail(), magicLink);

        System.out.println("Link: " + magicLink);

        response.sendRedirect("/login/ott?sent=true");
    }
}

package finalmission.global;

import finalmission.service.JwtService;
import finalmission.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberService memberService;
    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        final Authentication principal = SecurityContextHolder.getContext().getAuthentication();
        final String rawUserId = principal.getName();
        try {
            final long userId = Long.parseLong(rawUserId);
            return new LoginUser(userId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("예기치 못한 예외가 발생했습니다.");
        }
    }
}

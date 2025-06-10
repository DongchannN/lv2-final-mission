package finalmission.global;

import finalmission.domain.Member;
import finalmission.service.JwtService;
import finalmission.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
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
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader("Authorization");
        if (token == null) {
            return null;
        }
        final String substringed = token.substring(7);
        System.out.println("substringed = " + substringed);
        final String phoneNumber = jwtService.resolveToken(substringed);
        System.out.println("phoneNumber = " + phoneNumber);
        final Member member = memberService.findMemberByPhoneNumber(phoneNumber);
        return new LoginMember(member.getId(), "ROLE_USER");
    }
}

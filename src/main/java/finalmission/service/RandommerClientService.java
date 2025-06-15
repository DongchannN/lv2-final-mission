package finalmission.service;

import static finalmission.service.MemberService.RAMDOMMER_HEADER_KEY;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class RandommerClientService {

    private final RestClient randommerClient;

    @Value("${randommer.api-key}")
    private String randommorApiKey;

    public String getRandomNickname() {
        final String[] body = randommerClient.get()
                .uri("/Name?nameType={type}&quantity={quantity}", "fullname", 1)
                .header(RAMDOMMER_HEADER_KEY, randommorApiKey)
                .retrieve()
                .body(String[].class);
        if (body == null) {
            throw new IllegalArgumentException("닉네임 생성 중 에러가 발생하였습니다.");
        }
        return body[0];
    }
}


**`/api/members`**
- 회원가입 (`/signup')✅
- 로그인 (`/signin`)✅

**`/api/members/reservations`**
- 내가 한 예약들 조회 (`/api/members/reservations`)✅
- 내가 한 단일 예약 조회 (`/{reservation_id}`)✅
- 내 예약 취소 (`/{reservation_id}`)✅
- 내 예약 시간 변경 (`/{reservation_id}`)✅

사용자 예약 혹은 수정 시 크레딧에 모자라면 PT를 예약할 수 없다.

**`api/reservations`**
- 예약 현황 조회✅
    - 헬스장, 선생님, 날짜 별 조회
- 예약 기능✅
    - 헬스장, 선생님, 날짜, 시간 굳

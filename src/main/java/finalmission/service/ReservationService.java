package finalmission.service;

import finalmission.controller.dto.ReservationResponse;
import finalmission.controller.dto.ReservationsPreviewResponse;
import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.Trainer;
import finalmission.repository.GymRepository;
import finalmission.repository.ReservationRepository;
import finalmission.repository.TrainerRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final GymRepository gymRepository;
    private final TrainerRepository trainerRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberService memberService,
                              GymRepository gymRepository, TrainerRepository trainerRepository) {
        this.reservationRepository = reservationRepository;
        this.memberService = memberService;
        this.gymRepository = gymRepository;
        this.trainerRepository = trainerRepository;
    }

    public ReservationsPreviewResponse getReservationsByMemberId(Long memberId) {
        final Member member = memberService.findMemberById(memberId);
        final List<Reservation> reservationsByMember = reservationRepository.findReservationsByMember(member);
        return ReservationsPreviewResponse.from(reservationsByMember);
    }

    public ReservationResponse getReservation(Long memberId, Long reservationId) {
        final Reservation reservation = getValidReservation(memberId, reservationId);
        return ReservationResponse.from(reservation);
    }

    public void deleteReservation(Long memberId, Long reservationId) {
        final Reservation reservation = getValidReservation(memberId, reservationId);
        reservationRepository.delete(reservation);
    }

    public void updateReservation(Long memberId, Long reservationId, Long gymId, Long trainerId, LocalDate date, LocalTime time) {
        final Reservation reservation = getValidReservation(memberId, reservationId);
        // TODO : 예약 변경 전 이미 존재하는 예약인지 테스트
        final Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("헬스장이 존재하지 않습니다."));
        final Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("트레이너가 존재하지 않습니다."));
        reservation.update(gym, trainer, date, time);
    }

    private Reservation getValidReservation(Long memberId, Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
        if (!reservation.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("예약에 접근할 수 없습니다.");
        }
        return reservation;
    }
}

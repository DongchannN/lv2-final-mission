package finalmission.service;

import finalmission.controller.dto.ReservationResponse;
import finalmission.controller.dto.ReservationSlotsResponse;
import finalmission.controller.dto.ReservationsPreviewResponse;
import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import finalmission.repository.ReservationRepository;
import finalmission.repository.TrainerScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final TrainerScheduleRepository trainerScheduleRepository;
    private final GymService gymService;
    private final TrainerService trainerService;

    public void addReservation(Long memberId, Long gymId, Long trainerId, LocalDate date, LocalTime time) {
        final Member member = memberService.findMemberById(memberId);
        final Gym gym = gymService.getGymById(gymId);
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        validateReservationInMine(date, time, gym, trainer, member);
        validateReservedReservation(date, time, gym, trainer);
        checkBalance(member, trainer.getCreditPrice());
        final Reservation reservation = new Reservation(gym, member, trainer, date, time);
        reservationRepository.save(reservation);
    }

    private void validateReservationInMine(LocalDate date, LocalTime time, Gym gym, Trainer trainer, Member member) {
        final boolean existsInMyReservations = reservationRepository.existsByGymAndTrainerAndDateAndTimeAndMemberNot(
                gym,
                trainer,
                date,
                time,
                member
        );
        if (existsInMyReservations) {
            throw new IllegalArgumentException("이미 예약한 시간입니다.");
        }
    }

    public ReservationSlotsResponse getReservationSlotsByGymAndTrainerAndDate(Long gymId,
                                                                                 Long trainerId,
                                                                                 LocalDate date) {
        final Gym gym = gymService.getGymById(gymId);
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        final List<LocalTime> schedules = trainerScheduleRepository.findTrainerSchedulesByTrainer(trainer).stream()
                .map(TrainerSchedule::getTime)
                .toList();
        final List<LocalTime> reserved = reservationRepository.findReservationsByGymAndTrainerAndDate(gym, trainer, date).stream()
                .map(Reservation::getTime)
                .toList();
        return ReservationSlotsResponse.from(gym, trainer, schedules, reserved);
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
        final Member member = memberService.findMemberById(memberId);
        final Reservation reservation = getValidReservation(memberId, reservationId);
        final Trainer beforeTrainer = reservation.getTrainer();
        final Gym gym = gymService.getGymById(gymId);
        final Trainer trainer = trainerService.getTrainerById(trainerId);
        final int creditDifference = trainer.getCreditPrice() - beforeTrainer.getCreditPrice();
        checkBalance(member, creditDifference);
        validateReservedReservation(date, time, gym, trainer);
        reservation.update(gym, trainer, date, time);
    }

    private static void checkBalance(Member member, int creditDifference) {
        if (member.getCreditAmount() < creditDifference) {
            throw new IllegalArgumentException("잔고가 없습니다.");
        }
        member.decreaseCredit(creditDifference);
    }

    private void validateReservedReservation(LocalDate date, LocalTime time, Gym gym, Trainer trainer) {
        final boolean existsReservation = reservationRepository.existsByGymAndTrainerAndDateAndTime(
                gym,
                trainer,
                date,
                time
        );
        if (existsReservation) {
            throw new IllegalArgumentException("이미 예약된 시간입니다.");
        }
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

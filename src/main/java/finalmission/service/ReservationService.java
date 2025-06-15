package finalmission.service;

import finalmission.controller.dto.ReservationResponse;
import finalmission.controller.dto.ReservationSlotsResponse;
import finalmission.controller.dto.ReservationsPreviewResponse;
import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.ReservationStatus;
import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import finalmission.repository.ReservationRepository;
import finalmission.repository.TrainerScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberService memberService;
    private final TrainerScheduleRepository trainerScheduleRepository;
    private final GymService gymService;
    private final TrainerService trainerService;

    @Transactional
    public Long addReservation(Long memberId, Long gymId, Long trainerId, LocalDate date, LocalTime time) {
        final Member member = memberService.findMemberById(memberId);
        final Gym gym = gymService.getGymById(gymId);
        member.validateMemberGym(gym);
        final Trainer trainer = trainerService.getTrainerById(trainerId);

        final List<Reservation> reservations = reservationRepository.findReservationsByGymAndTrainerAndDateAndTime(gym, trainer, date, time);
        validateAlreadyReserved(memberId, reservations);

        checkBalance(member, trainer.getCreditPrice());
        return reservations.isEmpty() ?
                addAcceptReservation(date, time, member, trainer, gym)
                : addPendingReservation(date, time, member, trainer, gym);
    }

    private Long addAcceptReservation(LocalDate date, LocalTime time, Member member, Trainer trainer, Gym gym) {
        member.decreaseCredit(trainer.getCreditPrice());
        final Reservation reservation = Reservation.createAcceptedReservation(gym, member, trainer, date, time);
        return reservationRepository.save(reservation).getId();
    }

    private Long addPendingReservation(LocalDate date, LocalTime time, Member member, Trainer trainer, Gym gym) {
        final Reservation reservation = Reservation.createPendingReservation(gym, member, trainer, date, time);
        return reservationRepository.save(reservation).getId();
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

    public ReservationsPreviewResponse getReservationsByMemberId(Long memberId, Pageable pageable) {
        final Member member = memberService.findMemberById(memberId);
        final List<Reservation> reservationsByMember = reservationRepository.findReservationsByMember(member, pageable);
        return ReservationsPreviewResponse.from(reservationsByMember);
    }

    public ReservationResponse getReservation(Long memberId, Long reservationId) {
        final Reservation reservation = getValidReservation(memberId, reservationId);
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void deleteReservation(Long memberId, Long reservationId) {
        final Reservation reservation = getValidReservation(memberId, reservationId);
        reservationRepository.delete(reservation);
        if (reservation.getStatus() == ReservationStatus.ACCEPTED) {
            acceptTopReservation(reservation);
        }
    }

    private void acceptTopReservation(Reservation reservation) {
        final Reservation topPendingReservation = reservationRepository.findFirstByGymAndTrainerAndDateAndTimeOrderById(
                reservation.getGym(), reservation.getTrainer(), reservation.getDate(), reservation.getTime()
        ).orElse(null);
        if (topPendingReservation != null) {
            topPendingReservation.accept();
            topPendingReservation.getMember().decreaseCredit(topPendingReservation.getTrainer().getCreditPrice());
        }
    }

    @Transactional
    public void updateReservation(Long memberId, Long reservationId, Long gymId, Long trainerId, LocalDate date, LocalTime time) {
        final Member member = memberService.findMemberById(memberId);
        final Gym gym = gymService.getGymById(gymId);
        member.validateMemberGym(gym);
        final Reservation reservation = getValidReservation(memberId, reservationId);
        final Trainer trainer = trainerService.getTrainerById(trainerId);

        final List<Reservation> reservations = reservationRepository.findReservationsByGymAndTrainerAndDateAndTime(gym, trainer, date, time);
        validateAlreadyReserved(memberId, reservations);

        final int creditDifference = reservation.calculateTrainerCreditDifference(trainer);;
        checkBalance(member, creditDifference);

        if (reservations.isEmpty()) {
            member.decreaseCredit(creditDifference);
            reservation.update(gym, trainer, date, time, ReservationStatus.ACCEPTED);
            return ;
        }
        reservation.update(gym, trainer, date, time, ReservationStatus.PENDING);
    }

    private void validateAlreadyReserved(Long memberId, List<Reservation> reservations) {
        final boolean alreadyReserved = reservations.stream()
                .anyMatch(r -> r.getMember().getId().equals(memberId));
        if (alreadyReserved) {
            throw new IllegalArgumentException("이미 예약한 시간입니다.");
        }
    }

    private void checkBalance(Member member, int creditDifference) {
        if (member.getCreditAmount() < creditDifference) {
            throw new IllegalArgumentException("잔고가 없습니다.");
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

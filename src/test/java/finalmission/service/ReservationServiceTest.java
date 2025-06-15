package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import finalmission.controller.dto.ReservationResponse;
import finalmission.controller.dto.ReservationSlotsResponse;
import finalmission.controller.dto.ReservationSlotsResponse.ReservationSlot;
import finalmission.controller.dto.ReservationsPreviewResponse;
import finalmission.domain.Gym;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.domain.ReservationStatus;
import finalmission.domain.Trainer;
import finalmission.domain.TrainerSchedule;
import finalmission.repository.GymRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.ReservationRepository;
import finalmission.repository.TrainerRepository;
import finalmission.repository.TrainerScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainerScheduleRepository trainerScheduleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    private LocalDate tommorrow;

    private Gym gym;

    private Trainer trainer;
    private Trainer cheapTrainer;
    private Trainer expensiveTrainer;

    private Member member1;
    private Member member2;

    private TrainerSchedule schedule1;
    private TrainerSchedule schedule2;
    private TrainerSchedule schedule3;
    private TrainerSchedule cheapTrainerSchedule1;
    private TrainerSchedule cheapTrainerSchedule2;
    private TrainerSchedule cheapTrainerSchedule3;
    private TrainerSchedule expensiveTrainerSchedule1;
    private TrainerSchedule expensiveTrainerSchedule2;
    private TrainerSchedule expensiveTrainerSchedule3;

    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;

    @BeforeEach
    void setUp() {
        final LocalTime now = LocalTime.now();
        LocalTime time1 = LocalTime.of(now.getHour(), now.getMinute()).plusHours(1);
        LocalTime time2 = LocalTime.of(now.getHour(), now.getMinute()).plusHours(2);
        LocalTime time3 = LocalTime.of(now.getHour(), now.getMinute()).plusHours(3);
        gym = gymRepository.save(new Gym("gym1", "location1", "01099999999"));
        trainer = trainerRepository.save(new Trainer("trainer1", 100, "description1", "image1", gym));
        cheapTrainer = trainerRepository.save(new Trainer("trainer2", 50, "description2", "image2", gym));
        expensiveTrainer = trainerRepository.save(new Trainer("trainer3", 5000, "description3", "image3", gym));
        member1 = memberRepository.save(new Member("member1", "nickname1", "01011111111", "1234", 1000, gym));
        member2 = memberRepository.save(new Member("member2", "nickname2", "01022222222", "1234", 1000, gym));
        schedule1 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time1));
        schedule2 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time2));
        schedule3 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time3));
        cheapTrainerSchedule1 = trainerScheduleRepository.save(new TrainerSchedule(cheapTrainer, time1));
        cheapTrainerSchedule2 = trainerScheduleRepository.save(new TrainerSchedule(cheapTrainer, time2));
        cheapTrainerSchedule3 = trainerScheduleRepository.save(new TrainerSchedule(cheapTrainer, time3));
        expensiveTrainerSchedule1 = trainerScheduleRepository.save(new TrainerSchedule(expensiveTrainer, time1));
        expensiveTrainerSchedule2 = trainerScheduleRepository.save(new TrainerSchedule(expensiveTrainer, time2));
        expensiveTrainerSchedule3 = trainerScheduleRepository.save(new TrainerSchedule(expensiveTrainer, time3));

        tommorrow = LocalDate.now().plusDays(1);
        reservation1 = reservationRepository.save(
                new Reservation(gym, member1, trainer, tommorrow, time1, ReservationStatus.ACCEPTED)
        );
        reservation2 = reservationRepository.save(
                new Reservation(gym, member1, trainer, tommorrow, time3, ReservationStatus.ACCEPTED)
        );
        reservation3 = reservationRepository.save(
                new Reservation(gym, member2, trainer, tommorrow, time1, ReservationStatus.PENDING)
        );
    }

    @Test
    @DisplayName("예약 조회 시 예약 슬롯들을 반환한다")
    void getReservationSlotsByGymAndTrainerAndDateTest() {
        // when
        final ReservationSlotsResponse slots = reservationService.getReservationSlotsByGymAndTrainerAndDate(
                gym.getId(), trainer.getId(), LocalDate.now().plusDays(1)
        );

        // then
        final List<ReservationSlot> reservations = slots.reservations();
        assertThat(reservations).hasSize(3);
        assertThat(
                reservations.stream()
                        .filter(reservationSlot -> reservationSlot.startAt().equals(schedule1.getTime()))
                        .findFirst()
                        .get()
                        .waitCount()
        ).isEqualTo(2);
        assertThat(
                reservations.stream()
                        .filter(reservationSlot -> reservationSlot.startAt().equals(schedule2.getTime()))
                        .findFirst()
                        .get()
                        .waitCount()
        ).isEqualTo(0);
        assertThat(
                reservations.stream()
                        .filter(reservationSlot -> reservationSlot.startAt().equals(schedule3.getTime()))
                        .findFirst()
                        .get()
                        .waitCount()
        ).isEqualTo(1);
    }

    @Test
    @DisplayName("예약이 존재하지 않다면 바로 예약이 된다.")
    void addReservationAcceptedTest() {
        // when
        final Long id = reservationService.addReservation(
                member1.getId(), gym.getId(), trainer.getId(), tommorrow, schedule2.getTime()
        );

        // then
        final Optional<Reservation> reservation = reservationRepository.findById(id);
        assertThat(reservation).isPresent();
        assertThat(reservation.get().getStatus()).isEqualTo(ReservationStatus.ACCEPTED);
        final Member member = memberRepository.findById(member1.getId()).orElseThrow();
        assertThat(member.getCreditAmount()).isEqualTo(900);
    }

    @Test
    @DisplayName("예약이 존재한다면 대기가 된다.")
    void addReservationPendingTest() {
        // when
        final Long id = reservationService.addReservation(
                member2.getId(), gym.getId(), trainer.getId(), tommorrow, schedule3.getTime()
        );

        // then
        final Optional<Reservation> reservation = reservationRepository.findById(id);
        assertThat(reservation).isPresent();
        assertThat(reservation.get().getStatus()).isEqualTo(ReservationStatus.PENDING);
        final Member member = memberRepository.findById(member2.getId()).orElseThrow();
        assertThat(member.getCreditAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("예약 삭제 시 다음 예약이 있다면 승급한다.")
    void deleteReservationAndAcceptNextReservationTest() {
        // when
        reservationService.deleteReservation(member1.getId(), reservation1.getId());

        // then
        final Reservation reservation = reservationRepository.findById(reservation3.getId()).orElseThrow();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACCEPTED);
    }

    @Test
    @DisplayName("내 예약을 조회 할 수 있다")
    void getMyReservationsTest() {
        // given
        PageRequest first = PageRequest.of(0, 1);
        PageRequest second = PageRequest.of(1, 1);

        // when
        final ReservationsPreviewResponse firstPage = reservationService.getReservationsByMemberId(member1.getId(), first);
        final ReservationsPreviewResponse secondPage = reservationService.getReservationsByMemberId(member1.getId(), second);

        // then
        assertThat(firstPage.reservations()).hasSize(1);
        assertThat(secondPage.reservations()).hasSize(1);
        assertThat(firstPage.reservations().getFirst().reservationId()).isEqualTo(reservation1.getId());
        assertThat(secondPage.reservations().getFirst().reservationId()).isEqualTo(reservation2.getId());
    }

    @Test
    @DisplayName("내 예약을 상세 조회할 수 있다.")
    void getMyReservationTest() {
        // given
        Long memberId = member1.getId();
        Long reservationId = reservation1.getId();

        // when
        final ReservationResponse reservation = reservationService.getReservation(memberId, reservationId);

        // then
        assertThat(reservation.date()).isEqualTo(reservation1.getDate());
        assertThat(reservation.time()).isEqualTo(reservation1.getTime());
        assertThat(reservation.gymName()).isEqualTo(reservation1.getGym().getName());
        assertThat(reservation.trainerName()).isEqualTo(reservation1.getTrainer().getName());
    }

    @Test
    @DisplayName("내 예약이 아니라면 예외가 발생한다.")
    void getOtherReservationTest() {
        // given
        Long memberId = member1.getId();
        Long reservationId = reservation3.getId();

        // when, then
        assertThatThrownBy(() -> reservationService.getReservation(memberId, reservationId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 수정 시 대기가 없다면 바로 등록하고 차액을 결재한다.")
    void updateReservationAcceptedTest() {
        // given
        Long memberId = member1.getId();
        Long reservationId = reservation1.getId();

        // when
        reservationService.updateReservation(memberId, reservationId, gym.getId(), cheapTrainer.getId(), tommorrow, cheapTrainerSchedule1.getTime());

        // then
        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        assertThat(reservation.getTrainer().getId()).isEqualTo(cheapTrainer.getId());
        assertThat(reservation.getTime()).isEqualTo(cheapTrainerSchedule1.getTime());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACCEPTED);
        assertThat(reservation.getMember().getCreditAmount()).isEqualTo(1050);
    }

    @Test
    @DisplayName("예약 수정 시 대기가 있다면 대기를 등록한다 차액을 결재한다.")
    void updateReservationPendingTest() {
        // given
        Long memberId = member2.getId();
        Long reservationId = reservation3.getId();

        final LocalTime time = cheapTrainerSchedule1.getTime();
        final Long trainerId = cheapTrainer.getId();
        reservationService.updateReservation(member1.getId(), reservation1.getId(), gym.getId(), trainerId, tommorrow, time);

        // when
        reservationService.updateReservation(memberId, reservationId, gym.getId(), trainerId, tommorrow, time);

        // then
        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        assertThat(reservation.getTrainer().getId()).isEqualTo(trainerId);
        assertThat(reservation.getTime()).isEqualTo(time);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getMember().getCreditAmount()).isEqualTo(1000);
    }

    @Test
    @DisplayName("잔고가 부족하면 예약 변경에 실패한다.")
    void updateReservationExpensiveTest() {
        // given
        Long memberId = member1.getId();
        Long reservationId = reservation1.getId();

        // when, then
        assertThatThrownBy(() -> reservationService.updateReservation(memberId, reservationId, gym.getId(), expensiveTrainer.getId(), tommorrow, expensiveTrainerSchedule1.getTime()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

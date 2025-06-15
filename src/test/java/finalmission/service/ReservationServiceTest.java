package finalmission.service;

import static org.assertj.core.api.Assertions.assertThat;

import finalmission.controller.dto.ReservationSlotsResponse;
import finalmission.controller.dto.ReservationSlotsResponse.ReservationSlot;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    private Gym gym;
    private Trainer trainer;
    private Member member1;
    private Member member2;
    private TrainerSchedule schedule1;
    private TrainerSchedule schedule2;
    private TrainerSchedule schedule3;
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
        member1 = memberRepository.save(new Member("member1", "nickname1", "01011111111", "1234", 1000, gym));
        member2 = memberRepository.save(new Member("member2", "nickname2", "01022222222", "1234", 1000, gym));
        schedule1 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time1));
        schedule2 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time2));
        schedule3 = trainerScheduleRepository.save(new TrainerSchedule(trainer, time3));

        reservation1 = reservationRepository.save(
                new Reservation(gym, member1, trainer, LocalDate.now().plusDays(1), time1, ReservationStatus.ACCEPTED)
        );
        reservation2 = reservationRepository.save(
                new Reservation(gym, member1, trainer, LocalDate.now().plusDays(1), time3, ReservationStatus.ACCEPTED)
        );
        reservation3 = reservationRepository.save(
                new Reservation(gym, member2, trainer, LocalDate.now().plusDays(1), time1, ReservationStatus.PENDING)
        );
    }

    @Test
    @DisplayName("예약 조회 시 예약 슬롯들을 반환한다")
    void getReservationSlotsByGymAndTrainerAndDateTest() {
        // given


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
}

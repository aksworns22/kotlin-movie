import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.CinemaSchedule
import model.schedule.MovieScreening
import model.schedule.ScreenSchedule
import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.seat.SeatRow
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime

fun main() {
    CinemaController(
        moviePaymentController = MoviePaymentController(),
        movieReservationController = creatMovieReservationController(),
    ).run()
}

private fun creatMovieReservationController(): MovieReservationController {
    val screenSchedules =
        listOf(
            ScreenSchedule(
                screenId = "1",
                servicePeriod =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 6, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 23, 0)),
                    ),
                movieScreenings =
                    listOf(
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 0)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 9, 7, 0)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 9, 8, 0)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 10)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 10)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "F4 꽃보다 남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 16, 30)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 17, 30)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                    ),
            ),
            ScreenSchedule(
                screenId = "2",
                servicePeriod =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 6, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 23, 0)),
                    ),
                movieScreenings =
                    listOf(
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "F4 꽃보다 남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 30)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 30)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                        MovieScreening(
                            movie =
                                Movie(
                                    name = MovieName(name = "혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 15, 20)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 16, 20)),
                                ),
                            seatGroup = createSeatGroup(),
                        ),
                    ),
            ),
        )

    return MovieReservationController(
        CinemaSchedule(
            screenSchedules = screenSchedules,
        ),
    )
}

private fun createSeatGroup(
    seats: List<Seat> =
        listOf(
            Seat(
                SeatPosition(
                    row = SeatRow("B"),
                    column = SeatColumn(2),
                ),
                grade = SeatGrade.S,
            ),
            Seat(
                SeatPosition(
                    row = SeatRow("B"),
                    column = SeatColumn(1),
                ),
                grade = SeatGrade.B,
            ),
            Seat(
                SeatPosition(
                    row = SeatRow("A"),
                    column = SeatColumn(2),
                ),
                grade = SeatGrade.B,
            ),
            Seat(
                SeatPosition(
                    row = SeatRow("A"),
                    column = SeatColumn(1),
                ),
                grade = SeatGrade.S,
            ),
        ),
): SeatGroup = SeatGroup(seats)

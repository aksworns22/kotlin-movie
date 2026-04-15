package model.schedule

import model.movie.MovieName

class CinemaSchedule(
    screenSchedules: List<ScreenSchedule>,
) {
    private val screenSchedules = screenSchedules.toList()

    init {
        require(screenSchedules.distinct().size == screenSchedules.size) {
            "동일한 상영관의 일정은 중복해서 존재할 수 없습니다"
        }
    }

    operator fun get(movieName: MovieName): MovieSchedule =
        MovieSchedule(
            movieName = movieName,
            movieScreenings =
                screenSchedules.flatMap { screenSchedule ->
                    screenSchedule[movieName]
                },
        )
}

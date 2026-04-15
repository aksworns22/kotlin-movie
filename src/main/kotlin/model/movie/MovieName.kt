package model.movie

@JvmInline
value class MovieName(
    private val name: String,
) {
    val value: String get() = name
}

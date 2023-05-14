package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.music.model.Music
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertMusicRepository : JpaRepository<ConcertMusic, Long?> {
    fun existsByConcertIdAndMusicId(concertId: Long, musicId: Long): Boolean

    @Query(
        """
        SELECT music FROM ConcertMusic AS cm JOIN Music AS music ON cm.music.id = music.id
            WHERE cm.concert.id = :concertId
    """,
    )
    fun getMusicListByConcertId(@Param("concertId") concertId: Long): List<Music>
}
